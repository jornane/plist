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

public abstract class NSObject {

	public enum Types { STRING, INTEGER, REAL, DATE, DATA, BOOLEAN, DICT, ARRAY, KEY, UUID };
	
	NSObject() {/*not directly extendable outside this package*/}
	
	public abstract Object getValue();
	
	
	@Override
	public int hashCode() {
		return getValue().hashCode();
	}
	@Override
	public boolean equals(Object o) {
		return o instanceof NSString
			&& getValue().equals(((NSObject) o).getValue());
	}
	@Override
	public String toString() {
		Object val = getValue();
		return val == null ? "null" : val.toString();
	}

}
