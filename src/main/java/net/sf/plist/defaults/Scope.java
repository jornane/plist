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

/**
 * The scope indicates who has access to a specific {@link NSDefaults} instance.
 * This can be either {@link #USER} or {@link #SYSTEM} where the first
 * is only used for the current user and the latter is system wide.
 * A special {@link Scope} is {@link #USER_BYHOST} which is specific for
 * the current user but only on the host the program is currently running on.
 */
public enum Scope {
	
	SYSTEM(false),
	USER(false),
	USER_BYHOST(true),
	;
	
	/** @see Scope#isByHost() */
	private final boolean isByHost;

	/**
	 * Instantiate a {@link Scope}
	 * @param isByHost determines if this scope is by host.
	 * @see Scope#isByHost()
	 */
	private Scope(boolean isByHost) {
		this.isByHost = isByHost;
	}
	
	/**
	 * Determine if this scope is by host.
	 * If it is, a machine UUID needs to be appended to the filename
	 */
	public boolean isByHost() {
		return isByHost;
	}

}
