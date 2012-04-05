/*
Property List Utility - LGPL 3.0 licensed
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
package net.sf.plist.defaults;

import java.util.HashMap;

public class NSUserDefaults extends NSDefaults {
	private static final long serialVersionUID = 1L;
	
	/** All instances */
	protected final static HashMap<String,NSUserDefaults> INSTANCES = new HashMap<String,NSUserDefaults>();
	
	/** This defaults' scope */
	public static final Scope SCOPE = Scope.USER;
	
	/**
	 * Construct new defaults instance for a given domain
	 * @param domain	the domain
	 */
	private NSUserDefaults(String domain) {
		super(OSPATH.getPListFile(domain, SCOPE));
	}
	
	/** @see NSDefaults#getInstance(String, Scope) */
	public synchronized static final NSUserDefaults getInstance(String domain) {
		if (INSTANCES.containsValue(domain))
			return INSTANCES.get(domain);
		NSUserDefaults result = new NSUserDefaults(domain);
		INSTANCES.put(domain, result);
		return result;
	}
	/** @see NSDefaults#getInstance(Class, Scope) */
	public static NSDefaults getInstance(Class<?> baseClass) {
		return NSDefaults.getInstance(baseClass, SCOPE);
	}
	/** @see NSDefaults#getInstance(Object, Scope) */
	public static NSDefaults getInstance(Object baseObject) {
		return NSDefaults.getInstance(baseObject, SCOPE);
	}

}
