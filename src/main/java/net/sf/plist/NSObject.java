/*
Property List Object - LGPL licensed
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

/**
 * Every PList contains one or more <code>NSObject</code>s.
 * Every different extension of the <code>NSObject</code> holds a certain kind of value,
 * for example the <code>NSString</code> holds a <code>String</code> and the <code>NSDate</code> holds a <code>Date</code>.
 * Some <code>NSObject</code>s can hold other <code>NSObject</code>s, thus making a tree.
 * Those are <code>NSDictionary</code> and <code>NSArray</code>.
 * 
 * The value of this <code>NSObject</code> can be requested through the getValue method.
 * 
 * The <code>hashCode</code>, <code>equals</code> and <code>toString</code> methods are overridden to use the respective functions of the value object. 
 */
public abstract class NSObject {

	public static enum Type { STRING, INTEGER, REAL, DATE, DATA, BOOLEAN, DICT, ARRAY, KEY, UUID };
	
	NSObject() {/*not directly extendable outside this package*/}
	
	/**
	 * Get the unmodifiable value of this <code>NSObject</code> 
	 * @return the value of this <code>NSObject</code>
	 */
	public abstract Object getValue();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		NSObject other = (NSObject) obj;
		if (this.getValue() == null) {
			if (other.getValue() != null)
				return false;
		} else if (!this.getValue().equals(other.getValue()))
			return false;
		return true;
	}
	@Override
	/**
	 * Get the String representation of this <code>NSObject</code>s value.
	 * @returns the <code>String</code> representation for this <code>NSObject</code>.
	 * @see java.lang.Object.toString
	 */
	public String toString() {
		Object val = getValue();
		return val == null ? "null" : val.toString();
	}

}
