package org.zkoss.zest.examples.hello;

/**
 * The Hello World action.
 * @author tomyeh
 */
public class HelloWorldAction {
	private String _message = "Welcome";
	public String execute() {
		return "success";
	}
	/** Sets the message.
	 */
	public void setMessage(String message) {
		_message = message;
	}
	/** Returns the message.
	 */
	public String getMessage() {
		return _message;
	}
}
