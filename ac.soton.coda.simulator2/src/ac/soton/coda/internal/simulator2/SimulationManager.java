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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eventb.core.IMachineRoot;

import ac.soton.coda.internal.simulator2.ISimulation.MODE;

import com.google.inject.Injector;

import de.prob.Main;
import de.prob.exception.ProBError;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;


/**
 * <p>
 * A singleton manager for controlling CODA simulations.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see AbstractSimulationChangeProvider
 * @since 0.1
 */
public class SimulationManager extends AbstractSimulationChangeProvider
		implements ISimulationManager, IAnimationChangeListener,
		IModelChangedListener {

	/**
	 * The static singleton instance.
	 */
	private static SimulationManager manager;

	private Map<UUID, Simulation> sims;

	private Simulation currSim;

	private AnimationSelector selector;

	/**
	 * Singleton class has private constructor.
	 */
	private SimulationManager() {
		sims = new HashMap<UUID, Simulation>();
		Injector injector = Main.getInjector();
		selector = injector.getInstance(AnimationSelector.class);
		selector.registerAnimationChangeListener(this);
	};

	/**
	 * Return the singleton instance (create if none exists).
	 *
	 * @return the singleton instance.
	 */
	public static ISimulationManager getDefault() {
		if (manager == null)
			manager = new SimulationManager();
		return manager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ISimulationManager#getCurrentSimulation()
	 */
	@Override
	public ISimulation getCurrentSimulation() {
		return currSim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.soton.coda.internal.simulator2.ISimulationManager#newSimulation(org
	 * .eventb.core.IMachineRoot)
	 */
	@Override
	public ISimulation newSimulation(IMachineRoot mch, Trace trace) {
		Simulation sim = new Simulation(trace, mch);
		this.currSim = sim;
		sims.put(trace.getUUID(), sim);
		return sim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IAnimationChangeListener#animatorStatus(boolean)
	 */
	@Override
	public void animatorStatus(boolean arg0) {
		// TODO ???
	}

	/**
	 * @param arg0
	 *            the current trace.
	 * @param arg1
	 *            indicates if there are changes to the current trace.
	 * 
	 * @see IAnimationChangeListener#traceChange(Trace, boolean)
	 */
	public void traceChange(Trace arg0, boolean arg1) {
		checkRemovedTraces();
		if (!arg1) // If the current trace was not changed then ignore.
			return;
		if (currSim == null)
			return;
		if (arg0 == null) {
			currSim = null;
			notifySimulationChangeListeners();
			return;
		}

		UUID uuid = arg0.getUUID();
		if (uuid.equals(currSim.getTrace().getUUID())) {
			// Update the trace for the current simulation and notify the
			// simulation change listeners.
			currSim.setTrace(arg0);
			notifySimulationChangeListeners();
		} else {
			// Update the trace for the corresponding simulation.
			Simulation sim = sims.get(uuid);
			if (sim != null) {
				sim.setTrace(arg0);
				currSim = sim;
				notifySimulationChangeListeners();
			}
		}

	}

	/**
	 * 
	 */
	private void checkRemovedTraces() {
		// Check if a trace is removed in the background.
		List<Trace> traces = selector.getTraces();
		List<UUID> activeUUIDs = new ArrayList<UUID>(traces.size());
		for (Trace trace : traces) {
			activeUUIDs.add(trace.getUUID());
		}

		Set<UUID> keySet = sims.keySet();
		UUID[] uuids = keySet.toArray(new UUID[keySet.size()]);
		for (UUID uuid : uuids) {
			if (!activeUUIDs.contains(uuid)) {
				sims.remove(uuid);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.statespace.IModelChangedListener#modelChanged(de.prob.statespace
	 * .StateSpace)
	 */
	@Override
	public void modelChanged(StateSpace arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ISimulationManager#setMode(MODE)
	 */
	@Override
	public void setMode(MODE mode) throws ProBError, IOException,
			ModelTranslationError {
		currSim.setMode(mode);
		notifySimulationChangeListeners();
	}

}
