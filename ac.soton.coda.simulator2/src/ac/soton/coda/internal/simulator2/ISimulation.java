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
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eventb.emf.core.machine.Machine;

import de.prob.exception.ProBError;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

/**
 * <p>
 * A common interface for a simulation run.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see Simulation
 * @since 0.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISimulation {

	/**
	 * <p>
	 * The simulation MODE, either PLAYBACK or RECORDING
	 * </p>
	 *
	 * @author htson
	 * @version 0.1
	 * @since 0.1
	 */
	public enum MODE {
		PLAYBACK, RECORDING
	};

	/**
	 * <p>
	 * The simulation result, various enumerated values to indicate the status
	 * of the operations.
	 * <ul>
	 * <li>{@link SIM_RESULT#OK}: The operation is successful.
	 * </ul>
	 * </p>
	 *
	 * @author htson
	 * @version
	 * @see
	 * @since
	 */
	public enum SIM_RESULT {
		/**
		 * The operation is successful.
		 */
		OK,

		/**
		 * 
		 */
		PLAYBACK_TRANSITION_NOT_ENABLED,
		
		/**
		 * 
		 */
		RECORDING_DEADLOCK,
		
		/**
		 * 
		 */
		PLAYBACK_NO_FURTHER_TRANSITION,
		
		/**
		 * 
		 */
		RECORDING_NON_DETERMINISTIC,
		
		/**
		 * 
		 */
		PLAYBACK_INCORRECT_SNAPSHOT, 
		
		/**
		 * Timeout 
		 */
		TIMEOUT
	}

	/**
	 * Set the trace of the simulation.
	 * 
	 * @param trace
	 *            the ProB2 trace.
	 */
	public void setTrace(Trace trace);

	/**
	 * Returns the trace of the simulation.
	 * 
	 * @return the trace of the simulation.
	 */
	public Trace getTrace();

	/**
	 * Returns the machine corresponding to the machine.
	 * 
	 * @return the machine corresponding to the machine.
	 */
	public Machine getMachine();

	/**
	 * Returns the list of components' status of the simulation at the current
	 * state.
	 * 
	 * @return the list of components' status.
	 */
	public List<IComponentStatemachinesStatus> getComponentStatemachinesStatus();

	/**
	 * Returns the list of connectors' status of the simulation at the current
	 * state.
	 * 
	 * @return the list of connectors' status.
	 */
	public List<IConnectorStatus> getConnectorsStatus();

	/**
	 * Returns the current time.
	 * 
	 * @return the current time.
	 */
	public int getCurrentTime();

	/**
	 * Returns the mode of the current simulation.
	 * 
	 * @return the mode of the current simulation.
	 */
	public MODE getMode();

	/**
	 * Returns the oracle folder.
	 * 
	 * @return the oracle folder.
	 * @throws CoreException
	 *             if there is some unexpected error.
	 */
	public IFolder getOracleFolder() throws CoreException;

	/**
	 * Save the current trace.
	 * 
	 * @param dialog
	 *            the progress monitor dialog.
	 * @throws CoreException
	 *             if there is an unexpected error occurs.
	 */
	public void save(ProgressMonitorDialog dialog) throws CoreException;

	/**
	 * Restart the simulation.
	 * 
	 * @return the result of the operation.
	 * @throws ModelTranslationError
	 * @throws IOException
	 * @throws ProBError
	 */
	public SIM_RESULT restart() throws ProBError, IOException,
			ModelTranslationError;

	/**
	 * Load a run from an oracle given by a raw location.
	 * 
	 * @param rawLocation
	 *            the raw location of a oracle.
	 * @throws IOException
	 *             if there is an error during opening the oracle.
	 * @throws ModelTranslationError
	 *             if there is an error during model translation for simulation.
	 * @throws ProBError
	 *             if there is an error from ProB.
	 */
	public void loadRun(String rawLocation) throws IOException, ProBError,
			ModelTranslationError;

	/**
	 * Executes a transition.
	 * 
	 * @param transition
	 *            the transition to be executed.
	 */
	public void execute(Transition transition);

	/**
	 * Progress (randomly) forward a number ticks.
	 * 
	 * @param ticks
	 *            the number of ticks to progress.
	 * @return the result of the operation.
	 */
	public SIM_RESULT tick(int ticks);

	/**
	 * Continues the simulation for a number of steps. Stops if there is a
	 * non-deterministic choice within a component.
	 * 
	 * @param steps
	 *            the number of steps to continue.
	 * @return the result of the operation.
	 */
	public SIM_RESULT toContinue(int steps);

	/**
	 * Executes a step. The behaviour depends on the current simulation mode.
	 * <ul>
	 * <li>{@link MODE#RECORDING}: pick a random enabled transition and execute.
	 * </li>
	 * <li>{@link MODE#PLAYBACK}: get the next transition in the play-back run
	 * and execute.</li>
	 * </ul>
	 * 
	 * @return the result of the operation.
	 */
	public SIM_RESULT step();

	/**
	 * @return
	 */
	public List<IWakeQueueStatus> getWakeQueuesStatus();

}