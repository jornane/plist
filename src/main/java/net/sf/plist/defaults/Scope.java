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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The scope indicates who has access to a specific {@link NSDefaults} instance.
 * This can be either {@link #USER} or {@link #SYSTEM} where the first
 * is only used for the current user and the latter is system wide.
 * A special {@link Scope} is {@link #USER_BYHOST} which is specific for
 * the current user but only on the host the program is currently running on.
 */
abstract public class Scope {
	
	private static final Set<Scope> INSTANCES = Collections.synchronizedSet(new HashSet<Scope>());
	private static final Map<String,UserByHostScope> HOST_INSTANCES = new HashMap<String,UserByHostScope>();
	
	public static final SystemScope SYSTEM = new SystemScope();
	public static final UserScope USER = new UserScope();
	public static final UserByHostScope USER_BY_THIS_HOST = getUserByHostScope();
	
	public static SystemScope getSystemScope() {
		return SYSTEM;
	}
	public static UserScope getUserScope() {
		return USER;
	}
	public static UserByHostScope getUserByHostScope() {
		return getUserByHostScope(OperatingSystemPath.getInstance().buildMachineUUID());
	}
	public static UserByHostScope getUserByHostScope(String machineUUID) {
		if (HOST_INSTANCES.containsKey(machineUUID))
			return HOST_INSTANCES.get(machineUUID);
		synchronized(HOST_INSTANCES) {
			if (!HOST_INSTANCES.containsKey(machineUUID))
				HOST_INSTANCES.put(machineUUID, new UserByHostScope(machineUUID));
		}
		return getUserByHostScope(machineUUID);
	}
	
	Scope() {
		INSTANCES.add(this);
	}
		
	/**
	 * Determine if this scope is by host.
	 * If it is, a machine UUID needs to be appended to the filename
	 */
	abstract public boolean isByHost();
	
	public static Scope[] instances() {
		return INSTANCES.toArray(new Scope[0]);
	}
	
	public static final class SystemScope extends Scope {

		SystemScope() {}

		/** {@inheritDoc} */
		@Override
		public boolean isByHost() {
			return false;
		}

	}

	public static final class UserByHostScope extends Scope {

		public final String machineUUID;

		UserByHostScope(String machineUUID) {
			this.machineUUID = machineUUID;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isByHost() {
			return true;
		}

	}
	
	public static final class UserScope extends Scope {

		UserScope() {}

		/** {@inheritDoc} */
		@Override
		public boolean isByHost() {
			return false;
		}

	}

}
