/*
Property List Collection - LGPL 3.0 licensed
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

/**
 * Parent class to {@link NSArray} and {@link NSDictionary}
 * to make these classes, which contain {@link NSObject}s themselves,
 * easier identifiable. This class does not provide any functionality.
 */
public abstract class NSCollection extends NSObject {
	
	/**
	 * Collections can not be converted to a byte array
	 * in a way that makes sense.
	 */
	@Override
	public byte[] toBytes() {
		return new byte[0];
	}

}
