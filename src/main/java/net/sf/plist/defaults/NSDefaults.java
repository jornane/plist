/*
Property List Utility - LGPL 3.0 licensed
Copyright (C) 2012  Yørn de Jong

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3.0 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

File is part of the Property List project.
Project page on http://plist.sf.net/
*/
package net.sf.plist.defaults;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.TreeMap;

import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;
import net.sf.plist.io.PropertyListWriter;

/**
 * {@link NSDefaults} is an implementation of the <a href="http://en.wikipedia.org/wiki/Defaults_(software)">defaults system used in Mac OS X</a>.
 * The defaults system provides every program that uses it with a key/value store. Keys are {@link String}s, values are {@link NSObject}s.
 * {@link NSDefaults} behaves like {@link NSDictionary} in the sense that both represent key/value pairs in property list files,
 * but {@link NSDictionary} is linked to a specific file and is editable.<br><br>
 * 
 * It is possible to directly instantiate {@link NSDefaults} but this is discouraged.
 * The preferred way to instantiate {@link NSDefaults} is by using the {@link #getInstance(Object, Scope)} method,
 * which will instantiate {@link NSDefaults} for the domain the {@link Object} belongs to.<br><br>
 * 
 * In this implementation the package name is used to derive the domain.
 * The standard way to determine the domain is by taking the first three parts of the package name.
 * This behaviour can be overridden by using the {@link #addDomain(String)} method,
 * which will cause the domain to be "learned".<br>
 * If the beginning of a package name matches a learned domain, the learned domain is used.  
 */
public class NSDefaults extends TreeMap<String,NSObject> {
	private static final long serialVersionUID = 1L;
	
	/** Collection of domains */
	private final static HashSet<String> domains = new HashSet<String>();
	
	/** Instance of {@link OperatingSystemPath} for current operating system */
	public final static OperatingSystemPath OSPATH = OperatingSystemPath.getInstance();
	
	/** The Property List file used for reading and storing the preferences in this defaults instance */
	protected final File file;
	
	/**
	 * Construct using a fixed file.
	 * Invocation of this constructor is discouraged,
	 * use {@link #getInstance(Object, Scope)} instead.
	 * 
	 * @param file The PList file used, the file may be overwritten 
	 */
	public NSDefaults(File file) {
		this.file = file;
	}
	
	/**
	 * Write all changes to the Property List file
	 * @throws PropertyListException	constructing the Property List file failed
	 * @throws IOException	writing the file failed
	 */
	synchronized public void commit() throws PropertyListException, IOException {
		PropertyListWriter.write(new NSDictionary(this), file);
	}
	
	/**
	 * Re-read all information from the Property List file
	 */
	synchronized public void refresh() {
		putAll(getRoot(file).toMap());
	}
	
	/**
	 * Get the root element of a Property List {@link File}.
	 * The root element should be an {@link NSDictionary},
	 * if it is not, an empty {@link NSDictionary} is returned.
	 * 
	 * @param file	the Property List file
	 * @return	the root node or empty node
	 */
	private static NSDictionary getRoot(File file) {
		try {
			return (NSDictionary) PropertyListParser.parse(file);
		} catch (Exception e) {
			return new NSDictionary(new TreeMap<String, NSObject>());
		}
	}
	
	/**
	 * Get the global domain instance
	 * 
	 * @param scope	the scope
	 * @return	the instance
	 */
	public static final NSDefaults getGlobalInstance(Scope scope) {
		return getInstance((String)null, scope);
	}
	
	/**
	 * Get instance for a given domain
	 * 
	 * @param domain the domain
	 * @param scope the scope
	 * @return	the instance
	 */
	synchronized static final NSDefaults getInstance(String domain, Scope scope) {
		NSDefaults result = null;
		switch(scope) {
			case USER:result = NSUserDefaults.getInstance(domain);break;
			case SYSTEM:result = NSSystemDefaults.getInstance(domain);break;
			case USER_BYHOST:result = NSUserByHostDefaults.getInstance(domain);
		}
		if (result == null)
			throw new IllegalArgumentException("Unknown scope");
		result.refresh();
		return result;
	}
	
	/**
	 * Get instance for a given class
	 * 
	 * @param baseClass	the class
	 * @param scope	the scope
	 * @return	the instance
	 */
	static final NSDefaults getInstance(Class<?> baseClass, Scope scope) {
		return getInstance(getDomainForName(baseClass.getCanonicalName()), scope);
	}
	
	/**
	 * Get instance for a given object
	 * 
	 * @param baseObject	the object
	 * @param scope	the scope
	 * @return	the instance
	 */
	static final NSDefaults getInstance(Object baseObject, Scope scope) {
		return getInstance(baseObject.getClass(), scope);
	}
	
	/**
	 * Translate a canonicalName to a domain
	 * 
	 * @param canonicalName	the canonical name
	 * @return	the domain
	 */
	public static String getDomainForName(String canonicalName) {
		if (canonicalName.toLowerCase().startsWith("java."))
			throw new IllegalArgumentException("Can not instantiate NSDefaults for a built-in Java object.");
		for(String s : domains) {
			if (canonicalName.equalsIgnoreCase(s) || (canonicalName.toLowerCase()+".").startsWith(s.toLowerCase()))
				return s;
		}
		String[] pack = canonicalName.split("\\.", 4);
		if (pack.length < 4)
			throw new IllegalArgumentException("CanonicalName "+canonicalName+" has less than four segments.");
		System.out.println(pack[0]+"."+pack[1]+"."+pack[2]);
		domains.add(pack[0]+"."+pack[1]+"."+pack[2]);
		return pack[0]+"."+pack[1]+"."+pack[2];
	}
	
	/**
	 * "Learn" a domain
	 * 
	 * @param domain	the domain
	 */
	public static void addDomain(String domain) {
		domains.add(domain);
	}
	
	/**
	 * Get the root of this {@link NSDefaults} as {@link NSDictionary}
	 * @return	the dictionary
	 */
	public NSDictionary toDictionary() {
		return new NSDictionary(this);
	}

}
