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

import ac.soton.eventb.emf.components.Component;
import ac.soton.eventb.emf.components.WakeQueue;

/**
 * <p>
 * A common interface for wake queues' status. This extends
 * {@link IObjectStatus} for the (time,value)-pairs of the queues' status.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see WakeQueueStatus
 * @since 0.1
 */
public interface IWakeQueueStatus {

	/**
	 * Returns the wake-queue.
	 * 
	 * @return the wake-queue.
	 */
	public WakeQueue getQueue();

	/**
	 * @return
	 */
	public IInterval[] getIntervals();

	/**
	 * @return
	 */
	public Component getComponent();

	/**
	 * @param min
	 * @param max
	 */
	public void addInterval(String min, String max);

	/**
	 * 
	 */
	public void initialise();

}
