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

/**
 * A common interface for CODA simulation change listener.
 * 
 * @author htson
 * @version 0.1
 * @see ISimulationChangeProvider
 * @see SimulationManager
 * @since 0.1
 */
public interface ISimulationChangeListener {

	/**
	 * A call-back method for simulation change providers to notify the
	 * listener. Currently, there is no information regarding the changes is
	 * passed. As a result, the listeners need to query the status of the
	 * providers to update their status.
	 */
	public void simulationChange();
}
