/* ViewInfoProxy.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun  1 09:12:20 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys.impl;

import org.zkoss.zest.ActionContext;
import org.zkoss.zest.ZestException;
import org.zkoss.zest.sys.ExValue;
import org.zkoss.zest.sys.ViewInfo;
import org.zkoss.zest.sys.ViewInfo.ViewType;

/**
 * Used to hold {@link ViewInfo} and evaluate the EL expressions it might have.
 * @author tomyeh
 * @since 1.1.0
 */
public class ViewInfoProxy {
	private final ExValue _type;
	private final ExValue _content;
	public ViewInfoProxy(String type, String content) {
		_type = new ExValue(type, String.class);
		_content = new ExValue(content, String.class);
	}
	public ViewInfo getViewInfo(ActionContext ac) {
		ViewType type;
		int errCode = 0;
		String uri = null, errMsg = null,
			stype = (String)_type.getValue(ac),
			content = (String)_content.getValue(ac);
		if (content == null)
			content = "";

		if (stype == null || stype.length() == 0 || "forward".equals(stype)) {
			type = ViewType.FORWARD;
			uri = content;
		} else if ("redirect".equals(stype)) {
			type = ViewType.REDIRECT;
			uri = content;
		} else if ("error".equals(stype)) {
			type = ViewType.ERROR;
			final int j = content.indexOf(":");
			final String code = (j >= 0 ? content.substring(0, j): content).trim();
			errMsg = j >= 0 ? content.substring(j + 1).trim(): null;
			try {
				errCode = Integer.parseInt(code);
			} catch (Throwable ex) {
				throw new ZestException("Unknown error: "+content);
			}
		} else if ("done".equals(stype)) {
			type = ViewType.DONE;
		} else {
			throw new ZestException("Unknown view type: "+stype);
		}
		return new VI(type, uri, errCode, errMsg);
	}
	public String toString() {
		return "[" + _type + ", " + _content + ']';
	}

	private static class VI implements ViewInfo {
		private final ViewType _type;
		private final String _uri;
		private final int _errCode;
		private final String _errMsg;

		private VI(ViewType type, String uri, int errCode, String errMsg) {
			_type = type;
			_uri = uri;
			_errCode = errCode;
			_errMsg = errMsg;
		}
		public ViewType getViewType() {
			return _type;
		}
		public String getURI() {
			return _uri;
		}
		public int getErrorCode() {
			return _errCode;
		}
		public String getErrorMessage() {
			return _errMsg;
		}
	}
}

