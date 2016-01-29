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

package ac.soton.coda.internal.simulator2.views;

import ac.soton.eventb.emf.components.Component;

/**
 * <p>
 * A class for variables status. Each instance is a tuple
 * <code>(component, variable name, variable value)</code>.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @since 0.1
 */
public class StatemachineValueStatus implements IObjectVariableValueStatus {

	private Component component;
	
	private String variable;
	
	private String value;
	
	/**
	 * @param component
	 * @param variable
	 * @param value
	 */
	public StatemachineValueStatus(Component component, String variable, String value) {
		this.component = component;
		this.variable = variable;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see ac.soton.coda.internal.simulator2.views.IObjectVariableValueStatus#getVariable()
	 */
	@Override
	public String getVariable() {
		return variable;
	}

	/* (non-Javadoc)
	 * @see ac.soton.coda.internal.simulator2.views.IObjectVariableValueStatus#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}

	public String toString() {
		return component.getName();
	}
}
