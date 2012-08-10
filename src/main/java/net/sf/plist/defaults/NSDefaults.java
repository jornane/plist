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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.defaults.Scope.UserByHostScope;
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
	/** Whether or not the domains available on the system are already read */
	private static boolean domainsInitialized = false;
	
	/** Collection of all instances, grouped by {@link Scope} and domain */
	private final static Map<Scope, HashMap<String, NSDefaults>> INSTANCES = new HashMap<Scope,HashMap<String,NSDefaults>>();
	
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
	/** 
	 * True if {@link #clear()} has been called since last commit, or since initialization if not committed yet.
	 * This indicates that a refresh should NOT read the original Property List file. 
	 */
	private boolean cleared = false;
	/**
	 * {@link NSDefaults} is lazy loaded.
	 * This variable stays true until the property list file is read for the first time.
	 * This happens when a value is read or when a commit is done.
	 * It does not happen when a value is written or {@link #clear()} is called.
	 */
	private boolean loaded;
	
	/** The scope of this object */
	private Scope scope;
	
	/**
	 * Lock object to keep {@link #theMap}, {@link #modifications} and {@link #removals} in sync.
	 * This object is used instead of the instance itself, because that instance might be used for locking by other code.
	 */
	private Object LOCK = new Object();
	
	/**
	 * Construct using a fixed file.
	 * Invocation of this constructor is discouraged,
	 * use {@link #getInstance(Object, Scope)} instead.
	 * 
	 * @param file The PList file used, the file may be overwritten 
	 */
	public NSDefaults(File file) {
		this.file = file;
		loaded = false;
	}
	
	public static synchronized void initDomains() {
		for(Scope s : Scope.instances())
			for(String domain : OSPATH.getPListPath(s).list(OSPATH.getFilter(null))) {
				String[] elements = domain.split("\\.");
				String extension = elements[elements.length-1];
				if (s instanceof UserByHostScope) {
					String machineUUID = "."+elements[elements.length-2];
					domain = domain.substring(0, domain.length()-extension.length()-machineUUID.length()-1);
					Scope.getUserByHostScope(machineUUID);
				} else {
					domain = domain.substring(0, domain.length()-extension.length()-1);
				}
				domains.add(domain);
			}
		domainsInitialized = true;
	}
	
	/**
	 * Write all changes to the Property List file
	 * @throws PropertyListException	constructing the Property List file failed
	 * @throws IOException	writing the file failed
	 */
	synchronized public void commit() throws PropertyListException, IOException {
		file.getParentFile().mkdirs();
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			PropertyListWriter.write(new NSDictionary(this), file);
			modifications.clear();
			removals.clear();
			cleared = false;
		}
	}
	
	/**
	 * Re-read all information from the Property List file
	 */
	synchronized public void refresh() {
		synchronized(LOCK) {
			loaded = true;
			theMap.clear();
			if (!cleared) {
				theMap.putAll(getRoot(file).toMap());
				for(String key : removals)
					theMap.remove(key);
			}
			theMap.putAll(modifications);
		}
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
	 * @param domain the domain, null for the global preferences
	 * @param scope the scope
	 * @return	the instance
	 */
	public static final NSDefaults getInstance(String domain, Scope scope) {
		HashMap<String, NSDefaults> instances = INSTANCES.get(scope);
		if (instances == null)
			synchronized(INSTANCES) {
				if (instances == null)
					INSTANCES.put(scope, instances = new HashMap<String, NSDefaults>());
			}
		else if (instances.containsValue(domain))
			return instances.get(domain);
		synchronized(instances) {
			if (instances.containsValue(domain))
				return instances.get(domain);
			NSDefaults result = new NSDefaults(OSPATH.getPListFile(domain, scope));
			result.scope = scope;
			instances.put(domain, result);
			return result;
		}
	}
	
	/**
	 * Get instance for a given class
	 * 
	 * @param baseClass	the class
	 * @param scope	the scope
	 * @return	the instance
	 */
	public static final NSDefaults getInstance(Class<?> baseClass, Scope scope) {
		return getInstance(getDomainForName(baseClass.getCanonicalName()), scope);
	}
	
	/**
	 * Get instance for a given object
	 * 
	 * @param baseObject	the object
	 * @param scope	the scope
	 * @return	the instance
	 */
	public static final NSDefaults getInstance(Object baseObject, Scope scope) {
		return getInstance(baseObject.getClass(), scope);
	}
	
	/**
	 * Translate a canonicalName to a domain
	 * 
	 * @param canonicalName	the canonical name
	 * @return	the domain
	 */
	public static String getDomainForName(String canonicalName) {
		if (!domainsInitialized) synchronized(domains) {
			if (!domainsInitialized)
				initDomains();
		}
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
	 * Return an array of all domains known to {@link NSDefaults}
	 * This includes both domains added through {@link #addDomain(String)}
	 * as well as domains already in used on the local computer
	 * @return	array containing all known domains
	 */
	public static String[] getDomains() {
		if (domainsInitialized) synchronized(domains) {
			if (domainsInitialized)
				initDomains();
		}
		return domains.toArray(new String[0]);
	}
	
	/**
	 * Get the root of this {@link NSDefaults} as {@link NSDictionary}
	 * @return	the dictionary
	 */
	public NSDictionary toDictionary() {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return new NSDictionary(theMap);
		}
	}
	
	/**
	 * Get the scope for this instance.
	 * The scope is null if this object was created using the public constructor.
	 * @return	the scope
	 */
	public Scope getScope() {
		return scope;
	}
	
	  //////////////////////////////////
	 // Composition of TreeMap below //
	//////////////////////////////////
	
	/** {@inheritDoc} */
	public int size() {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.size();
		}
	}
	
	/** {@inheritDoc} */
	public boolean isEmpty() {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.isEmpty();
		}
	}
	
	/** {@inheritDoc} */
	public boolean containsKey(Object key) {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.containsKey(key);
		}
	}
	
	/** {@inheritDoc} */
	public boolean containsValue(Object value) {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.containsValue(value);
		}
	}
	
	/** {@inheritDoc} */
	public NSObject get(Object key) {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.get(key);
		}
	}
	
	/** {@inheritDoc} */
	public NSObject put(String key, NSObject value) {
		synchronized(LOCK) {
			removals.remove(key);
			modifications.put(key, value);
			return theMap.put(key, value);
		}
	}
	
	/** {@inheritDoc} */
	public NSObject remove(Object key) {
		if (key instanceof String) synchronized(LOCK) {
			removals.add(key.toString());
			modifications.remove(key);
			return theMap.remove(key);
		}
		return null;
	}
	
	/** {@inheritDoc} */
	public void putAll(Map<? extends String, ? extends NSObject> m) {
		synchronized(LOCK) {
			removals.removeAll(m.keySet());
			modifications.putAll(m);
			theMap.putAll(m);
		}
	}
	
	/** {@inheritDoc} */
	public void clear() {
		synchronized(LOCK) {
			removals.clear();
			cleared = true;
			modifications.clear();
			theMap.clear();
		}
	}
	
	/** {@inheritDoc} */
	public Comparator<? super String> comparator() {
		return theMap.comparator();
	}
	
	/** {@inheritDoc} */
	public SortedMap<String, NSObject> subMap(String fromKey, String toKey) {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.subMap(fromKey, toKey);
		}
	}
	
	/** {@inheritDoc} */
	public SortedMap<String, NSObject> headMap(String toKey) {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.headMap(toKey);
		}
	}
	
	/** {@inheritDoc} */
	public SortedMap<String, NSObject> tailMap(String fromKey) {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.headMap(fromKey);
		}
	}
	
	/** {@inheritDoc} */
	public String firstKey() {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.firstKey();
		}
	}
	
	/** {@inheritDoc} */
	public String lastKey() {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.lastKey();
		}
	}
	
	/** {@inheritDoc} */
	public Set<String> keySet() {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.keySet();
		}
	}
	
	/** {@inheritDoc} */
	public Collection<NSObject> values() {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.values();
		}
	}
	
	/** {@inheritDoc} */
	public Set<Entry<String, NSObject>> entrySet() {
		synchronized(LOCK) {
			if (!loaded)
				refresh();
			return theMap.entrySet();
		}
	}

}
