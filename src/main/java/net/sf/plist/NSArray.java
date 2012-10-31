/*
Property List Array - LGPL 3.0 licensed
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>Ordered list of {@link NSObject}s.</p>
 * 
 * <p>In this implementation, a {@link List} is used to represent the {@link NSArray}.</p>
 * @see List
 */
public final class NSArray extends NSCollection {

	private final NSObject[] theList;
	
	/**
	 * Constructor.
	 * @param theList the contents of new object
	 */
	public NSArray(Collection<? extends NSObject> theList) {
		this(theList.toArray(new NSObject[0]));
	}
	/**
	 * Constructor.
	 * @param theList the contents of new object
	 */
	public NSArray(NSObject[] theList) {
		this.theList = theList.clone();
	}
	
	/**
	 * Get {@link NSObject} corresponding to index from this object.
	 * @param index index of object to retrieve
	 */
	public NSObject get(int index) {
		return getValue().get(index);
	}
	/**
	 * Get an unmodifiable {@link List} containing all values of this object.
	 * This {@link List} cannot be modified,
	 * but can be used to instantiate a new {@link ArrayList}
	 * and then used to create a new {@link NSArray}.
	 * Each subsequent call to {@link #toList()} will create a new {@link ArrayList}.
	 * Use {@link #getValue()} to get an unmodifiable {@link List}.
	 * @return the {@link List}
	 * @see #getValue()
	 */
	@Override
	public List<NSObject> toList() {
		return new ArrayList<NSObject>(Arrays.asList(theList));
	}
	/**
	 * <p>Get an array containing all values of this object.
	 * Changes made in the array will not affect this object.</p>
	 * 
	 * <p>When iterating through all items in this object,
	 * the {@link #toList()} method is a better choice for performance reasons.</p> 
	 * @return the array
	 */
	public NSObject[] array() {
		return theList.clone();
	}
	/** {@inheritDoc} */
	@Override
	public List<Object> toObject() {
		ArrayList<Object> result = new ArrayList<Object>();
		for(NSObject e : toList())
			result.add(e.toObject());
		return result;
	}
	/**
	 * {@inheritDoc}
	 * @see #toList()
	 */
	@Override
	public List<NSObject> getValue() {
		return Collections.unmodifiableList(
				Arrays.asList(theList)
			);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isTrue() {
		return theList.length != 0;
	}
	
	/** {@inheritDoc} */
	@Override
	public SortedMap<String, NSObject> toMap() {
		int i = 0;
		TreeMap<String, NSObject> result = new TreeMap<String, NSObject>();
		for(NSObject o : theList) {
			result.put("item "+i, o);
			i++;
		}
		return result;
	}
	
	/** {@inheritDoc} */
	@Override
	public long toLong() {
		return theList.length;
	}
	
	/** {@inheritDoc} */
	@Override
	public double toDouble() {
		return theList.length;
	}
	/**
	 * Convert a list to an NSArray.
	 * In order for this to work,
	 * the value objects must be of one of these types:
	 * - a java.lang object representing a primitive type
	 * - a NSObject
	 * - a Map or List which follows the same rules
	 * 
	 * {@link NSObject#getValue()} always returns a valid object.
	 */
	public static NSArray fromList(List<?> list) {
		ArrayList<NSObject> arrayList = new ArrayList<NSObject>();
		for(Object item : list) {
			arrayList.add(NSObject.fromObject(item));
		}
		return new NSArray(arrayList);
	}

}
