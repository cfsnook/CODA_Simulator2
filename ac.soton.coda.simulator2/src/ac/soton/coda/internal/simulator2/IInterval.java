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
 * An interface for intervals (from <code>min</code> to <code>max</code>).
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see Interval
 * @since 0.2
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IInterval {

	/**
	 * Returns the lower-bound (<code>min</code>) of the interval.
	 * 
	 * @return the lower-bound of the interval.
	 */
	public String getMin();

	/**
	 * Returns the upper-bound (<code>max</code>) of the interval.
	 * 
	 * @return the upper-bound of the interval.
	 */
	public String getMax();
	
}
