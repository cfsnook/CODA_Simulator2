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

package ac.soton.coda.internal.simulator2.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;
import org.eventb.emf.core.AbstractExtension;
import org.eventb.emf.core.machine.Machine;

import ac.soton.coda.internal.simulator2.ISimulation;
import ac.soton.coda.internal.simulator2.ISimulation.MODE;
import ac.soton.coda.internal.simulator2.SimulationManager;
import ac.soton.coda.internal.simulator2.utils.Messages;
import ac.soton.coda.internal.simulator2.utils.Utils;
import ac.soton.eventb.emf.components.AbstractComponentOperation;
import ac.soton.eventb.emf.components.Component;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

/**
 * <p>
 * An implementation of a table viewer for monitoring the operations' status
 * during CODA simulations.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see AbstractStatusTableViewer
 * @since 0.1
 */
public class OperationsStatusTableViewer extends AbstractStatusTableViewer {

	// The column titles
	private final static String[] TITLES = { Messages.COLUMN_TITLE_COMPONENT,
			Messages.COLUMN_TITLE_ENABLED_OPERATION };

	// The default column bounds.
	private final static int[] BOUNDS = { 100, 200 };

	/**
	 * @param parent
	 */
	public OperationsStatusTableViewer(Composite parent) {
		super(parent);

		// Set the selection listener so that the corresponding transition is
		// executed when selected in RECORDING mode.
		this.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISimulation sim = SimulationManager.getDefault()
						.getCurrentSimulation();
				MODE mode = sim.getMode();
				if (mode == MODE.PLAYBACK) // Do nothing in PLAYBACK mode.
					return;

				// Get the current (structured) selection and execute the
				// transition.
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) selection;
					Object firstElement = ssel.getFirstElement();
					if (firstElement instanceof OperationStatus) {
						Transition transition = ((OperationStatus) firstElement)
								.getTransition();
						sim.execute(transition);
					}
				}
			}
		});
	}

	/**
	 * An implementation of
	 * {@link AbstractStatusTableViewer#createColumns(Composite)} to create the
	 * columns for the table viewer. The label provider for each column assume
	 * each entry is a {@link OperationStatus}. The first column is the
	 * component name (if any), the second column is the transition name and
	 * parameters.
	 */
	@Override
	protected void createColumns(Composite parent) {
		// The first column is for component names.
		TableViewerColumn col = createTableViewerColumn(TITLES[0], BOUNDS[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				OperationStatus varStatus = (OperationStatus) element;
				Component component = varStatus.getComponent();
				if (component == null)
					return Messages.NULL_COMPONENT_LABEL;
				return component.getName();
			}

		});

		// The second column is for transition name and parameters.
		col = createTableViewerColumn(TITLES[1], BOUNDS[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				OperationStatus varStatus = (OperationStatus) element;
				Transition transition = varStatus.getTransition();
				String name = transition.getName();
				List<String> params = transition.getParams();
				return Utils.prettyPrintTransition(name, params);
			}
		});

	}

	/**
	 * A private class for operation status. Each instance is a tuple
	 * <code>(component, transition)</code>.
	 * 
	 * @author htson
	 * @version 0.1
	 * @since 0.1
	 */
	private final static class OperationStatus {
		// The component.
		private Component component;

		// The variable name.
		private Transition transition;

		/**
		 * A constructor to create an instance with a given component,
		 * variable's name and value.
		 * 
		 * @param component
		 *            The component.
		 * @param transition
		 *            The transition.
		 */
		public OperationStatus(Component component, Transition transition) {
			this.component = component;
			this.transition = transition;
		}

		/**
		 * Returns the component.
		 * 
		 * @return The component.
		 */
		public Component getComponent() {
			return component;
		}

		/**
		 * Returns the transition.
		 * 
		 * @return The transition.
		 */
		public Transition getTransition() {
			return transition;
		}

	}

	/**
	 * An implementation of {@link AbstractStatusView#getStatus(ISimulation)} to
	 * return an array of {@link OperationStatus} for the components of the
	 * machine associated with the current CODA simulation trace. Each returned
	 * object corresponds to an enabled transition in the current state.
	 */
	protected Object[] getStatus(ISimulation sim) {
		List<OperationStatus> result = new ArrayList<OperationStatus>();
		Trace trace = sim.getTrace();
		Machine emfMch = sim.getMachine();

		Set<Transition> enabledTransitions = trace.getNextTransitions();

		// go through each extension and process components
		for (AbstractExtension ext : emfMch.getExtensions()) {
			if (ext instanceof Component) { // For each root component.
				// Get the list of direct sub-components.
				EList<Component> components = ((Component) ext).getComponents();

				// For each component, get the list of associated operations and
				// compare them with the list of enabled transitions.
				for (Component component : components) {
					EList<AbstractComponentOperation> operations = component
							.getOperations();
					for (AbstractComponentOperation operation : operations) {
						Set<Transition> transitions = getTransition(
								enabledTransitions, operation);
						for (Transition transition : transitions) {
							result.add(new OperationStatus(component,
									transition));
						}
						enabledTransitions.removeAll(transitions);
					}

				}
			}
		}

		for (Transition transition : enabledTransitions) {
			result.add(new OperationStatus(null, transition));
		}
		// Return the result as an array.
		return result.toArray(new OperationStatus[result.size()]);
	}

	/**
	 * Utility method to get a set of transitions corresponding to an operation
	 * (having the same name as the operation label) from a set of input
	 * transitions.
	 * 
	 * @param transitions
	 *            the set of input transitions.
	 * @param operation
	 *            the input operation.
	 * @return the set of input transitions having the same name as the input
	 *         operation.
	 */
	private Set<Transition> getTransition(Set<Transition> transitions,
			AbstractComponentOperation operation) {
		Set<Transition> result = new HashSet<Transition>();
		for (Transition transition : transitions) {
			if (transition.getName().equals(operation.getLabel()))
				result.add(transition);
		}
		return result;
	}

}
