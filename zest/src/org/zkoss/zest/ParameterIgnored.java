/* ParameterIgnored.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 14 11:17:39 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest;

/**
 * A decorator interface that might be implemented by an action to indicate
 * the request's paramters shall not be converted to the action.
 * <p>Alernatively, you could specify the annotation called
 * {@link org.zkoss.zest.annotation.ActionType#parameterIgnored} as follows.
 * <pre><code>@ActionType(parameterIgnroed=true)
 *public class FooAction {
 *  ...</code></pre>
 *
 * <p>If this interface is not implemented by an action (default)
 * nor {@link org.zkoss.zest.annotation.ActionType#parameterIgnored} is specified,
 * ZEST will look for the setter method for each parameter, and then
 * invoke the method to store the parameter's value.
 * It is so-called the parameter conversion.
 *
 * @author tomyeh
 */
public interface ParameterIgnored {
}
