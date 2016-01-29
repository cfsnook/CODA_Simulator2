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
 *
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since
 */
public class ConnectorFutureValueStatus extends ConnectorTimeValueStatus
		implements IObjectTimeStatus {

	/**
	 * @param connector
	 * @param time
	 * @param value
	 */
	public ConnectorFutureValueStatus(Connector connector, int time,
			String value) {
		super(connector, time, value);
	}


}
