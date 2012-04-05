/*
Property List String - LGPL 3.0 licensed
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
 * <p>Represents a string.</p>
 * @see String
 */
public final class NSString extends NSObject {

	private final String theString;
	
	/**
	 * Constructor.
	 * @param theString value of the new object
	 */
	public NSString(String theString) {
		this.theString = theString;
	}
	
	/**
	 * {@inheritDoc}
	 * @see #toString()
	 */
	@Override
	public String getValue() {
		return theString;
	}

}
