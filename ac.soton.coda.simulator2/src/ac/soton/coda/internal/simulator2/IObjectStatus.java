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

/**
 * <p>
 * A common interface for object status with (time, value)-pairs. Each object
 * status contains a map from time (Integer value) to the value of the object
 * (in String) at that time.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see AbstractObjectStatus
 * @since 0.1
 */
public interface IObjectStatus {

	/**
	 * Adds a pair of time-value to the map.
	 * 
	 * @param time
	 *            the time point.
	 * @param value
	 *            the value of the connector at the time.
	 */
	public void addValue(int time, String value);

	/**
	 * Returns the list of time points.
	 * 
	 * @return the list of time points.
	 */
	public Integer[] getTimes();

	/**
	 * Returns the value of the connector at a given time.
	 * 
	 * @param time
	 *            the time point.
	 * @return the value of the connector at the given time.
	 */
	public String getValue(int time);

}