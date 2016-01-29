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
import java.util.Collection;

/**
 * <p>
 * An abstract implementation for {@link ISimulationChangeProvider}. This
 * maintains a list of {@link ISimulationChangeListener}s.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see ISimulationChangeListener
 * @since 0.1
 */
public class AbstractSimulationChangeProvider implements
		ISimulationChangeProvider {

	// The list of listeners.
	private Collection<ISimulationChangeListener> listeners = new ArrayList<ISimulationChangeListener>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see ISimulationChangeProvider#addSimulationChangeListener(
	 * ISimulationChangeListener)
	 */
	@Override
	public final void addSimulationChangeListener(
			ISimulationChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ISimulationChangeProvider#removeSimulationChangeListener(
	 * ISimulationChangeListener)
	 */
	@Override
	public final void removeSimulationChangeListener(
			ISimulationChangeListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	/**
	 * Utility method for notify the simulation change listeners. No information
	 * about the change is passed, hence the listener needs to read the status
	 * of the manager to get updated information.
	 */
	protected final void notifySimulationChangeListeners() {
		for (ISimulationChangeListener listener : listeners) {
			listener.simulationChange();
		}
	}

}
