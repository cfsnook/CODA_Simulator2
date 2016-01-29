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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ac.soton.coda.internal.simulator2.ISimulation;
import ac.soton.coda.internal.simulator2.ISimulationChangeListener;
import ac.soton.coda.internal.simulator2.SimulationManager;

/**
 * The abstract implementation for table viewer for monitoring the status of
 * CODA simulations.
 * 
 * @author htson
 * @version 0.1
 * @see TableViewer
 * @see ISimulationChangeListener
 * @since 0.1
 */
public abstract class AbstractStatusTableViewer extends TableViewer implements
		ISimulationChangeListener {

	/**
	 * Construct a status table view with the default style. Call the abstract
	 * methods to create the columns ({@link #createColumns(Composite)}). The
	 * content of the table is populated using the abstract method
	 * {@link #getStatus(ISimulation)} and the input is set using
	 * {@link #setInput()}.
	 * 
	 * @param parent
	 *            The composite parent.
	 */
	public AbstractStatusTableViewer(Composite parent) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.BORDER);
		// Create the table columns.
		createColumns(parent);

		Table table = this.getTable();
		// Set the visibility of the headers.
		table.setHeaderVisible(true);
		// Set the layout for the table.
		table.setLayout(new GridLayout(2, true));
		this.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		// Set the default content provider.
		this.setContentProvider(new IStructuredContentProvider() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see IContentProvider#inputChanged(Viewer, Object, Object)
			 */
			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// Do nothing
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see IContentProvider#dispose()
			 */
			@Override
			public void dispose() {
				// Do nothing
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see IStructuredContentProvider#getElements(Object)
			 */
			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement == null)
					return null;
				ISimulation sim = (ISimulation) inputElement;
				return getStatus(sim);
			}

		});

		// Set the input
		this.setInput();

		// Register this as a listener to simulation changes.
		SimulationManager.getDefault().addSimulationChangeListener(this);
	}

	/**
	 * An implementation of the call back
	 * {@link ISimulationChangeListener#simulationChange()} to handle change
	 * from the Simulation manager. This simply (re)-set the input to the viewer
	 * which will force the viewer to refresh and update the content.
	 */
	@Override
	public final void simulationChange() {
		// Display thread safe.
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setInput();
			}
		});
	}

	/*
	 * Utility class for setting the view's input. This set the current CODA
	 * simulation as the table viewer's input.
	 */
	private void setInput() {
		this.setInput(SimulationManager.getDefault().getCurrentSimulation());
		return;
	}

	/**
	 * Abstract method for getting the statuses to be displayed in the table
	 * viewer. This information is collected from the current CODA simulation.
	 * 
	 * @param sim
	 *            The CODA simulation.
	 * @return The list of objects to be displayed in the table.
	 */
	protected abstract Object[] getStatus(ISimulation sim);

	/**
	 * Abstract method to create the columns of this table viewer.
	 * 
	 * @param parent
	 *            The composite parent.
	 * @param viewer
	 *            The table viewer.
	 */
	protected abstract void createColumns(Composite parent);

	/**
	 * Utility method for create a column for this table viewer with given a
	 * title, a default bound and the column number.
	 * 
	 * @param title
	 *            The title.
	 * @param bound
	 *            The default bound
	 * @param colNumber
	 *            The column number.
	 * @return The newly created table viewer column.
	 */
	protected final TableViewerColumn createTableViewerColumn(String title,
			int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(this,
				SWT.LEFT | SWT.WRAP);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

}