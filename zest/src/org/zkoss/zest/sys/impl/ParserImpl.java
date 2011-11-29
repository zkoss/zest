/* ParserImpl.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar  7 13:04:27 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zest.sys.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;

import org.zkoss.lang.Classes;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.logging.Log;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.Item;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.taglib.Taglibs;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.util.MethodFunction;

import org.zkoss.zest.sys.*;
import org.zkoss.zest.ZestException;

/**
 * The default implementation of the parser of the configuration file
 * (/WEB-INF/zest.xml).
 * @author tomyeh
 */
public class ParserImpl implements Parser {
	private static final Log log = Log.lookup(ParserImpl.class);

	public ParserImpl() {
	}

	@Override
	public Configuration parse(URL url, Locator loc) throws Exception {
		try {
			return parse(new SAXBuilder(true, false, true).build(url).getRootElement(), loc);
		} catch (Throwable ex) {
			log.realCauseBriefly("Failed to parse "+url, ex);
			if (ex instanceof Error) throw (Error)ex;
			throw (Exception)ex;
		}
	}
	/** Parses the specified root element.
	 */
	public Configuration parse(Element root, Locator loc) throws Exception {
		final List<ActionDefinition> defs = new LinkedList<ActionDefinition>();
		final List<Taglib> taglibs = new LinkedList<Taglib>();
		final List<Object[]> xelmtds = new LinkedList<Object[]>();
		final List<VariableResolver> resolvers = new LinkedList<VariableResolver>();
		String[] exts = null;
		ErrorHandler errh = null;
		for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String elnm = el.getName();
			if ("action".equals(elnm)) {
				defs.add(parseAction(el));
			} else if ("error-handler-class".equals(elnm)) {
				final String clsnm = el.getText(true);
				noELnorEmpty(elnm, clsnm, el);
				errh = (ErrorHandler)Classes.newInstanceByThread(clsnm);
			} else if ("extensions".equals(elnm)) {
				final String s = el.getText(true);
				noEL(elnm, s, el);
				exts = parseExtensions(s);
			} else if ("taglib".equals(elnm)) {
				final String uri = IDOMs.getRequiredAttributeValue(el, "uri");
				noELnorEmpty("uri", uri, el);
				final String prefix = IDOMs.getRequiredAttributeValue(el, "prefix");
				noELnorEmpty("prefix", prefix, el);
				taglibs.add(new Taglib(prefix, uri));
			} else if ("variable-resolver".equals(elnm)) {
				final String clsnm = IDOMs.getRequiredAttributeValue(el, "class");
				noELnorEmpty("class", clsnm, el);
				resolvers.add(0, (VariableResolver)Classes.newInstanceByThread(clsnm)); //FILO
			} else if ("xel-method".equals(elnm)) {
				parseXelMethod(xelmtds, el);
			}
		}
		return new ConfigurationImpl(
			defs.toArray(new ActionDefinition[defs.size()]),
			exts != null ? exts: new String[] {""}, errh,
			ChainedResolver.getVariableResolver(resolvers),
			!taglibs.isEmpty() || !xelmtds.isEmpty() ?
				Taglibs.getFunctionMapper(taglibs, null, xelmtds, loc): null);
	}
	//parse action
	private static ActionDefinition parseAction(Element el)
	throws Exception {
		final Map<String, ViewInfoProxy> results = new HashMap<String, ViewInfoProxy>();
		for (Iterator it = el.getElements("result").iterator(); it.hasNext();) {
			final Element e = (Element)it.next();
			results.put(e.getAttributeValue("name"),
				new ViewInfoProxy(e.getAttributeValue("type"), e.getText(true)));
		}
		final String path = IDOMs.getRequiredAttributeValue(el, "path");
		noELnorEmpty("path", path, el);
		return new ActionDefinitionImpl(path,
			IDOMs.getRequiredAttributeValue(el, "class"),
			el.getAttributeValue("method"), results);
	}
	/** Parse the XEL method. */
	private static void parseXelMethod(List<Object[]> xelmtds, Element el)
	throws Exception {
		final String prefix = IDOMs.getRequiredAttributeValue(el, "prefix");
		noELnorEmpty("prefix", prefix, el);
		final String nm = IDOMs.getRequiredAttributeValue(el, "name");
		noELnorEmpty("name", nm, el);
		final String clsnm = IDOMs.getRequiredAttributeValue(el, "class");
		noELnorEmpty("class", clsnm, el);
		final String sig = IDOMs.getRequiredAttributeValue(el, "signature");
		noELnorEmpty("signature", sig, el);

		final Method mtd;
		try {
			final Class cls = Classes.forNameByThread(clsnm);
			mtd = Classes.getMethodBySignature(cls, sig, null);
		} catch (ClassNotFoundException ex) {
			throw new ZestException("Class not found: "+clsnm+", "+el.getLocator());
		} catch (Exception ex) {
			throw new ZestException("Method not found: "+sig+" in "+clsnm+", "+el.getLocator());
		}
		if ((mtd.getModifiers() & Modifier.STATIC) == 0)
			throw new ZestException("Not a static method: "+mtd+", "+el.getLocator());
		xelmtds.add(new Object[] {prefix, nm, new MethodFunction(mtd)});
	}
	//parse extensions
	private static String[] parseExtensions(String extensions) {
		if (extensions == null || extensions.length() == 0)
			return null;
		final String[] exts = extensions.split(",");
		for (int j = 0; j < exts.length; ++j) {
			exts[j] = exts[j].trim();
			if (exts[j].length() > 0 && exts[j].charAt(0) != '.')
				exts[j] = '.' + exts[j];
		}
		return exts;
	}
	/** Whether a string is null or empty. */
	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	private static void noELnorEmpty(String nm, String val, Item item)
	throws ZestException {
		if (isEmpty(val))
			throw new ZestException(nm + " cannot be empty, "+item.getLocator());
		noEL(nm, val, item);
	}
	private static void noEL(String nm, String val, Item item)
	throws ZestException {
		if (val != null && val.indexOf("${") >= 0)
			throw new ZestException(nm+" does not support EL expressions, "+item.getLocator());
	}
}
/*package*/ class ChainedResolver implements VariableResolver {
	private VariableResolver[] _resolvers;
	
	/*package*/ static
	VariableResolver getVariableResolver(List<VariableResolver> resolvers) {
		int sz;
		if (resolvers == null || (sz = resolvers.size()) == 0)
			return null;
		return new ChainedResolver(resolvers.toArray(new VariableResolver[sz]));
	}
	private ChainedResolver(VariableResolver[] resolvers) {
		_resolvers = resolvers;
	}
	@Override
	public Object resolveVariable(String name) {
		for (int j = 0; j < _resolvers.length; ++j) {
			final Object o = _resolvers[j].resolveVariable(name);
			if (o != null)
				return o;
		}
		return null;
	}
}
