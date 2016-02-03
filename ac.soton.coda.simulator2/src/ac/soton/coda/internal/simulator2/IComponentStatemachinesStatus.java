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

import java.util.Set;

import ac.soton.eventb.emf.components.Component;

/**
 * <p>
 * A common interface for status of a component's statemachines. Each status
 * contains a map from the statemachines' variable to their values.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see ComponentStatemachinesStatus
 * @since 0.2
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IComponentStatemachinesStatus {

	/**
	 * Add a pair of a variable and its value to the map.
	 * 
	 * @param statemachine
	 *            the statemachine variable.
	 * @param value
	 *            the value of the variable.
	 */
	public void addStatemachine(String statemachine, String value);

	/**
	 * Return the set of statemachines of the component.
	 * 
	 * @return the set of statemachines of the component.
	 */
	public Set<String> getStatemachines();

	/**
	 * Return the component.
	 * 
	 * @return the component.
	 */
	public Component getComponent();

	/**
	 * Return the value of a variable of the component.
	 * 
	 * @param statemachine
	 *            the statemachine.
	 * @return the value of the statemachine.
	 */
	public String getValue(String statemachine);

}
