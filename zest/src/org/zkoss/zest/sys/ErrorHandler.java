/* ErrorHandler.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar  7 17:54:14 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys;

import org.zkoss.zest.ActionContext;

/**
 * The error handler for handling exceptions thrown
 * when processing a request.
 * @author tomyeh
 */
public interface ErrorHandler {
	/** Called when an exception is thrown.
	 * If the error handler cannot handle it, it has to re-throw the exception.
	 */
	public void onError(ActionContext ac, Object action, Throwable ex)
	throws Throwable;
	/** Called when an exception is thrown when coercing a request parameter to be stored
	 * in an action.
	 * If the error handler cannot handle it, it has to re-throw the exception.
	 * @param name the parameter's name
	 * @param value the parameter's value
	 * @param ex the exception being caught
	 */
	public void onParamError(ActionContext ac, Object action,
	String name, String value, Throwable ex)
	throws Throwable;
}
