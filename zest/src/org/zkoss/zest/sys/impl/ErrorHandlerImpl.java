/* ErrorHandlerImpl.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 11 11:56:47 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys.impl;

import org.zkoss.zest.ActionContext;
import org.zkoss.zest.sys.ErrorHandler;

/**
 * The default implementation of {@link ErrorHandler}.
 *
 * @author tomyeh
 */
public class ErrorHandlerImpl implements ErrorHandler {
	/** Handles the error.
	 * <p>Default: re-throw ex
	 */
	@Override
	public void onError(ActionContext ac, Object action, Throwable ex)
	throws Throwable {
		throw ex;
	}
	/** Handles the error when processing the request's parameter.
	 * <p>Default: does nothing.
	 */
	@Override
	public void onParamError(ActionContext ac, Object action,
	String name, String value, Throwable ex)
	throws Throwable {
	}
}
