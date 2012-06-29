/*
Property List Parsing Exception - LGPL 3.0 licensed
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
package net.sf.plist.io;

/**
 * Exception which indicates that something went wrong while parsing a property list
 */
public class PropertyListException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct a new exception stating a reason and a cause.
	 * @see Exception#Exception(String, Throwable)
	 */
	public PropertyListException(String reason, Throwable cause) {
		super(reason, cause);
	}
	
	/**
	 * Construct a new exception stating a reason but no cause.
	 * @see Exception#Exception(String)
	 */
	public PropertyListException(String reason) {
		super(reason);
	}

}
