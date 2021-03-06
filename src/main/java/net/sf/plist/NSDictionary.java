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
Project page on http://plist.sf.net/
*/
package net.sf.plist;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>A key/value store,
 * with {@link String} as keys and {@link NSObject} as values.<br />
 * Usually, the root node of a Property List is a {@link NSDictionary}.</p> 
 * 
 * <p>In this implementation, a {@link SortedMap} is used
 * to represent the {@link NSDictionary}.</p>
 * @see SortedMap
 */
public final class NSDictionary extends NSCollection {

	private final SortedMap<String,NSObject> theDictionary;
	
	/**
	 * Standard constructor. Needs a {@link SortedMap}
	 * in order to keep the ordering.
	 * @param map	value of the new object
	 */
	public NSDictionary(Map<String, ? extends NSObject> map) {
		this.theDictionary = Collections.unmodifiableSortedMap(
				new TreeMap<String,NSObject>(map)
			);
	}
	
	/**
	 * Convert a map to a NSDictionary.
	 * In order for this to work,
	 * the value objects must be of one of these types:
	 * - a java.lang object representing a primitive type
	 * - a NSObject
	 * - a Map or List which follows the same rules
	 * 
	 * {@link NSObject#getValue()} always returns a valid object.
	 */
	public static NSDictionary fromMap(Map<String,?> map) {
		TreeMap<String, NSObject> dictMap = new TreeMap<String,NSObject>();
		for(Entry<String, ?> e : map.entrySet()) {
			dictMap.put(e.getKey(), NSObject.fromObject(e.getValue()));
		}
		return new NSDictionary(dictMap);
	}
	
	/**
	 * Get the {@link NSObject} associated with <code>key</code>.
	 * @param key The key to retrieve
	 * @return The {@link NSObject} associated with <code>key</code>
	 * @see java.util.Map#get(Object)
	 */
	public NSObject get(String key) {
		return toMap().get(key);
	}
	/**
	 * Get the entryset for this {@link NSObject}.
	 * @return the entryset
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<String, NSObject>> entrySet() {
		return toMap().entrySet();
	}
	
	/**
	 * {@inheritDoc}
	 * @see #toMap()
	 */
	@Override
	public SortedMap<String,NSObject> getValue() {
		return theDictionary;
	}
	/**
	 * Get an unlinked modifiable {@link SortedMap}
	 * containing all values of this object.
	 * This {@link SortedMap} can be modified and then
	 * used to create a new {@link NSDictionary}.
	 * Each subsequent call to {@link #toMap()}
	 * will create a new {@link SortedMap}.
	 * Use {@link #getValue()} to get an unmodifiable {@link SortedMap}.
	 * @return the {@link SortedMap}
	 * @see #getValue()
	 */
	@Override
	public SortedMap<String, NSObject> toMap() {
		return new TreeMap<String, NSObject>(theDictionary);
	}
	
	/** {@inheritDoc} */
	@Override
	public SortedMap<String, ?> toObject() {
		TreeMap<String, Object> result = new TreeMap<String,Object>();
		for(Entry<String, ? extends NSObject> e : toMap().entrySet())
			result.put(e.getKey(), e.getValue().toObject());
		return result;
	}

	
	/** {@inheritDoc} */
	@Override
	public List<NSObject> toList() {
		return Collections.unmodifiableList(
				Arrays.asList(theDictionary.values().toArray(new NSObject[0]))
			);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isTrue() {
		return !theDictionary.isEmpty();
	}
	
	/** {@inheritDoc} */
	@Override
	public long toLong() {
		return theDictionary.size();
	}
	
	/** {@inheritDoc} */
	@Override
	public double toDouble() {
		return theDictionary.size();
	}

}
