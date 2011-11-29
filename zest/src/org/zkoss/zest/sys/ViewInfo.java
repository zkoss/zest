/* ViewInfo.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 18:52:12 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys;

/** The information of the view.
 * @since 1.1.0
 * @author tomyeh
 */
public interface ViewInfo {
	/** Returns the type of the view. */
	public ViewType getViewType();
	/** Returns the URI to forward or redirect to. */
	public String getURI();
	/** Returns the error code. It is meaningful only if
	 * {@link #getViewType} is ERROR.
	 */
	public int getErrorCode();
	/** Returns the error message, or null if not associated with
	 * a custom error message. It is meaningful only if
	 * {@link #getViewType} is ERROR.
	 */
	public String getErrorMessage();

	/** The types of the view.
	 * @since 1.1.0
	 */
	public enum ViewType {
		/** Forward to the given URI. */
		FORWARD,
		/** Redirect to the given URI. */
		REDIRECT,
		/** An error occurs. */
		ERROR,
		/** Indicates the action generates the response directly
		 * and there is no to handle it further.
		 */
		DONE
	}
}