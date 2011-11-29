/* ActionType.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 29 12:33:11 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the detailed information about an action.
 * @author tomyeh
 * @since 1.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ActionType {
	/** Used to indicate the request's paramters shall not be converted to
	 * the action.
	 * @see ParameterIgnored
	 */
	public boolean parameterIgnored() default false;
}
