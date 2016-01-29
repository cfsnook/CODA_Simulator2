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
public class AbstractObjectTimeValueStatus implements IObjectTimeStatus {

	// The object
	private Object object;
	
	// The time
	private int time;

	// The value
	private Object value;
	
	public AbstractObjectTimeValueStatus(Object object, int time, Object value) {
		this.object = object;
		this.time = time;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see ac.soton.coda.internal.simulator2.views.IObjectTimeStatus#getTime()
	 */
	@Override
	public int getTime() {
		return time;
	}

	/* (non-Javadoc)
	 * @see ac.soton.coda.internal.simulator2.views.IObjectTimeStatus#getObject()
	 */
	@Override
	public Object getObject() {
		return object;
	}

	/* (non-Javadoc)
	 * @see ac.soton.coda.internal.simulator2.views.IObjectTimeStatus#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}
}
