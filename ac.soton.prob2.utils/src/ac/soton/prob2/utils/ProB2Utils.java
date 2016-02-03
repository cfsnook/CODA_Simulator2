/*******************************************************************************
 * Copyright (c) 2015 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package ac.soton.prob2.utils;

import java.io.IOException;

import org.eventb.core.IMachineRoot;

import com.google.inject.Injector;

import de.prob.Main;
import de.prob.exception.ProBError;
import de.prob.model.eventb.EventBModel;
import de.prob.scripting.EventBFactory;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob2.ui.eclipse.VersionController;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see
 * @since 0.1
 */
public class ProB2Utils {

	/***************************************************************************
	 * ProB2 Utilities (BEGIN) *
	 **************************************************************************/

	/**
	 * <p>
	 * Start a new ProB2 simulation for the given machine root. All other ProB2
	 * simulations will be unchanged.
	 * </p>
	 * 
	 * <p>
	 * This is adapted from
	 * ac.soton.eventb.statemachines.animation2.DiagramAnimator.
	 * </p>
	 * 
	 * @param mch
	 * @return The trace corresponding to the newly created ProB2 simulation.
	 * @throws ModelTranslationError
	 *             if there are some errors in translating model into ProB2
	 *             input.
	 * @throws IOException
	 *             if there are some errors in reading the model.
	 */
	public static Trace createTrace(IMachineRoot mchRoot) throws IOException,
			ModelTranslationError, ProBError {

		// Check version installed.
		VersionController.ensureInstalled();
		
		// Get the checked machine absolute location.
		String fileName = mchRoot.getResource().getRawLocation().makeAbsolute()
				.toOSString();
		fileName = fileName.replace(".bum", ".bcm");

		// load the machine
		Injector injector = Main.getInjector();
		final EventBFactory instance = injector
				.getInstance(EventBFactory.class);
		EventBModel model = instance.load(fileName);
		StateSpace s = model.getStateSpace();

		// Create a new trace
		Trace trace = new Trace(s);

		return trace;
	}

	public static void ProB2NewAnimation(Trace trace) {
		Injector injector = Main.getInjector();
		AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);

		// Add the new trace as a new animation.
		selector.addNewAnimation(trace, false);

		// Run the garbage collector.
		System.gc();
	}

	/**
	 * Utility method to remove an animation specified by the input trace.
	 * 
	 * @param trace
	 *            the trace animation to be removed.
	 */
	public static void ProB2RemoveAnimation(Trace trace) {
		Injector injector = Main.getInjector();
		AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);
		selector.removeTrace(trace);
	}

	/***************************************************************************
	 * ProB2 Utilities (END) *
	 **************************************************************************/

}
