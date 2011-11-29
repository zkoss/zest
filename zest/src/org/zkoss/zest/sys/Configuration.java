/* Configuration.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar  7 17:50:22 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys;

import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;

/**
 * Represents the ZEST configuration.
 * @author tomyeh
 */
public interface Configuration {
	/** Returns an array of {@link ActionDefinition} that have been defined
	 */
	public ActionDefinition[] getActionDefinitions();
	/** Returns the extensions that will be processed.
	 * If a request's path does not match any of the extensions,
	 * it will be ignored by ZEST (and handled as if ZEST is not
	 * available).
	 */
	public String[] getExtensions();
	/** Returns the error handler, or null if not available.
	 * Then, the exception will be thrown up to the container.
	 */
	public ErrorHandler getErrorHandler();
	/** Returns the function mapper defined in the configuration, or null
	 * if not available.
	 */
	public FunctionMapper getFunctionMapper();
	/** Returns the variable resolver defined in the configuration, or null
	 * if not available.
	 */
	public VariableResolver getVariableResolver();
}
