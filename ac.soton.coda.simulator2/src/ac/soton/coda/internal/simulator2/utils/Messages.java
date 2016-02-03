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

import org.eclipse.osgi.util.NLS;

/**
 * A class managing extenalised strings.
 * 
 * @author htson
 * @version 0.1
 * @see NLS
 * @since 0.1
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ac.soton.coda.internal.simulator2.utils.messages"; //$NON-NLS-1$

	
	// Views
	public static String COLUMN_TITLE_COMPONENT;
	public static String COLUMN_TITLE_CONNECTOR;
	public static String COLUMN_TITLE_ENABLED_OPERATION;
	public static String COLUMN_TITLE_TIME;
	public static String COLUMN_TITLE_VALUE;
	public static String COLUMN_TITLE_VARIABLE;
	public static String EMPTY_CONNECTOR_NAME;
	public static String PREVIOUS;
	public static String NOW;
	public static String NULL_COMPONENT_LABEL;
	public static String UNINITIALISED;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Private constructor. 
	 */
	private Messages() {
	}

}
