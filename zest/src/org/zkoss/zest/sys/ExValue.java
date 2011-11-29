/* ExValue.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar  8 16:36:30 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys;

import org.zkoss.lang.Classes;
import org.zkoss.xel.Expression;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.XelException;

import org.zkoss.lang.Objects;
import org.zkoss.zest.ActionContext;

/**
 * Used to represent a string value that might contain an expression.
 * It is serializable and the expression is parsed by demand.
 * 
 * @author tomyeh
 */
public class ExValue implements java.io.Serializable {
	private String _value;
	private Class _expected;
	/** Coerced value. Used only if _expr is DUMMY_EXPRESSION. */
	private transient Object _coercedVal = Objects.UNKNOWN;
	private transient Expression _expr;

	/** Constructor.
	 * @param value the value. It can be null.
	 */
	public ExValue(String value, Class expectedType) {
		if (expectedType == null)
			throw new IllegalArgumentException();
		_value = value;
		_expected = expectedType;
	}

	/** Tests whether it is an expression.
	 * Note: it is a wild guess. In other words, it returns false
	 * only if 100% not an expression.
	 */
	public boolean isExpression() {
		return _expr == null ? _value != null && _value.indexOf("${") >= 0:
			_expr != Expressions.DUMMY_EXPRESSION;
	}

	/** Returns the raw value.
	 * The raw value is the value passed to the constructor.
	 * That is, it might contain EL expressions.
	 */
	public final String getRawValue() {
		return _value;
	}
	/** Sets the raw value.
	 * @param value the value. It can be null.
	 */
	public void setRawValue(String value) {
		if (!Objects.equals(value, _value)) {
			_value = value;
			_expr = null;
			_coercedVal = Objects.UNKNOWN;
		}
	}
	/** Returns the expected type.
	 */
	public final Class getExpectedType() {
		return _expected;
	}
	/** Sets the expected type.
	 */
	public final void setExpectedType(Class expectedType) {
		if (expectedType == null)
			throw new IllegalArgumentException();

		if (_expected != expectedType) {
			_expected = expectedType;
			if (_expr != Expressions.DUMMY_EXPRESSION) _expr = null; //re-parse
			_coercedVal = Objects.UNKNOWN;
		}
	}

	/** Returns the value after evaluation.
	 */
	public Object getValue(ActionContext ac)
	throws XelException {
		if (_expr == null)
			if (_value != null && _value.indexOf("${") >= 0)
				_expr = ac.parseExpression(_value, _expected);
			else
				_expr = Expressions.DUMMY_EXPRESSION; //to denote not-an-expr
		return _expr == Expressions.DUMMY_EXPRESSION ? coerce(): ac.evaluate(_expr);
	}
	private Object coerce() {
		if (_coercedVal == Objects.UNKNOWN)
			_coercedVal = Classes.coerce(_expected, _value);
		return _coercedVal;
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		_coercedVal = Objects.UNKNOWN;
	}

	public String toString() {
		return _value;
	}
	public int hashCode() {
		return Objects.hashCode(_value);
	}
	public boolean equals(Object o) {
		if (o instanceof ExValue) {
			final ExValue val = (ExValue)o;
			return Objects.equals(val._value, _value)
				&& Objects.equals(val._expected, _expected);
		}
		return false;
	}
}
