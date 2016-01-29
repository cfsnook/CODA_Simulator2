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

import ac.soton.eventb.emf.components.Connector;

/**
 * <p>
 * An abstract class for connector/time status. Each instance is a tuple
 * <code>(connector, time, value)</code>.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see
 * @since 0.1
 */
public abstract class ConnectorTimeValueStatus extends AbstractObjectTimeValueStatus implements
		IObjectTimeStatus {

	/**
	 * Constructor to create an object with given connector, time, and value.
	 * 
	 * @param connector
	 *            the connector.
	 * @param time
	 *            the time.
	 * @param value
	 *            the value.
	 */
	public ConnectorTimeValueStatus(Connector connector, int time, String value) {
		super(connector, time, value);
	}

	/**
	 * Returns the connector's name.
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		Connector connector = (Connector) this.getObject();
		return connector.getName();
	}

}
