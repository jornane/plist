/*
Property List Array - LGPL licensed
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>NSArray</code> is an ordered list of <code>NSObject</code>s,
 * 
 * In this implementation, a <code>List</code> is used to represent the <code>NSArray</code>.
 * @see java.util.List
 */
public class NSArray extends NSObject {

	private final List<NSObject> theList;
	
	public NSArray() {
		this.theList = new ArrayList<NSObject>();
	}
	public NSArray(List<NSObject> theList) {
		this.theList = theList;
	}
	
	/**
	 * Add an <code>NSObject</code> to this <code>NSArray</code>
	 * @param value the value to add
	 */
	public void add(NSObject value) {
		getValue().add(value);
	}
	/**
	 * Remove an <code>NSObject</code> from this <code>NSArray</code>
	 * @param value the value to remove
	 */
	public void remove(NSObject value) {
		getValue().remove(value);
	}
	/**
	 * Remove an index from this <code>NSArray</code>
	 * @param index the index to remove
	 */
	public void remove(int index) {
		getValue().remove(index);
	}
	/**
	 * Get an unmodifiable <code>List</code> containing all values of this <code>NSArray</code>.
	 * @return the <code>List</code>
	 */
	public List<NSObject> list() {
		return Collections.unmodifiableList(getValue());
	}
	/**
	 * Get an unmodifiable <code>List</code> containing all values of this <code>NSArray</code>.
	 * @return the <code>List</code>
	 */
	@Override
	public List<NSObject> getValue() {
		return Collections.unmodifiableList(theList);
	}

}
