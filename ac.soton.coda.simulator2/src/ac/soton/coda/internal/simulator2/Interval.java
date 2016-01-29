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
 * An implementation of {@link IInterval}.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see
 * @since 0.2
 */
public class Interval implements IInterval {

	private String min;
	
	private String max;
	
	/**
	 * @param min
	 * @param max
	 */
	public Interval(String min, String max) {
		this.min = min;
		this.max = max;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IInterval#getMin()
	 */
	@Override
	public String getMin() {
		return min;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IInterval#getMax()
	 */
	@Override
	public String getMax() {
		return max;
	}

}
