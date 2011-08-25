/*
Property List Dictionary - LGPL licensed
Copyright (C) 2011  Yørn de Jong

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

This file was obtained from http://plist.sf.net/
*/
package net.sf.plist;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The <code>NSDictionary</code> is a key/value store,
 * with <code>String</code>s as keys and <code>NSObject</code>s as values.
 * Usually, the root node of a PList is a <code>NSDictionary</code>. 
 * 
 * In this implementation, a <code>Map</code> is used to represent the <code>NSDictionary</code>.
 * @see java.util.Map
 */
public class NSDictionary extends NSObject {

	private final Map<String,NSObject> theDictionary;
	
	public NSDictionary() {
		this.theDictionary = new HashMap<String,NSObject>();
	}
	public NSDictionary(Map<String,NSObject> theMap) {
		this.theDictionary = theMap;
	}
	
	public synchronized void put(String key, NSObject value) {
		getValue().put(key, value);
	}
	public NSObject get(String key) {
		return getValue().get(key);
	}
	public Set<Entry<String, NSObject>> entrySet() {
		return Collections.unmodifiableSet(getValue().entrySet());
	}
	
	@Override
	public Map<String,NSObject> getValue() {
		return Collections.unmodifiableMap(theDictionary);
	}

}
