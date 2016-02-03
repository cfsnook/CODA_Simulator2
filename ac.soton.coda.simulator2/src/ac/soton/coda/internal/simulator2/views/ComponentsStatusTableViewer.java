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
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;

import ac.soton.coda.internal.simulator2.IComponentStatemachinesStatus;
import ac.soton.coda.internal.simulator2.IInterval;
import ac.soton.coda.internal.simulator2.ISimulation;
import ac.soton.coda.internal.simulator2.IWakeQueueStatus;
import ac.soton.coda.internal.simulator2.utils.Messages;
import ac.soton.eventb.emf.components.Component;
import ac.soton.eventb.emf.components.WakeQueue;

/**
 * <p>
 * An implementation for a table viewer for monitoring the components' status
 * during CODA simulation.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see AbstractStatusTableViewer
 * @since 0.1
 */
public class ComponentsStatusTableViewer extends AbstractStatusTableViewer {

	// The column titles.
	private final static String[] TITLES = { Messages.COLUMN_TITLE_COMPONENT, Messages.COLUMN_TITLE_VARIABLE, Messages.COLUMN_TITLE_VALUE };

	// The default column bounds.
	private final static int[] BOUNDS = { 100, 100, 200 };

	/**
	 * A constructor to create the table viewer using the Composite parent.
	 * 
	 * @param parent
	 *            the Composite parent.
	 */
	public ComponentsStatusTableViewer(Composite parent) {
		super(parent);
	}

	/**
	 * An implementation of
	 * {@link AbstractStatusTableViewer#createColumns(Composite)} to create the
	 * columns for the table viewer. The label provider for each column assume
	 * each entry is a {@link StatemachineValueStatus}.
	 */
	@Override
	public void createColumns(Composite parent) {

		// The first column is for component names.
		TableViewerColumn col = createTableViewerColumn(TITLES[0], BOUNDS[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}

		});

		// The second column is for variable names.
		col = createTableViewerColumn(TITLES[1], BOUNDS[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IObjectVariableValueStatus compStatus = (IObjectVariableValueStatus) element;
				return compStatus.getVariable();
			}
		});

		// The third column is for the variable values.
		col = createTableViewerColumn(TITLES[2], BOUNDS[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IObjectVariableValueStatus compStatus = (IObjectVariableValueStatus) element;
				return compStatus.getValue();
			}
		});

	}

	/**
	 * An implementation of {@link AbstractStatusView#getStatus(ISimulation)} to
	 * return an array of {@link StatemachineValueStatus} for the components of the
	 * machine associated with the current CODA simulation trace.
	 */
	protected Object[] getStatus(ISimulation sim) {
		List<IObjectVariableValueStatus> result = new ArrayList<IObjectVariableValueStatus>();

		// Add statemachine status.
		List<IComponentStatemachinesStatus> smsStatus = sim.getComponentStatemachinesStatus();
		for (IComponentStatemachinesStatus smStatus : smsStatus) {
			Component component = smStatus.getComponent();
			Set<String> variables = smStatus.getStatemachines();
			for (String variable : variables) {
				String value = smStatus.getValue(variable);
				if (value == null)
					value = Messages.UNINITIALISED;
				result.add(new StatemachineValueStatus(component, variable, value));
			}
		}

		// Add wakequeue status.
		List<IWakeQueueStatus> queuesStatus = sim.getWakeQueuesStatus();
		for (IWakeQueueStatus queueStatus : queuesStatus) {
			Component component = queueStatus.getComponent();
			WakeQueue queue = queueStatus.getQueue();
			IInterval[] intervals = queueStatus.getIntervals();
			
			if (intervals == null) {
				result.add(new WakeQueueValueStatus(component, queue, "Uninitialised"));
			}
			else {
				StringBuffer value = new StringBuffer();
				boolean first = true;
				for (IInterval interval : intervals) {
					if (first) {
						first = false;
					} else {
						value.append(", ");
					}
				
					String min = interval.getMin();
					String max = interval.getMax();
				
					value.append(min + " ↦ " + max);
				}
			
				result.add(new WakeQueueValueStatus(component, queue, value.toString()));
			}
		}
		
		// Return the result as an array.
		return result.toArray(new IObjectVariableValueStatus[result.size()]);
	}

}
