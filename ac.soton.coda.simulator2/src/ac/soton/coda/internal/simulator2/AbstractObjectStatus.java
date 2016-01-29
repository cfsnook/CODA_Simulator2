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

/**
 * <p>
 * An abstract implementation of {@link IObjectStatus}.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see
 * @since 0.1
 */
public abstract class AbstractObjectStatus implements IObjectStatus {

	// The map of time-value pairs.
	private Map<Integer, String> values;

	/**
	 * A public default constructor to initialise the map of time-value pairs.
	 */
	public AbstractObjectStatus() {
		values = new HashMap<Integer, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IObjectStatus#addValue(int, String)
	 */
	public void addValue(int time, String value) {
		values.put(time, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IObjectStatus#getTimes()
	 */
	public Integer[] getTimes() {
		Set<Integer> keySet = values.keySet();
		return keySet.toArray(new Integer[keySet.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IObjectStatus#getValue(int)
	 */
	public String getValue(int time) {
		return values.get(time);
	}

}
