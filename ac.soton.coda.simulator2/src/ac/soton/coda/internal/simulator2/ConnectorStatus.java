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
 * An implementation of {@link IConnectorStatus} by extending
 * {@link AbstractObjectStatus}.
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @see Connector
 * @since 0.1
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ConnectorStatus extends AbstractObjectStatus implements
		IConnectorStatus {

	// The connector.
	private Connector connector;

	/**
	 * The constructor to create a connector status for a given connector with
	 * an empty map of time-value pairs.
	 * 
	 * @param connector
	 *            the connector.
	 */
	public ConnectorStatus(Connector connector) {
		super();
		this.connector = connector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConnectorStatus#getConnector()
	 */
	@Override
	public Connector getConnector() {
		return connector;
	}

}
