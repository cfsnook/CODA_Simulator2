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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * An implementation for viewing CODA components status during simulation.
 * 
 * @author htson
 * @version 0.1
 * @see AbstractStatusView
 * @since 0.1
 */
public class ComponentsView extends ViewPart {

	// The View ID.
	public static final String VIEW_ID = "ac.soton.coda.simulation2.componentsView"; //$NON-NLS-1$
			
	// The table viewer.
	private TableViewer viewer;
	
	/**
	 * An implementation of {@link WorkbenchPart#createPartControl(Composite)}
	 * to create a table viewer for components' status.
	 */
	@Override
	public final void createPartControl(Composite parent) {
		viewer = new ComponentsStatusTableViewer(parent);
	}

	/**
	 * An implementation of {@link WorkbenchPart#setFocus()} to focus on the
	 * control of the table viewer when the view gets focus.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
