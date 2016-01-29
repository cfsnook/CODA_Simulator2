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

import java.io.IOException;

import org.eventb.core.IMachineRoot;

import ac.soton.coda.internal.simulator2.ISimulation.MODE;
import ac.soton.coda.internal.simulator2.utils.Utils;
import de.prob.exception.ProBError;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.Trace;

/**
 * <p>
 * A common interface for simulation manager.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see ISimulationChangeProvider
 * @since 0.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISimulationManager extends ISimulationChangeProvider {

	/**
	 * Creates and returns a new simulation corresponding to an Event-B machine.
	 * 
	 * @param mch
	 *            a machine root.
	 * @param trace
	 *            a ProB trace corresponding to the input machine.
	 * @return the newly created machine root.
	 * @throws ModelTranslationError
	 *             if there are some unexpected errors in translation of the
	 *             model for simulation.
	 * @throws IOException
	 *             if there are some unexpected errors reading the input
	 *             machine.
	 * @throws ProBError
	 *             if there are some unexpected ProB errors.
	 * @see Utils#createTrace(IMachineRoot)
	 */
	public ISimulation newSimulation(IMachineRoot mch,Trace trace);

	/**
	 * Return the current simulation or <code>null</code> if none exists.
	 * 
	 * @return the current simulation.
	 */
	public ISimulation getCurrentSimulation();

	/**
	 * @param recording
	 * @throws ModelTranslationError
	 * @throws IOException
	 * @throws ProBError
	 */
	public void setMode(MODE recording) throws ProBError, IOException,
			ModelTranslationError;

}