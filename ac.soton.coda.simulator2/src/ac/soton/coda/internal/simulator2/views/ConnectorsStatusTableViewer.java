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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;

import ac.soton.coda.internal.simulator2.IConnectorStatus;
import ac.soton.coda.internal.simulator2.ISimulation;
import ac.soton.coda.internal.simulator2.utils.Messages;
import ac.soton.eventb.emf.components.Connector;

/**
 * <p>
 * An implementation of a table viewer for monitoring the connectors' status
 * during CODA simulations.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see AbstractStatusTableViewer
 * @since 0.1
 */
public class ConnectorsStatusTableViewer extends AbstractStatusTableViewer {

	// The titles
	private final static String[] TITLES = { Messages.COLUMN_TITLE_CONNECTOR,
			Messages.COLUMN_TITLE_TIME, Messages.COLUMN_TITLE_VALUE };

	// The default bounds.
	private final static int[] BOUNDS = { 100, 100, 200 };

	/**
	 * A constructor to create the table viewer using the Composite parent.
	 * 
	 * @param parent
	 *            the Composite parent.
	 */
	public ConnectorsStatusTableViewer(Composite parent) {
		super(parent);

		// Set the sorter.
		this.setSorter(new ConnectorSorter());
	}

	/**
	 * An implementation of
	 * {@link AbstractStatusTableViewer#createColumns(Composite)} to create the
	 * columns for the table viewer. The label provider for each column assume
	 * each entry is a {@link ConnectorTimeValueStatus}. The first column is the
	 * component name (if any), the second column is the time, and the third
	 * column is the value.Ï
	 */
	@Override
	protected void createColumns(Composite parent) {
		// The first column is for component names.
		TableViewerColumn col = createTableViewerColumn(TITLES[0], BOUNDS[0], 0);

		// Only display the component name if the time is -1 (i.e., "now").
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IObjectTimeStatus status = (IObjectTimeStatus) element;
				if (status instanceof ConnectorCurrentValueStatus) {
					return status.toString();
				}
				else if (status instanceof ConnectorPreviousValueStatus) {
					return status.toString();
				} else {
					return Messages.EMPTY_CONNECTOR_NAME;
				}
			}

		});

		// The second column is for time.
		col = createTableViewerColumn(TITLES[1], BOUNDS[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IObjectTimeStatus status = (IObjectTimeStatus) element;
				int time = status.getTime();
				if (status instanceof ConnectorPreviousValueStatus) {
					return Messages.bind(Messages.PREVIOUS, time);
				} else if (status instanceof ConnectorCurrentValueStatus) {
					return Messages.bind(Messages.NOW, time);
				} else {
					return time + ""; //$NON-NLS-1$
				}
			}
		});

		// The third column is for value of the connector.
		col = createTableViewerColumn(TITLES[2], BOUNDS[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IObjectTimeStatus status = (IObjectTimeStatus) element;
				return status.getValue().toString();
			}
		});

	}

	/**
	 * An implementation of {@link AbstractStatusView#getStatus(ISimulation)} to
	 * return an array of {@link ConnectorTimeValueStatus} of the machine
	 * associated with the current CODA simulation trace.
	 */
	@Override
	protected Object[] getStatus(ISimulation sim) {
		List<IObjectTimeStatus> result = new ArrayList<IObjectTimeStatus>();

		int currTime = sim.getCurrentTime();

		// Get the connectors' status
		List<IConnectorStatus> connsStatus = sim.getConnectorsStatus();
		for (IConnectorStatus connStatus : connsStatus) {
			Connector connector = connStatus.getConnector();
			int previous = -1;
			Integer[] times = connStatus.getTimes();
			if (times.length == 0) {
				result.add(new ConnectorCurrentValueStatus(connector, -1,
						Messages.UNINITIALISED));
			} else {
				for (int time : times) {
					if (time > currTime) {
						String value = connStatus.getValue(time);
						result.add(new ConnectorFutureValueStatus(connector,
								time, value));
					} else if (time == currTime) {
						String value = connStatus.getValue(time);
						result.add(new ConnectorCurrentValueStatus(connector,
								time, value));
						previous = time;
					} else if (previous < time) {
						previous = time;
					}
				}
				if (previous != currTime) {
					result.add(new ConnectorPreviousValueStatus(connector,
							previous, connStatus.getValue(previous)));
				}
			}
		}

		return result.toArray(new IObjectTimeStatus[result.size()]);
	}

	/**
	 * A sorter for sorting the connector/time objects in the table. The objects
	 * are sorted according to (in the order of priority) (1) the connector
	 * name, and (2) the time.
	 * 
	 * @author htson
	 * @version 0.1
	 * @see ViewerSorter
	 * @since 0.1
	 */
	private class ConnectorSorter extends ViewerSorter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface
		 * .viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {

			if (e1 instanceof ConnectorTimeValueStatus
					&& e2 instanceof ConnectorTimeValueStatus) {
				ConnectorTimeValueStatus status1 = (ConnectorTimeValueStatus) e1;
				ConnectorTimeValueStatus status2 = (ConnectorTimeValueStatus) e2;

				String name1 = status1.toString();
				String name2 = status2.toString();

				if (name1.equals(name2)) {
					// If the names are the same the compare the time.
					int time1 = status1.getTime();
					int time2 = status2.getTime();
					if (time1 < time2)
						return -1;
					if (time1 > time2)
						return 1;
					return 0;
				} else {
					return name1.compareTo(name2);
				}

			}

			return super.compare(viewer, e1, e2);
		}

	}
}
