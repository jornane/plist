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
Project page on https://plist.sf.net/
*/
package net.sf.plist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Ordered list of {@link NSObject}s.</p>
 * 
 * <p>In this implementation, a {@link List} is used to represent the {@link NSArray}.</p>
 * @see List
 */
public final class NSArray extends NSObject {

	private final List<NSObject> theList;
	
	/**
	 * Constructor.
	 * @param theList the contents of new object
	 */
	public NSArray(List<NSObject> theList) {
		this.theList = Collections.unmodifiableList(theList);
	}
	
	/**
	 * Get {@link NSObject} corresponding to index from this object.
	 * @param index index of object to retrieve
	 */
	public NSObject get(int index) {
		return getValue().get(index);
	}
	/**
	 * Get an unlinked modifiable {@link List} containing all values of this object.
	 * This {@link List} can be modified and then used to create a new {@link NSArray}.
	 * Each subsequent call to {@link #list()} will create a new {@link ArrayList}.
	 * Use {@link #getValue()} to get an unmodifiable {@link List}.
	 * @return the {@link List}
	 * @see #getValue()
	 */
	public List<NSObject> list() {
		return new ArrayList<NSObject>(theList);
	}
	/**
	 * <p>Get an array containing all values of this object.
	 * Changes made in the array will not affect this object.</p>
	 * 
	 * <p>When iterating through all items in this object,
	 * the {@link #list()} method is a better choice for performance reasons.</p> 
	 * @return the array
	 */
	public NSObject[] array() {
		return theList.toArray(new NSObject[0]);
	}
	/**
	 * {@inheritDoc}
	 * @see #list()
	 */
	@Override
	public List<NSObject> getValue() {
		return theList;
	}

}
