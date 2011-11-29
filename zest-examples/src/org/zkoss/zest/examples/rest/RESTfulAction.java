package org.zkoss.zest.examples.rest;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/**
 * RESTful Action.
 *
 * @author tomyeh
 */
public class RESTfulAction {
	private final List<String> _states = new LinkedList<String>();
	private String _content;
	private int _index = 0;

	public String get() {
		return "success";
	}
	public String delete() {
		if (_index < 0 || _index >= _states.size())
			return "failed: out of range";
		_states.remove(_index);
		return "success";
	}
	public String modify() {
		if (_index < 0 || _index >= _states.size())
			return "failed: out of range";
		_states.set(_index, _content);
		return "success";
	}
	public String add() {
		_states.add(_index > _states.size() ? _states.size(): _index, _content);
		return "success";
	}

	public List getStates() {
		return _states;
	}
	public void setIndex(int index) {
		_index = index;
	}
	public void setContent(String content) {
		_content = content;
	}

	//Utilities//
	/** Converting the request's method (POST, PUT, ADD) to a method's name.
	 */
	public static String toMethodName(String requestMethod) {
		requestMethod = _methods.get(requestMethod.toLowerCase());
		return requestMethod != null ? requestMethod: "get";
	}
	private static final Map<String, String> _methods = new HashMap<String, String>();
	static {
		_methods.put("delete", "delete");
		_methods.put("post", "add");
		_methods.put("put", "modify");
	}
}
