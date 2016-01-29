/*******************************************************************************
 * (c) Crown owned copyright 2015 (UK Ministry of Defence)
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
 * International License
 *
 * This is to identify the UK Ministry of Defence as owners along with the license rights provided. The
 * URL of the CC BY NC SA 4.0 International License is 
 * http://creativecommons.org/licenses/by-nc-sa/4.0/legalcode (Accessed 02-NOV-15).
 *  
 * Contributors:
 *   University of Southampton - Initial API and implementation
 *******************************************************************************/
package ac.soton.coda.internal.simulator2.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.emf.core.EventBElement;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.persistence.EMFRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import ac.soton.coda.internal.simulator2.ISimulationManager;
import ac.soton.coda.internal.simulator2.SimulationManager;
import ac.soton.coda.internal.simulator2.perspectives.Sim2Perspective;
import ac.soton.coda.internal.simulator2.utils.Utils;
import ac.soton.eventb.statemachines.Statemachine;
import ac.soton.eventb.statemachines.animation2.AnimationState;
import ac.soton.eventb.statemachines.animation2.DiagramAnimator;
import ac.soton.eventb.statemachines.diagram.part.StatemachinesDiagramEditor;
import ac.soton.prob2.utils.ProB2Utils;
import de.prob.exception.ProBError;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.Trace;

/**
 * @author htson
 *
 */
public class SimulateHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (Utils.DEBUG)
			Utils.debug("Simulate 2");

		// Get the current selection
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);

		// Get the selected machine
		IMachineRoot mchRoot = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IMachineRoot) {
					mchRoot = (IMachineRoot) obj;
				}
			}
		}

		// Return if the current selection is not a machine root.
		if (mchRoot == null)
			return null;

		// Try to launch State machine animation 2
		// if (DiagramAnimator.getAnimator().isRunning())
		// return null;
		List<IFile> bmsFiles = new ArrayList<IFile>();

		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil
				.getActiveWorkbenchWindow(event);
		List<Statemachine> stateMachines = getOpenStateMachines(
				activeWorkbenchWindow, mchRoot);

		Trace trace;
		try {
			trace = ProB2Utils.createTrace(mchRoot);
		} catch (ProBError e1) {
			Utils.handleException(e1, "ProB was not able to load the model.");
			return null;
		} catch (ModelTranslationError e1) {
			Utils.handleException(e1, "Was not able to translate the model.");
			return null;
		} catch (IOException e1) {
			Utils.handleException(
					e1,
					"Loading of the model failed."
							+ " Please check to make sure that the Rodin static checker has "
							+ "produced a valid static checked file (.bcc or .bcm)."
							+ " If not, try cleaning the project.");
			return null;
		}

		EMFRodinDB emfRodinDB = new EMFRodinDB();
		Machine machine = (Machine) emfRodinDB.loadEventBComponent(mchRoot);

		if (stateMachines.size() != 0) {
			DiagramAnimator diagramAnimator = DiagramAnimator.getAnimator();
			if (diagramAnimator.start(machine, trace, stateMachines, bmsFiles)) {
				// set animation service state to active
				ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil
						.getActiveWorkbenchWindow(event).getService(
								ISourceProviderService.class);
				AnimationState animationStateService = (AnimationState) sourceProviderService
						.getSourceProvider(AnimationState.STATE);
				animationStateService.setActive(true);
			}
		}
		// ///////////

		// Create a new CODA simulation using the simulation manager.
		ISimulationManager simMan = SimulationManager.getDefault();
		simMan.newSimulation(mchRoot, trace);
		ProB2Utils.ProB2NewAnimation(trace);

		// Switch to CODA simulation perspective.
		final IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective(Sim2Perspective.PERSPECTIVE_ID,
					activeWorkbenchWindow);
		} catch (WorkbenchException e) {
			// ignore exceptions
		}

		return null;
	}

	/**
	 * @param mch
	 * @return
	 */
	private List<Statemachine> getOpenStateMachines(
			IWorkbenchWindow activeWorkbenchWindow, IMachineRoot mch) {
		List<Statemachine> stateMachines = new ArrayList<Statemachine>();
		List<StatemachinesDiagramEditor> editors = new ArrayList<StatemachinesDiagramEditor>();
		// Find all the statemachines of the machine
		// (these must come from the editors as each editor has a different
		// local copy)

		for (IWorkbenchPage page : activeWorkbenchWindow.getPages()) {
			for (IEditorReference editorRef : page.getEditorReferences()) {
				IEditorPart editor = editorRef.getEditor(true);
				if (editor instanceof StatemachinesDiagramEditor) {
					Statemachine statemachine = (Statemachine) ((StatemachinesDiagramEditor) editor)
							.getDiagram().getElement();
					if (mch.equals(getEventBRoot(statemachine))) {

						if (editor.isDirty()) {
							editor.doSave(new NullProgressMonitor());
						}
						stateMachines.add(statemachine);

						// let the editor know that we are animating so that it
						// doesn't try to save animation artifacts
						((StatemachinesDiagramEditor) editor).startAnimating();
						editors.add((StatemachinesDiagramEditor) editor);
					}
				}
			}
		}

		return stateMachines;
	}

	public static IEventBRoot getEventBRoot(EventBElement element) {
		Resource resource = element.eResource();
		if (resource != null && resource.isLoaded()) {
			IFile file = WorkspaceSynchronizer.getFile(resource);
			IRodinProject rodinProject = RodinCore.getRodinDB()
					.getRodinProject(file.getProject().getName());
			IEventBRoot root = (IEventBRoot) rodinProject.getRodinFile(
					file.getName()).getRoot();
			return root;
		}
		return null;
	}

}
