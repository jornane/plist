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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
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
public final class NSDefaults implements SortedMap<String,NSObject> {
	
	/** Collection of domains */
	private final static HashSet<String> domains = new HashSet<String>();
	
	/** Collection of all instances, grouped by {@link Scope} and domain */
	private final static Map<Scope, HashMap<String, NSDefaults>> INSTANCES;
	
	/** Instance of {@link OperatingSystemPath} for current operating system */
	public final static OperatingSystemPath OSPATH = OperatingSystemPath.getInstance();
	
	/** The Property List file used for reading and storing the preferences in this defaults instance */
	private final File file;
	
	/** Map containing all key/value pairs */
	private final TreeMap<String,NSObject> theMap = new TreeMap<String,NSObject>();
	/** Map containing all modifications since last commit, or since initialization if not committed yet */
	private final HashMap<String,NSObject> modifications = new HashMap<String,NSObject>();
	/** Set containing all names of keys that have been removed since last commit, or since initialization if not committed yet */
	private final HashSet<String> removals = new HashSet<String>();
	/** True if {@link #clear()} has been called since last commit, or since initialization if not committed yet */
	private boolean cleared = false;
	/** {@link NSDefaults} is lazy loaded. This variable stays true until the property list file is read for the first time. This happens when a value is read or when a commit is done. It does not happen when a value is written or {@link #clear()} is called. */
	private boolean virgin;
	
	/** Initialize INSTANCES with all {@link Scope}s */
	static {
		HashMap<Scope, HashMap<String, NSDefaults>> instances = new HashMap<Scope,HashMap<String,NSDefaults>>();
		for(Scope s : Scope.values())
			instances.put(s, new HashMap<String,NSDefaults>());
		INSTANCES = Collections.unmodifiableMap(instances);
	}
	
	/**
	 * Construct using a fixed file.
	 * Invocation of this constructor is discouraged,
	 * use {@link #getInstance(Object, Scope)} instead.
	 * 
	 * @param file The PList file used, the file may be overwritten 
	 */
	public NSDefaults(File file) {
		this.file = file;
		virgin = true;
	}
	
	/**
	 * Write all changes to the Property List file
	 * @throws PropertyListException	constructing the Property List file failed
	 * @throws IOException	writing the file failed
	 */
	synchronized public void commit() throws PropertyListException, IOException {
		file.getParentFile().mkdirs();
		synchronized(theMap) { synchronized(modifications) { synchronized(removals) {
			if (virgin) refresh();
			PropertyListWriter.write(new NSDictionary(this), file);
			modifications.clear();
			removals.clear();
			cleared = false;
		}}}
	}
	
	/**
	 * Re-read all information from the Property List file
	 */
	synchronized public void refresh() {
		synchronized(theMap) { synchronized(modifications) { synchronized(removals) {
			virgin = false;
			theMap.clear();
			if (!cleared) {
				theMap.putAll(getRoot(file).toMap());
				for(String key : removals)
					theMap.remove(key);
			}
			theMap.putAll(modifications);
		}}}
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
			return new NSDictionary(PropertyListParser.parse(file).toMap());
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
	public synchronized static final NSDefaults getInstance(String domain, Scope scope) {
		HashMap<String, NSDefaults> instances = INSTANCES.get(scope);
		if (instances == null)
			throw new IllegalArgumentException("Unknown scope");
		if (instances.containsValue(domain))
			return instances.get(domain);
		NSDefaults result = new NSDefaults(OSPATH.getPListFile(domain, scope));
		instances.put(domain, result);
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
		if (canonicalName.toLowerCase().startsWith("java.") || canonicalName.toLowerCase().startsWith("javax."))
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
		synchronized(theMap) {
			if (virgin) refresh();
			return new NSDictionary(theMap);
		}
	}
	
	  //////////////////////////////////
	 // Composition of TreeMap below //
	//////////////////////////////////
	
	/** {@inheritDoc} */
	public int size() {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.size();
		}
	}
	
	/** {@inheritDoc} */
	public boolean isEmpty() {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.isEmpty();
		}
	}
	
	/** {@inheritDoc} */
	public boolean containsKey(Object key) {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.containsKey(key);
		}
	}
	
	/** {@inheritDoc} */
	public boolean containsValue(Object value) {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.containsValue(value);
		}
	}
	
	/** {@inheritDoc} */
	public NSObject get(Object key) {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.get(key);
		}
	}
	
	/** {@inheritDoc} */
	public NSObject put(String key, NSObject value) {
		synchronized(removals) {
			removals.remove(key);
			synchronized(modifications) {
				modifications.put(key, value);
				synchronized(theMap) {
					return theMap.put(key, value);
				}
			}
		}
	}
	
	/** {@inheritDoc} */
	public NSObject remove(Object key) {
		if (key instanceof String) synchronized(removals) {
			removals.add(key.toString());
			synchronized(modifications) {
				modifications.remove(key);
				synchronized(theMap) {
					return theMap.remove(key);
				}
			}
		}
		return null;
	}
	
	/** {@inheritDoc} */
	public void putAll(Map<? extends String, ? extends NSObject> m) {
		synchronized(removals) {
			removals.removeAll(m.keySet());
			synchronized(modifications) {
				modifications.putAll(m);
				synchronized(theMap) {
					theMap.putAll(m);
				}
			}
		}
	}
	
	/** {@inheritDoc} */
	public void clear() {
		synchronized(removals) {
			synchronized(theMap) {
				removals.clear();
				cleared = true;
				synchronized(modifications) {
					modifications.clear();
					theMap.clear();
				}
			}
		}
	}
	
	/** {@inheritDoc} */
	public Comparator<? super String> comparator() {
		return theMap.comparator();
	}
	
	/** {@inheritDoc} */
	public SortedMap<String, NSObject> subMap(String fromKey, String toKey) {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.subMap(fromKey, toKey);
		}
	}
	
	/** {@inheritDoc} */
	public SortedMap<String, NSObject> headMap(String toKey) {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.headMap(toKey);
		}
	}
	
	/** {@inheritDoc} */
	public SortedMap<String, NSObject> tailMap(String fromKey) {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.headMap(fromKey);
		}
	}
	
	/** {@inheritDoc} */
	public String firstKey() {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.firstKey();
		}
	}
	
	/** {@inheritDoc} */
	public String lastKey() {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.lastKey();
		}
	}
	
	/** {@inheritDoc} */
	public Set<String> keySet() {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.keySet();
		}
	}
	
	/** {@inheritDoc} */
	public Collection<NSObject> values() {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.values();
		}
	}
	
	/** {@inheritDoc} */
	public Set<java.util.Map.Entry<String, NSObject>> entrySet() {
		synchronized(theMap) {
			if (virgin) refresh();
			return theMap.entrySet();
		}
	}

}
