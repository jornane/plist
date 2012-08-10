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
package net.sf.plist.io;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * If multiple operations with a Property List failed,
 * all exceptions that occurred can be packed inside this one exception.
 */
public class CompoundPropertyListException extends PropertyListException {
	private static final long serialVersionUID = 1L;

	/** The exceptions that occurred */
	protected PropertyListException[] why;
	
	/**
	 * Construct a new CompoundPropertyListException
	 * @param why	All exceptions that occurred when trying to complete an operation
	 */
	public CompoundPropertyListException(PropertyListException... why) {
		super(getReason(why));
		this.why = why;
	}
	
	/**
	 * Construct a reason string from all given exceptions
	 * @param why	All exceptions that occurred when trying to complete an operation
	 * @return	A human-readable reason
	 */
	private static String getReason(PropertyListException... why) {
		String result = "\n";
		for(int i=0;i<why.length;i++)
			result += "\t"+why[i]+"\n";
		return result;
	}
	
	/**
	 * Return a list of all reasons this exception was instantiated
	 * @return	the reasons
	 */
	public List<? extends PropertyListException> getAllReasons() {
		return Collections.unmodifiableList(Arrays.asList(why));
	}

}
