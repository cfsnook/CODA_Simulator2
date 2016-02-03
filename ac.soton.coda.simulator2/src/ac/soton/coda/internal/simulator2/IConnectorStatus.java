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

import ac.soton.eventb.emf.components.Connector;

/**
 * <p>
 * A common interface for connectors' status. This extends {@link IObjectStatus}
 * for the (time,value)-pairs of the connector status.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see ConnectorStatus
 * @since 0.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConnectorStatus extends IObjectStatus {

	/**
	 * Returns the connector.
	 * 
	 * @return the connector.
	 */
	public Connector getConnector();

}
