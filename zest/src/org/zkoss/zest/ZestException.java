/* ZestException.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar  3 12:37:07 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest;

import org.zkoss.lang.SystemException;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Expectable;

/**
 * Represents a ZEST runtime exception.
 * 
 * @author tomyeh
 */
public class ZestException extends SystemException {
	/** Utilities to wrap {@link ZestException}.
	 *
	 * <p>The reason to use a class to hold static utilities is we can
	 * override the method's return type later.
	 */
	public static class Aide {
		/** Converts an exception to ZestException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ZestException wrap(Throwable t) {
			return (ZestException)
				Exceptions.wrap(Exceptions.unwrap(t), ZestException.class);
		}
		/** Converts an exception to ZestException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ZestException wrap(Throwable t, String msg) {
			return (ZestException)
				Exceptions.wrap(Exceptions.unwrap(t), ZestException.class, msg);
		}
		/** Converts an exception to ZestException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ZestException wrap(Throwable t, int code, Object[] fmtArgs) {
			return (ZestException)
				Exceptions.wrap(Exceptions.unwrap(t), ZestException.class, code, fmtArgs);
		}
		/** Converts an exception to ZestException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ZestException wrap(Throwable t, int code, Object fmtArg) {
			return (ZestException)
				Exceptions.wrap(Exceptions.unwrap(t), ZestException.class, code, fmtArg);
		}
		/** Converts an exception to ZestException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ZestException wrap(Throwable t, int code) {
			return (ZestException)
				Exceptions.wrap(Exceptions.unwrap(t), ZestException.class, code);
		}
	}

	public ZestException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public ZestException(String s) {
		super(s);
	}
	public ZestException(Throwable cause) {
		super(cause);
	}
	public ZestException() {
	}

	public ZestException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public ZestException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public ZestException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public ZestException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public ZestException(int code, Throwable cause) {
		super(code, cause);
	}
	public ZestException(int code) {
		super(code);
	}
}
