/* ZestFilter.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar  3 09:52:16 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;

import org.zkoss.zest.sys.Parser;
import org.zkoss.zest.sys.impl.ParserImpl;

/**
 * The ZEST filter to match URL, instantiate actions, invoke actions and
 * forward to a view.
 *
 * <p>It provides better ecapsulation. This filter is actually a thin layer
 * on top of ZestManager.
 *
 * @author tomyeh
 */
public class ZestFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(ZestManager.class);

	private ZestManager _manager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
	FilterChain chain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest)
		|| !_manager.action((HttpServletRequest)request, (HttpServletResponse)response))
			chain.doFilter(request, response);
	}
	@Override
	public void destroy() {
		_manager.destroy();
		_manager = null;
	}
	@Override
	public final void init(FilterConfig config) throws ServletException {
		final ServletContext ctx = config.getServletContext();
		final ZestManager oldManager = ZestManager.getManager(ctx);

		//prepare parser
		final Parser parser;
		String clsnm = config.getInitParameter("parser-class");
		parser = clsnm != null ?
			(Parser)newInstance(clsnm, Parser.class): new ParserImpl();

		//prepare manager
		clsnm = config.getInitParameter("manager-class");
		_manager = clsnm != null ?
			(ZestManager)newInstance(clsnm, ZestManager.class): new ZestManager();
		_manager.init(ctx, parser);

		if (oldManager != null)
			log.warn(oldManager+" is replaced"+_manager);
	}
	private static Object newInstance(String clsnm, Class klass) throws ServletException {
		final Object o;
		try {
			o = Classes.newInstanceByThread(clsnm);
		} catch (Throwable t) {
			throw new ServletException("Unable to instantiate "+clsnm, t);
		}
		if (!klass.isInstance(o))
			throw new ServletException(o+" not an instance of "+klass);
		return o;
	}
}
