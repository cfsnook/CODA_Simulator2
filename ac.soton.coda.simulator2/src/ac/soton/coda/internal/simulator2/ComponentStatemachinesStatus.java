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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ac.soton.eventb.emf.components.Component;

/**
 * <p>
 * An implementation of {@link IComponentStatemachinesStatus}.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see Component
 * @since 0.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public final class ComponentStatemachinesStatus implements
		IComponentStatemachinesStatus {

	// The component.
	private Component component;

	// The mapping between the component's variables and their values.
	private Map<String, String> statemachinesValue;

	/**
	 * Constructor to create the component statemachines status for the given
	 * component.
	 * 
	 * @param component
	 *            the input component.
	 */
	public ComponentStatemachinesStatus(Component component) {
		this.component = component;
		statemachinesValue = new HashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IComponentStatus#addStatemachine(String, String)
	 */
	@Override
	public void addStatemachine(String statemachine, String value) {
		statemachinesValue.put(statemachine, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IComponentStatus#getStatemachines()
	 */
	@Override
	public Set<String> getStatemachines() {
		return statemachinesValue.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IComponentStatus#getComponent()
	 */
	@Override
	public Component getComponent() {
		return component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IComponentStatus#getValue(String)
	 */
	@Override
	public String getValue(String statemachine) {
		return statemachinesValue.get(statemachine);
	}

}
