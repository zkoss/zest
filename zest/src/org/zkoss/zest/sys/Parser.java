/* Parser.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar  7 12:16:31 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys;

import java.net.URL;

import org.zkoss.util.resource.Locator;

/**
 * The parser used to parse the configuration file, WEB-INF/zest.xml.
 *
 * @author tomyeh
 */
public interface Parser {
	/** Parses the specified configuration file.
	 * @param url the configuration file's URL
	 * @param loc the locator used to locate the associated resources.
	 * @return the configuration.
	 */
	public Configuration parse(URL url, Locator loc)
	throws Exception;
}
