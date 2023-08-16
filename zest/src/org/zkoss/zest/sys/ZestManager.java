/* ZestManager.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar  3 12:12:24 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys;

import java.util.Map;
import java.util.Iterator;
import java.net.URL;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.reflect.Fields;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.util.resource.ServletContextLocator;

import org.zkoss.zest.ActionContext;
import org.zkoss.zest.ZestException;
import org.zkoss.zest.ParameterIgnored;
import org.zkoss.zest.annotation.ActionType;
import org.zkoss.zest.sys.impl.ActionContextImpl;

/**
 * The core of ZEST that matches URL, instantiates actions, invokes actions and
 * forwards to a view.
 *
 * <p>By default, the manager ignores a path if its extension does not
 * match any of the allowed extensions. If you'd like more complex
 * algorithm to decide which to ignore, you could override {@link #pathIgnored}.
 *
 * @author tomyeh
 */
public class ZestManager {
	private static final Logger log = LoggerFactory.getLogger(ZestManager.class);
	private static final String ATTR_MANAGER = "org.zkoss.zest.sys.manager";

	private ServletContext _ctx;
	private Configuration _config;
	private String[] _exts;

	/** Returns the manager associated with the context, or null
	 * if not initialized yet.
	 */
	public static ZestManager getManager(ServletContext ctx) {
		return (ZestManager)ctx.getAttribute(ATTR_MANAGER);
	}

	/** Constructor.
	 */
	public ZestManager() {
	}
	/** Initializes the manager.
	 *
	 * @param parser the parser used to parse the configuration file (/WEB-INF/zest.xml)
	 */
	public void init(ServletContext ctx, Parser parser) {
		_ctx = ctx;
		loadConfiguration(parser, "/WEB-INF/zest.xml");
		ctx.setAttribute(ATTR_MANAGER, this);
		log.info("ZEST initialized");
	}
	/** Destroyes the manager.
	 */
	public void destroy() {
	}
	/** Returns the servlet context that this manager is associated with
	 * @since 1.1.0
	 */
	public ServletContext getServletContext() {
		return _ctx;
	}
	/** Loads the configuration.
	 * This method is usually called automatically.
	 * However, you could invoke it if you'd like to reload the configuration
	 * (such as when you modify <code>/WEB-INF/zest.xml</code>)
	 * @param configURI the URI of the configuration file, such as
	 * (<code>/WEB-INF/zest.xml</code>).
	 */
	public void loadConfiguration(Parser parser, String configURI) {
		try {
			final URL url = _ctx.getResource(configURI);
			if (url == null) {
				_config = null;
				log.info("File not found: "+ configURI);
			} else {
				_config = parser.parse(url, new ServletContextLocator(_ctx));
			}
		} catch (Throwable ex) {
			throw ZestException.Aide.wrap(ex, "Unable to load " + configURI);
		}
	}
	/** Handles the action.
	 * It first identifies any action that matches the request, and then
	 * instantiates/invoke the matched action and forward to the view.
	 * @return whether it is mapped to an action (and then handled).
	 */
	public boolean action(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		if (_config == null)
			return false;

		String s = request.getPathInfo();
		if (s == null || s.length() == 0)
			s = request.getServletPath();
		if (pathIgnored(s, _config.getExtensions()))
			return false;

		final ActionContext ac = new ActionContextImpl(request, response,
			_config.getVariableResolver(), _config.getFunctionMapper());
		final ActionDefinition[] defs = _config.getActionDefinitions();
		for (int j = 0; j < defs.length; ++j) {
			final ActionDefinition def = defs[j];
			Object action = null;
			try {
				action = def.getAction(ac);
				if (action != null) {
					request.setAttribute("action", action);
					if (!parameterIgnored(action))
						coerceParameters(ac, action);
					final String result = def.execute(ac, action);
					request.setAttribute("result", result);
					final ViewInfo viewInfo = def.getViewInfo(ac, result);
					if (viewInfo == null)
						throw new ZestException("No information specified for "+action+" under result is "+result+", when handling "+ac.getRequestPath());
					switch (viewInfo.getViewType()) {
					case REDIRECT:
						Https.sendRedirect(_ctx, request, response, viewInfo.getURI(), null, 0);
						break;
					case ERROR:
						final String msg = viewInfo.getErrorMessage();
						if (msg != null)
							response.sendError(viewInfo.getErrorCode(), msg);
						else
							response.sendError(viewInfo.getErrorCode());
					case DONE:
						break;
					default:
						Https.forward(_ctx, request, response, viewInfo.getURI());
						break;
					}
					return true;
				}
			} catch (Throwable ex) {
				try {
					_config.getErrorHandler().onError(ac, action, ex);
				} catch (ServletException t) {
					throw (ServletException)t;
				} catch (IOException t) {
					throw (IOException)t;
				} catch (Throwable t) {
					throw ZestException.Aide.wrap(t, "Failed to handle "+ac.getRequestPath());
				}
			}
		}
		return false;
	}
	private static boolean parameterIgnored(Object action) {
		if (action instanceof ParameterIgnored)
			return true;
		ActionType annot = action.getClass().getAnnotation(ActionType.class);
		return annot != null && annot.parameterIgnored();
	}
	/** Coerces the request's parameters to action's corresponding fields.
	 */
	protected void coerceParameters(ActionContext ac, Object action)
	throws Throwable {
		for (Iterator it = ac.getServletRequest().getParameterMap().entrySet().iterator();
		it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String nm = (String)me.getKey();
			final String[] vals = (String[])me.getValue();
			for (int j = 0; j < vals.length; ++j) {
				try {
					Fields.setByCompound(action, nm, vals[j], true);
				} catch (Throwable ex) {
					_config.getErrorHandler()
						.onParamError(ac, action, nm, vals[j], ex);
				}
			}
		}
	}
	private boolean pathIgnored(String path, String[] allowedExts) {
		String ext = "";
		for (int j = path.length(); --j >= 0;) {
			final char cc = path.charAt(j);
			if (cc == '.') {
				ext = path.substring(j);
				break;
			}
			if (cc == '/')
				break; //no extension
		}
		return pathIgnored(path, ext, allowedExts);
	}	
	/** Returns whether the given path shall be ignored.
	 * <p>Default: the path is ignored if the extension does not match one of
	 * the given allowed extensions.
	 * @param path the given path to test
	 * @param extension the extension of the path (it is part of path).
	 * It is an empty string if there is no extension.
	 * @param allowedExts the allowed extension. If null or zero-length,
	 * it means all paths are allowed.
	 * @since 1.1.0
	 */
	protected
	boolean pathIgnored(String path, String extension, String[] allowedExts) {
		if (allowedExts != null)
			for (int j = 0; j < allowedExts.length; ++j)
				if (extension.equals(allowedExts[j]))
					return false; //matached
		return true;
	}
}
