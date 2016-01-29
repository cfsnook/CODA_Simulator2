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

import java.util.ArrayList;
import java.util.List;

import ac.soton.eventb.emf.components.Component;
import ac.soton.eventb.emf.components.WakeQueue;

/**
 * <p>
 * An implementation of {@link IWakeQueueStatus} by extending
 * {@link AbstractObjectStatus}.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see IWakeQueueStatus
 * @see AbstractObjectStatus
 * @since 0.1
 */
public class WakeQueueStatus implements IWakeQueueStatus {

	// The component containing the queue.
	private Component component;

	// The wake-queue.
	private WakeQueue queue;

	private List<IInterval> intervals;

	/**
	 * The constructor to create a wake-queue status for a given queue with an
	 * empty map of time-value pairs.
	 * 
	 * @param component
	 *            the component containing the wake-queue.
	 * @param queue
	 *            the wake-queue.
	 */
	public WakeQueueStatus(Component component, WakeQueue queue) {
		super();
		this.queue = queue;
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IWakeQueueStatus#getQueue()
	 */
	@Override
	public WakeQueue getQueue() {
		return queue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.soton.coda.internal.simulator2.IWakeQueueStatus#getIntervals()
	 */
	@Override
	public IInterval[] getIntervals() {
		if (intervals == null)
			return null;
		return intervals.toArray(new IInterval[intervals.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IWakeQueueStatus#getComponent()
	 */
	@Override
	public Component getComponent() {
		return component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IWakeQueueStatus#addInterval(String, String)
	 */
	@Override
	public void addInterval(String min, String max) {

		IInterval interval = new Interval(min, max);
		intervals.add(interval);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IWakeQueueStatus#initialise()
	 */
	@Override
	public void initialise() {
		intervals = new ArrayList<IInterval>();
	}

}
