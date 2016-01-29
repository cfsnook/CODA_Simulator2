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

import ac.soton.eventb.emf.components.Component;
import ac.soton.eventb.emf.components.WakeQueue;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since
 */
public class WakeQueueValueStatus implements IObjectVariableValueStatus {

	private Component component;
	
	private WakeQueue queue;
	
	private String value;
	
	/**
	 * @param component
	 * @param queue
	 * @param string
	 */
	public WakeQueueValueStatus(Component component, WakeQueue queue,
			String value) {
		this.component = component;
		this.queue = queue;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see ac.soton.coda.internal.simulator2.views.IObjectVariableValueStatus#getVariable()
	 */
	@Override
	public String getVariable() {
		return "(WakeQ)" + queue.getName();
	}

	/* (non-Javadoc)
	 * @see ac.soton.coda.internal.simulator2.views.IObjectVariableValueStatus#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return component.getName();
	}
}
