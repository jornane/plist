/*
Property List Dictionary - LGPL 3.0 licensed
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
Project page on https://plist.sf.net/
*/
package net.sf.plist;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>A key/value store,
 * with {@link String} as keys and {@link NSObject} as values.<br />
 * Usually, the root node of a Property List is a {@link NSDictionary}.</p> 
 * 
 * <p>In this implementation, a {@link SortedMap} is used to represent the {@link NSDictionary}.</p>
 * @see SortedMap
 */
public final class NSDictionary extends NSObject {

	private final SortedMap<String,NSObject> theDictionary;
	
	/**
	 * Constructor .
	 * @param theMap value of the new object
	 */
	public NSDictionary(SortedMap<String,NSObject> theMap) {
		this.theDictionary = Collections.unmodifiableSortedMap(theMap);
	}
	/**
	 * Get the {@link NSObject} associated with <code>key</code>.
	 * @param key The key to retrieve
	 * @return The {@link NSObject} associated with <code>key</code>
	 * @see java.util.Map#get(Object)
	 */
	public NSObject get(String key) {
		return map().get(key);
	}
	/**
	 * Get the entryset for this {@link NSObject}.
	 * @return the entryset
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<String, NSObject>> entrySet() {
		return map().entrySet();
	}
	
	/**
	 * {@inheritDoc}
	 * @see #map()
	 */
	@Override
	public SortedMap<String,NSObject> getValue() {
		return theDictionary;
	}
	/**
	 * Get an unlinked modifiable {@link SortedMap} containing all values of this object.
	 * This {@link SortedMap} can be modified and then used to create a new {@link NSDictionary}.
	 * Each subsequent call to {@link #map()} will create a new {@link SortedMap}.
	 * Use {@link #getValue()} to get an unmodifiable {@link SortedMap}.
	 * @return the {@link SortedMap}
	 * @see #getValue()
	 */
	public SortedMap<String, NSObject> map() {
		return new TreeMap<String, NSObject>(theDictionary);
	}

}
