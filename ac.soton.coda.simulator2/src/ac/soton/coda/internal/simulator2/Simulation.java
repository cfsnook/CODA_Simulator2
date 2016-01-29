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

package ac.soton.coda.internal.simulator2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eventb.core.IMachineRoot;
import org.eventb.emf.core.AbstractExtension;
import org.eventb.emf.core.EventBNamed;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.Variable;
import org.eventb.emf.persistence.EMFRodinDB;

import ac.soton.coda.internal.simulator2.utils.Messages;
import ac.soton.coda.internal.simulator2.utils.Utils;
import ac.soton.coda.simulator2.Simulator2Plugin;
import ac.soton.eventb.emf.components.Component;
import ac.soton.eventb.emf.components.Connector;
import ac.soton.eventb.emf.components.WakeQueue;
import ac.soton.eventb.emf.oracle.Entry;
import ac.soton.eventb.emf.oracle.Oracle;
import ac.soton.eventb.emf.oracle.OracleFactory;
import ac.soton.eventb.emf.oracle.OraclePackage;
import ac.soton.eventb.emf.oracle.Run;
import ac.soton.eventb.emf.oracle.Snapshot;
import ac.soton.eventb.emf.oracle.Step;
import ac.soton.eventb.emf.oracle.util.OracleUtils;
import ac.soton.eventb.statemachines.Statemachine;
import ac.soton.eventb.statemachines.StatemachinesPackage;
import ac.soton.prob2.utils.ProB2Utils;
import de.prob.exception.ProBError;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.State;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

/**
 * <p>
 * An implementation of {@link ISimulation}. Each CODA simulation corresponds to
 * a ProB2 trace.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see Trace
 * @since 0.1
 */
public class Simulation implements ISimulation {

	// The current ProB2 animation trace.
	private Trace trace;

	// The machine corresponding to the trace.
	private Machine emfMch;

	// The machine root corresponding to the trace.
	private IMachineRoot mch;

	// The current simulation mode.
	private MODE mode;

	// The transactional editing domain.
	private TransactionalEditingDomain editingDomain;

	// The current play-back run.
	// private Run playbackRun;

	// The list of play-back steps.
	private EList<Entry> playbackEntries;

	// The iterator of the play-back steps.
	private Iterator<Entry> playbackStepIter;

	// The next playback step.
	private Step nextPlaybackStep;

	// The next playback snapshot.
	private Snapshot nextPlaybackSnapshot;

	// The random seed to select random operations.
	private Random random;
	
	private final static int MAX_STEP = 100;

	/**
	 * Constructor to create a CODA simulation with a given trace for a machine
	 * root.
	 * 
	 * @param trace
	 *            a trace
	 * @param mch
	 *            the machine root corresponding to the trace.
	 */
	public Simulation(Trace trace, IMachineRoot mch) {
		this.trace = trace;
		this.mch = mch;
		editingDomain = TransactionalEditingDomain.Factory.INSTANCE
				.createEditingDomain();

		EMFRodinDB emfRodinDB = new EMFRodinDB();
		emfMch = (Machine) emfRodinDB.loadEventBComponent(mch);
		mode = MODE.RECORDING;
		random = new Random();
		Run run = Utils.makeRun();
		playbackEntries = run.getEntries();
		initPlayback();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#getTrace()
	 */
	@Override
	public Trace getTrace() {
		return trace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ISimulation#setTrace(de.prob.statespace.Trace)
	 */
	@Override
	public void setTrace(Trace trace) {
		this.trace = trace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#getEMFMachine()
	 */
	@Override
	public Machine getMachine() {
		return emfMch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#getComponentsStatus()
	 */
	@Override
	public List<IComponentStatemachinesStatus> getComponentStatemachinesStatus() {
		List<IComponentStatemachinesStatus> result = new ArrayList<IComponentStatemachinesStatus>();
		Trace trace = this.getTrace();
		State state = trace.getCurrentState();
		Machine emfMch = this.getMachine();

		// go through each extension and process components
		for (AbstractExtension ext : emfMch.getExtensions()) {
			if (ext instanceof Component) {
				EList<Component> components = ((Component) ext).getComponents();

				// For each component, process the state machines, making
				// distinction between different kind of translation, i.e.,
				// SINGLEVAR v.s MULTIVAR.
				for (Component component : components) {

					IComponentStatemachinesStatus compStatus = new ComponentStatemachinesStatus(component);

					EList<Statemachine> stateMachines = component
							.getAsynchronousStatemachines();
					stateMachines.addAll(component
							.getSynchronousStatemachines());

					for (EObject eobject : stateMachines) {
						if (eobject == null)
							continue;
						Statemachine statemachine = (Statemachine) eobject;
						String variable;
						String value;
						switch (statemachine.getTranslation()) {
						case SINGLEVAR:
							variable = statemachine.getName();
							value = Utils.getValue(state, variable);
							if (value == null)
								value = Messages.UNINITIALISED;
							compStatus.addStatemachine(variable, value);

							// This is for nested state machine.
							for (EObject object : statemachine.getAllContained(
									StatemachinesPackage.Literals.STATEMACHINE,
									true)) {
								if (object == null)
									continue;
								variable = ((EventBNamed) object).getName();
								value = Utils.getValue(state, variable);
								if (value == null)
									value = Messages.UNINITIALISED;
								compStatus.addStatemachine(variable, value);
							}
							break;

						case MULTIVAR:
							for (EObject object : statemachine.getAllContained(
									StatemachinesPackage.Literals.STATE, true)) {
								if (object == null)
									continue;
								variable = ((ac.soton.eventb.statemachines.State) object)
										.getName();
								value = Utils.getValue(state, variable);
								compStatus.addStatemachine(variable, value);
							}

							break;
						default:
							break;
						}
					}
					result.add(compStatus);
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#getConnectorsStatus()
	 */
	@Override
	public List<IConnectorStatus> getConnectorsStatus() {
		List<IConnectorStatus> result = new ArrayList<IConnectorStatus>();
		Trace trace = this.getTrace();
		State state = trace.getCurrentState();
		Machine emfMch = this.getMachine();

		// go through each extension and process connectors for each component
		for (AbstractExtension ext : emfMch.getExtensions()) {
			if (ext instanceof Component) {
				EList<Connector> connectors = ((Component) ext).getConnectors();

				for (Connector connector : connectors) {
					IConnectorStatus connStatus = new ConnectorStatus(connector);

					String name = connector.getName();

					String domain = Utils.getValue(state, "dom(" + name + ")");

					if (domain != null) {
						int[] times = Utils.parseEnumeratedSet(domain);
						for (int i = 0; i < times.length; i++) {
							int time = times[i];
							String value = Utils.getValue(state, name + "("
									+ time + ")");
							connStatus.addValue(time, value);
						}
					}
					result.add(connStatus);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see ISimulation#getWakeQueuesStatus()
	 */
	@Override
	public List<IWakeQueueStatus> getWakeQueuesStatus() {
		List<IWakeQueueStatus> result = new ArrayList<IWakeQueueStatus>();
		Trace trace = this.getTrace();
		State state = trace.getCurrentState();
		Machine emfMch = this.getMachine();

		// go through each extension and process connectors
		for (AbstractExtension ext : emfMch.getExtensions()) {
			if (ext instanceof Component) {
				EList<Component> components = ((Component) ext).getComponents();
				for (Component component : components) {
					EList<WakeQueue> queues = component.getWakeQueues();

					for (WakeQueue queue : queues) {
						IWakeQueueStatus queueStatus = new WakeQueueStatus(
								component, queue);

						String name = queue.getName() + "_wakequeue_max";

						String domain = Utils.getValue(state, "dom(" + name
								+ ")");

						if (domain != null) {
							queueStatus.initialise();
							int[] maxes = Utils.parseEnumeratedSet(domain);
							for (int i = 0; i < maxes.length; i++) {
								int max = maxes[i];
								String min = Utils.getValue(state, name + "("
										+ max + ")");
								
								String done = Utils.getValue(state, min
										+ "∈ WM_SELFWAKE_" + queue.getName()
										+ "_done");
								if ("FALSE".equals(done))
									queueStatus.addInterval(min, max+"");
							}
						}
						result.add(queueStatus);
					}
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#getCurrentTime()
	 */
	@Override
	public int getCurrentTime() {
		Trace trace = this.getTrace();
		State state = trace.getCurrentState();
		String value = Utils.getValue(state, "current_time");
		if (value == null) {
			return -1;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	@Override
	public SIM_RESULT restart() throws ProBError, IOException,
			ModelTranslationError {
		Trace oldTrace = this.getTrace();
		trace = ProB2Utils.createTrace(mch);
		initPlayback();
		ProB2Utils.ProB2NewAnimation(trace);
		ProB2Utils.ProB2RemoveAnimation(oldTrace);
		return SIM_RESULT.OK;
	}

	/**
	 * Set the mode (e.g., RECORDING or PLAYBACK) for the current simulation.
	 * 
	 * @param mode
	 *            the mode of the simulation.
	 * @throws ModelTranslationError
	 * @throws IOException
	 * @throws ProBError
	 */
	protected void setMode(MODE mode) throws ProBError, IOException,
			ModelTranslationError {
		if (this.mode != mode) {
			this.mode = mode;
			if (mode == MODE.PLAYBACK)
				restart();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#getMode()
	 */
	@Override
	public MODE getMode() {
		return mode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#getOracleFolder()
	 */
	@Override
	public IFolder getOracleFolder() throws CoreException {
		URI uri = EcoreUtil.getURI(emfMch);
		uri = uri.trimFileExtension();
		uri = uri.trimSegments(1);
		uri = uri.appendSegment("Oracle");
		IPath folderPath = new Path(uri.toPlatformString(true));
		folderPath = folderPath.makeAbsolute();
		IFolder oracleFolder = ResourcesPlugin.getWorkspace().getRoot()
				.getFolder(folderPath);
		if (!oracleFolder.exists()) {
			oracleFolder.create(false, true, null);
		}
		return oracleFolder;
	}

	public void loadRun(String rawLocation) throws IOException, ProBError,
			ModelTranslationError {
		IPath rawPath = new Path(rawLocation);
		IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot()
				.getRawLocation();
		IPath workspaceRelativePath = rawPath.makeRelativeTo(workspacePath);
		IPath path = new Path("platform:/resource");
		path = path.append(workspaceRelativePath);
		URI uri = URI.createURI(path.toString());
		ResourceSet rset = editingDomain.getResourceSet();
		Resource resource = rset.getResource(uri, true);
		Run run = OracleUtils.loadRun(resource);
		playbackEntries = run.getEntries();
		this.setMode(MODE.PLAYBACK); // Switch automatically to PLAYBACK
		this.restart();
	}

	/**
	 * @param run
	 * 
	 */
	private void initPlayback() {
		playbackStepIter = playbackEntries.iterator();
		nextPlaybackStep = null;
		if (playbackStepIter.hasNext())
			nextPlaybackSnapshot = (Snapshot) playbackStepIter.next();

		getNextPlaybackStep();
	}

	/**
	 * 
	 */
	private void getNextPlaybackStep() {
		if (playbackStepIter.hasNext()) {
			nextPlaybackStep = (Step) playbackStepIter.next();
			nextPlaybackSnapshot = (Snapshot) playbackStepIter.next();
		} else {
			nextPlaybackStep = null;
			nextPlaybackSnapshot = null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ISimulation#execute(Transition)
	 */
	@Override
	public void execute(Transition transition) {
		Trace trace = this.getTrace();
		trace = trace.add(transition);
		Utils.ProB2SetCurrentAnimation(trace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#save()
	 */
	@Override
	public void save(ProgressMonitorDialog dialog) throws CoreException {
		Trace trace = this.getTrace();
		EList<Variable> variables = emfMch.getVariables();
		Run run = Utils.getRun(trace, variables);
		save(dialog, run, mode == MODE.RECORDING);
	}

	private void save(ProgressMonitorDialog dialog, Run run, boolean gold)
			throws CoreException {
		final Resource resource = getResource("test", Utils.getTimeStamp(),
				gold);
		final SaveRunCommand saveRunCommand = new SaveRunCommand(editingDomain,
				resource, run);
		if (saveRunCommand.canExecute()) {
			// run with progress
			try {
				dialog.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						monitor.beginTask("Saving Oracle",
								IProgressMonitor.UNKNOWN);
						try {
							saveRunCommand.execute(monitor, null);
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						monitor.done();
					}
				});
				// disconnect run from this resource so that we can add more to
				// it
				editingDomain.getResourceSet().getResources().remove(resource);
				TransactionUtil.disconnectFromEditingDomain(resource);
				EcoreUtil.remove(run);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param name
	 * @param timestamp
	 * @param gold
	 * @return
	 * @throws CoreException
	 */
	private Resource getResource(String name, String timestamp, boolean gold)
			throws CoreException {
		IFolder folder = this.getOracleFolder();
		IPath filePath = folder.getFullPath();
		URI mchURI = EcoreUtil.getURI(emfMch);
		filePath = filePath.append("/"
				+ mchURI.trimFileExtension().lastSegment());
		filePath = filePath.addFileExtension(name);
		filePath = filePath.addFileExtension(timestamp);
		if (gold)
			filePath = filePath.addFileExtension("gold");
		filePath = filePath.addFileExtension("oracle");
		IPath path = new Path("platform:/resource");
		path = path.append(filePath);
		URI uri = URI.createURI(path.toString(), true);
		ResourceSet rset = editingDomain.getResourceSet();
		Resource resource = rset.getResource(uri, false);
		if (resource == null) {
			resource = editingDomain.getResourceSet().createResource(uri);
		}
		return resource;
	}

	private static class SaveRunCommand extends AbstractEMFOperation {

		Resource resource;
		Run run;

		public SaveRunCommand(TransactionalEditingDomain editingDomain,
				Resource resource, Run run) {
			super(editingDomain, "what can I say?");
			setOptions(Collections.singletonMap(Transaction.OPTION_UNPROTECTED,
					Boolean.TRUE));
			this.resource = resource;
			this.run = run;
		}

		@Override
		public boolean canRedo() {
			return false;
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			if (resource.getContents().isEmpty()
					|| !(resource.getContents().get(0) instanceof Oracle)) {
				resource.getContents().clear();
				Oracle oracle = OracleFactory.eINSTANCE.createOracle();
				resource.getContents().add(oracle);
			}
			Oracle oracle = (Oracle) resource.getContents().get(0);
			// add current recorded run to the oracle
			if (run != null) {
				Command addCommand = AddCommand.create(getEditingDomain(),
						oracle, OraclePackage.Literals.ORACLE__RUNS, run);
				if (addCommand.canExecute())
					addCommand.execute();
			}
			final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
			saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED,
					Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
			try {
				resource.save(saveOptions);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Status(Status.ERROR, Simulator2Plugin.PLUGIN_ID,
						"Saving Oracle Failed", e);
			}
			return new Status(Status.OK, Simulator2Plugin.PLUGIN_ID,
					"Saving Oracle Succeeded");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#tick(int)
	 */
	@Override
	public SIM_RESULT tick(int ticks) {
		int step = 0;
		int currentTime = this.getCurrentTime();
		int targetTime = currentTime + ticks;
		if (Utils.DEBUG)
			Utils.debug("Target time: " + targetTime);
		while (currentTime < targetTime && step < MAX_STEP) {
			if (Utils.DEBUG)
				Utils.debug("Current time: " + currentTime);
			SIM_RESULT result = step();
			if (result != SIM_RESULT.OK)
				return result;
			currentTime = this.getCurrentTime();
			step++;
		}
		if (step == MAX_STEP)
			return SIM_RESULT.TIMEOUT;
		return SIM_RESULT.OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#toContinue(int)
	 */
	@Override
	public SIM_RESULT toContinue(int steps) {
		if (steps == 0)
			return SIM_RESULT.OK;
		SIM_RESULT result = this.step(true);
		if (result != SIM_RESULT.OK)
			return result;
		return toContinue(steps - 1);
	}

	/**
	 * @param determinstic
	 * @return
	 */
	private SIM_RESULT step(boolean deterministic) {
		Set<Transition> transitions = this.getTrace().getNextTransitions();
		if (mode == MODE.PLAYBACK) { // If the mode is PLAYBACK
			if (nextPlaybackStep != null) {
				Transition transition = Utils.getTransition(transitions,
						nextPlaybackStep);
				if (transition == null) {
					return SIM_RESULT.PLAYBACK_TRANSITION_NOT_ENABLED;
				} else {
					SIM_RESULT result;
					Trace trace = this.getTrace();
					trace = trace.add(transition);
					if (!verifiedTransition(trace, nextPlaybackSnapshot)) {
						result = SIM_RESULT.PLAYBACK_INCORRECT_SNAPSHOT;
					} else {
						result = SIM_RESULT.OK;
					}
					Utils.ProB2SetCurrentAnimation(trace);
					getNextPlaybackStep();
					return result;
				}
			} else {
				return SIM_RESULT.PLAYBACK_NO_FURTHER_TRANSITION;
			}
		} else {
			int size = transitions.size();
			if (size != 0) {
				if (deterministic && !Utils.deterministic(transitions, emfMch)) {
					return SIM_RESULT.RECORDING_NON_DETERMINISTIC;
				}
				int i = random.nextInt(size);
				Transition[] array = transitions
						.toArray(new Transition[transitions.size()]);
				Transition transition = array[i];
				if (Utils.DEBUG)
					Utils.debug("Execute: " + transition.getName() + "["
							+ transition.getParams() + "]");
				execute(transition);
				return SIM_RESULT.OK;
			} else {
				return SIM_RESULT.RECORDING_DEADLOCK;
			}

		}
	}

	/**
	 * @param trace2
	 * @param newTrace
	 * @param next2Entry2
	 * @return
	 */
	private boolean verifiedTransition(Trace trace, Snapshot snapshot) {
		EList<Variable> variables = emfMch.getVariables();
		Snapshot playbackSnapshot = Utils.getSnapshot(trace, variables);
		return Utils.sameSnapshot(snapshot, playbackSnapshot);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.ISimulation#step()
	 */
	@Override
	public SIM_RESULT step() {
		return step(false);
	}

}
