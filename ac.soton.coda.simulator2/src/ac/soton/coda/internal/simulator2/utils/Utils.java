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
package ac.soton.coda.internal.simulator2.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eventb.emf.core.AbstractExtension;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.Variable;

import ac.soton.coda.internal.simulator2.ISimulation.SIM_RESULT;
import ac.soton.coda.simulator2.Simulator2Plugin;
import ac.soton.eventb.emf.components.AbstractComponentOperation;
import ac.soton.eventb.emf.components.Component;
import ac.soton.eventb.emf.components.External;
import ac.soton.eventb.emf.oracle.Entry;
import ac.soton.eventb.emf.oracle.OracleFactory;
import ac.soton.eventb.emf.oracle.Run;
import ac.soton.eventb.emf.oracle.Snapshot;
import ac.soton.eventb.emf.oracle.Step;

import com.google.inject.Injector;

import de.prob.Main;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IdentifierNotInitialised;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.State;
import de.prob.statespace.Trace;
import de.prob.statespace.TraceElement;
import de.prob.statespace.Transition;

/**
 * @author htson
 *         <p>
 *         This class contains some utility static methods that are used in the
 *         CODA simulator (with ProB2).
 *         </p>
 *
 */
public class Utils {

	/**
	 * The debug flag. This is set by the option when the platform is launched.
	 * Client should not try to reset this flag.
	 */
	public static boolean DEBUG = false;

	/**
	 * Utility method to print a debug message.
	 * 
	 * @param msg
	 *            the debug message.
	 */
	public static void debug(String msg) {
		System.out.println(Simulator2Plugin.PLUGIN_ID + " " + msg);
	}

	/**
	 * Utility method for handling exception by open a message dialog.
	 * 
	 * @param e
	 *            the exception.
	 * @param msg
	 *            the message to display.
	 */
	public static void handleException(Exception e, String msg) {
		MessageDialog.openError(Display.getDefault().getActiveShell(),
				"Exception", msg + "\nThis is because: " + e.getMessage());
	}

	/**
	 * Utility method for getting the String value of a formula in a state.
	 * Return <code>null</code> if the formula cannot be evaluated successfully
	 * in the given state.
	 * 
	 * @param state
	 *            the input state.
	 * @param formula
	 *            the input formula to be evaluated.
	 * @return the evaluated result of the input formula in the given state or
	 *         <code>null</code>.
	 */
	public static String getValue(State state, String formula) {
		AbstractEvalResult eval = state.eval(formula);
		if (eval instanceof EvalResult) {
			return ((EvalResult) eval).getValue();
		} else {
			return null;
		}
	}

	/**
	 * Utility method to parse the domain value to get a list of elements in the
	 * domain. The domain assumes to be of the form a comma-separated list of
	 * integers inside a pair of curly brackets.
	 * 
	 * @param domain
	 *            The domain as string.
	 * @return the list of elements in the domain.
	 */
	public static int[] parseEnumeratedSet(String domain) {
		domain = domain.replace("{", "");
		domain = domain.replace("}", "");
		String[] split = domain.split(",");
		int length = split.length;
		int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = Integer.parseInt(split[i]);
		}
		return result;
	}

	/**
	 * Return a transition corresponding to a given step from a list of
	 * transitions or <code>null</code>.
	 * 
	 * @param transitions
	 *            input list of transitions.
	 * @param step
	 *            the input step.
	 * @return the transition corresponding to the input step or
	 *         <code>null</code>.
	 */
	public static Transition getTransition(Set<Transition> transitions,
			Step step) {
		for (Transition transition : transitions) {
			if (isTheSame(transition, step))
				return transition;
		}
		return null;
	}

	/**
	 * Utility method to compare a transition and a step. Return
	 * <code>true</code> if they have the same name and same
	 * parameters/arguments.
	 * 
	 * @param transition
	 *            the input transition
	 * @param step
	 *            the input step
	 * @return <code>true</code> if and only if the transition is the same as
	 *         the step.
	 */
	private static boolean isTheSame(Transition transition, Step step) {
		String transitionName = transition.getName();
		String stepName = step.getName();
		if (!transitionName.equals(stepName))
			return false;

		List<String> transitionParams = transition.getParams();
		EList<String> stepArgs = step.getArgs();

		return isTheSame(transitionParams, stepArgs);
	}

	/**
	 * Utility method to compare a list of transition parameters and a list of
	 * step arguments. They are the same if the number of parameters/steps are
	 * identical and they have the same value at the same position in the lists.
	 * 
	 * @param transitionParams
	 *            the list of transition parameters.
	 * @param stepArgs
	 *            the list of step arguments.
	 * @return <code>true</code> if and only if the parameters/arguments are the
	 *         same.
	 */
	private static boolean isTheSame(List<String> transitionParams,
			EList<String> stepArgs) {
		if (transitionParams.size() != stepArgs.size())
			return false;

		Iterator<String> paramIter = transitionParams.iterator();
		Iterator<String> argIter = stepArgs.iterator();
		while (paramIter.hasNext()) {
			String param = paramIter.next();
			String arg = argIter.next();
			if (!param.equals(arg))
				return false;
		}

		return true;
	}

	/**
	 * Utility method to get a time stamp.
	 * 
	 * @return the time step.
	 */
	public static String getTimeStamp() {
		String timestamp = "";
		Calendar calendar = Calendar.getInstance();
		timestamp = timestamp + twoDigits(calendar.get(Calendar.YEAR));
		timestamp = timestamp + twoDigits(calendar.get(Calendar.MONTH) + 1);
		timestamp = timestamp + twoDigits(calendar.get(Calendar.DAY_OF_MONTH));
		timestamp = timestamp + twoDigits(calendar.get(Calendar.HOUR_OF_DAY));
		timestamp = timestamp + twoDigits(calendar.get(Calendar.MINUTE));
		timestamp = timestamp + twoDigits(calendar.get(Calendar.SECOND));
		return timestamp;
	}

	/**
	 * Utility method to return an integer as at least two digits by prepend 0
	 * at the beginning.
	 * 
	 * @param integer
	 *            the input integer.
	 * @return the at-least-two digits string.
	 */
	private static String twoDigits(int integer) {
		String ret = Integer.toString(integer);
		if (ret.length() < 2)
			ret = "0" + ret;
		return ret;
	}

	/**
	 * Utility method to pretty print a transition in the form "name(params)".
	 * 
	 * @param name
	 *            the name of the transition.
	 * @param params
	 *            the list of the parameters.
	 * @return the pretty print of the transition.
	 */
	public static String prettyPrintTransition(String name, List<String> params) {
		StringBuilder sb = new StringBuilder(name);
		sb.append("(");
		boolean first = true;
		for (String param : params) {
			if (first) {
				sb.append(param);
				first = false;
			} else {
				sb.append(", " + param);
			}
		}
		sb.append(")");

		return sb.toString();
	}

	/**
	 * Gets a run from the current simulation trace with the given list of
	 * variables.
	 * 
	 * @param trace
	 *            the current simulation trace.
	 * @param variables
	 *            the list of variables.
	 * @return the run corresponding to the trace.
	 */
	public static Run getRun(Trace trace, EList<Variable> variables) {
		TraceElement current = trace.getCurrent();
		Run run = makeRun();

		while (current != null) { // Enumerate through the trace element.
			// Extract the step from the current trace element.
			Step step = getStep(current);
			// Extract the snapshot from the current trace element.
			Snapshot snapshot = getSnapshot(current, variables);

			// Add the snapshot to the head of the list of entries.
			EList<Entry> entries = run.getEntries();
			entries.add(0, snapshot);
			// Add the non-null step to the head of the list of entries
			if (step != null)
				entries.add(0, step);
			current = current.getPrevious();
		}

		return run;
	}

	/**
	 * Utility method to get a snapshot from the current trace element given a
	 * list of variables.
	 * 
	 * @param traceElement
	 *            the current trace element.
	 * @param variables
	 *            the list of variables.
	 * @return the snapshot corresponding to the trace element.
	 */
	private static Snapshot getSnapshot(TraceElement traceElement,
			EList<Variable> variables) {
		State currentState = traceElement.getCurrentState();
		TraceElement previous = traceElement.getPrevious();
		State previousState = null;
		if (previous != null) {
			previousState = previous.getCurrentState();
		}

		Snapshot snapshot = getTheDifferences(currentState, previousState,
				variables);

		return snapshot;
	}

	/**
	 * Utility method to gather the differences between the current state and
	 * the previous state. The list of variables are given as references.
	 * 
	 * @param currentState
	 *            the current state.
	 * 
	 * @param previousState
	 *            the previous state (<code>null</code> if there is no previous
	 *            state).
	 * @param variables
	 *            the list of variables.
	 * @return the Snapshot of the differences between the two input states.
	 */
	private static Snapshot getTheDifferences(State currentState,
			State previousState, EList<Variable> variables) {

		Snapshot snapshot = makeSnapshot();
		if (previousState != null) {
			for (Variable variable : variables) {
				String name = variable.getName();

				AbstractEvalResult currentValue = currentState.eval(name);
				if (currentValue instanceof IdentifierNotInitialised) {
					continue;
				}
				AbstractEvalResult previousValue = previousState.eval(name);
				if (!currentValue.equals(previousValue)) {
					String currentStr = currentValue.toString();
					String previousStr = previousValue.toString();
					if (Utils.DEBUG) {
						Utils.debug(name + ": " + previousStr + " => "
								+ currentStr);
					}
					snapshot.getValues().put(name, currentStr);
				}
			}
		}

		return snapshot;
	}

	/**
	 * Utility method to make an (empty) snapshot.
	 * 
	 * @return the newly created snapshot.
	 */
	private static Snapshot makeSnapshot() {
		Snapshot snapshot = OracleFactory.eINSTANCE.createSnapshot();
		return snapshot;
	}

	/**
	 * Utility method to get a step from the current trace element.
	 * 
	 * @param traceElement
	 *            the current trace element.
	 * @return the step extracted from the input trace element.
	 */
	private static Step getStep(TraceElement traceElement) {
		Transition transition = traceElement.getTransition();
		if (transition == null)
			return null;
		Step step = makeStep();
		step.setName(transition.getName());
		List<String> params = transition.getParams();
		step.getArgs().addAll(params);
		return step;
	}

	/**
	 * Utility method to create a new step.
	 * 
	 * @return the newly created step.
	 */
	private static Step makeStep() {
		Step step = OracleFactory.eINSTANCE.createStep();
		return step;
	}

	/**
	 * Utility method to create a run.
	 * 
	 * @return the newly created run.
	 */
	public static Run makeRun() {
		Run run = OracleFactory.eINSTANCE.createRun();
		run.setName("Name");
		return run;
	}

	/**
	 * @param result
	 */
	public static void handleResult(SIM_RESULT result) {
		String msg = getExplanation(result);
		MessageDialog.openError(Display.getDefault().getActiveShell(),
				"Unexpected result", msg);
	}

	/**
	 * @param result
	 * @return
	 */
	private static String getExplanation(SIM_RESULT result) {
		switch (result) {
		case PLAYBACK_NO_FURTHER_TRANSITION:
			return "No further transition to playback";
		case PLAYBACK_TRANSITION_NOT_ENABLED:
			return "Playback transition is not enabled";
		case RECORDING_DEADLOCK:
			return "The system is deadlocked";
		case TIMEOUT:
			return "Time out";
		default:
			return "OK";
		}
	}

	/**
	 * @param trace
	 */
	public static void ProB2SetCurrentAnimation(Trace trace) {
		Injector injector = Main.getInjector();
		AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);
		selector.traceChange(trace);
	}

	/**
	 * @param transitions
	 * @param emfMch
	 * @return
	 */
	public static boolean deterministic(Set<Transition> transitions,
			Machine emfMch) {
		EList<AbstractExtension> exts = emfMch.getExtensions();
		// go through each extension and find components and map to their
		// eventNames.
		for (AbstractExtension ext : exts) {
			if (ext instanceof Component) {
				Component topComponent = (Component) ext;
				EList<Component> components = topComponent.getComponents();
				for (Component component : components) {
					List<String> evtNames = getOperationsName(component);

					for (String evtName : evtNames) {
						int found = 0;
						for (Transition transition : transitions) {
							String name = transition.getName();
							if (evtName.equals(name)) {
								found++;
							}
							if (found > 1)
								return false;
						}
					}

				}
			}
		}
		return true;
	}

	/**
	 * @param component
	 * @return
	 */
	private static List<String> getOperationsName(Component component) {
		List<String> evtNames = new ArrayList<String>();
		EList<AbstractComponentOperation> operationsList = component
				.getOperations();
		for (AbstractComponentOperation op : operationsList) {
			if (!(op instanceof External)) {
				EList<Event> elaborates = op.getElaborates();
				for (Event evt : elaborates) {
					evtNames.add(evt.getName());
				}
			}
		}
		return evtNames;
	}

	/**
	 * @param trace
	 * @return
	 */
	public static Snapshot getSnapshot(Trace trace, EList<Variable> variables) {
		TraceElement traceElement = trace.getCurrent();
		return getSnapshot(traceElement, variables);
	}

	/**
	 * @param entry
	 * @param playbackSnapshot
	 * @return
	 */
	public static boolean sameSnapshot(Snapshot goldSnapshot,
			Snapshot playbackSnapshot) {
		EMap<String, String> goldValues = goldSnapshot.getValues();
		EMap<String, String> playbackValues = playbackSnapshot.getValues();
		if (goldValues.size() != playbackValues.size())
			return false;
		Set<String> keySet = goldValues.keySet();
		for (String key : keySet) {
			if (!goldValues.get(key).equals(playbackValues.get(key)))
				return false;
		}
		return true;
	}

}
