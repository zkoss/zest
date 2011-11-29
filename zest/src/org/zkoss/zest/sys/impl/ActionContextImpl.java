/* ActionContextImpl.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar  7 15:05:27 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.xel.XelContext;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.Expression;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.SimpleXelContext;

import org.zkoss.zest.ActionContext;

/**
 * The default implementation of {@link ActionContext}.
 * @author tomyeh
 */
public class ActionContextImpl implements ActionContext {
	private ExpressionFactory _expf;
	private final HttpServletRequest _request;
	private final HttpServletResponse _response;
	private final FunctionMapper _mapper;
	private final ActionResolver _resolver;
	private final String _path;

	public ActionContextImpl(
	HttpServletRequest request, HttpServletResponse response,
	VariableResolver resolver, FunctionMapper mapper) {
		_request = request;
		_response = response;
		String path = request.getServletPath();
		if (path == null || path.length() == 0)
			path = "/"; //in case that some container might not follow spec
		String pi = request.getPathInfo();
		if (pi != null && pi.length() > 0) {
			if (pi.charAt(0) == '/')
				path = path.charAt(path.length() - 1) == '/' ?
					path + pi.substring(1): path + pi;
			else
				path = path.charAt(path.length() - 1) == '/' ?
					path + pi: path + '/' + pi;
		}
		_path = path;
		_mapper = mapper;
		_resolver = new ActionResolver(resolver);
	}
	@Override
	public HttpServletRequest getServletRequest() {
		return _request;
	}
	@Override
	public HttpServletResponse getServletResponse() {
		return _response;
	}
	@Override
	public String getRequestPath() {
		return _path;
	}
	@Override
	public Expression parseExpression(String expression, Class expectedType)
	throws XelException {
		return getExpressionFactory()
			.parseExpression(newXelContext(), expression, expectedType);
	}
	@Override
	public Object evaluate(Expression expression)
	throws XelException {
		return expression.evaluate(newXelContext());
	}

	/** Returns the expression factory. */
	protected ExpressionFactory getExpressionFactory() {
		if (_expf == null)
			_expf = Expressions.newExpressionFactory();
		return _expf;
	}
	@Override
	public FunctionMapper getFunctionMapper() {
		return _mapper;
	}
	@Override
	public VariableResolver getVariableResolver() {
		return _resolver;
	}
	/** Instantiate a XEL context.
	 * Don't reuse it since it has attributes (that shall not be kept
	 * after evaluation).
	 */
	private XelContext newXelContext() {
		return new SimpleXelContext(getVariableResolver(), getFunctionMapper());
	}

	private class ActionResolver implements VariableResolver {
		private final VariableResolver _resolver;
		private ActionResolver(VariableResolver resolver) {
			this._resolver = resolver;
		}
		public Object resolveVariable(String name) {
			if ("request".equals(name))
				return _request;
			final Object o =_request.getAttribute(name);
			return o != null ? o:
				this._resolver != null ? this._resolver.resolveVariable(name): null;
		}
	}
}
