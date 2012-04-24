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

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * Helper class for {@link NSDefaults} to provide defaults on multiple operating systems.
 */
abstract class OperatingSystemPath {
	
	/**
	 * Class for generic (probably *NIX) operating system
	 */
	static class DefaultSystemPath extends OperatingSystemPath {
		/** {@inheritDoc} */
		@Override
		public File getPListPath(final Scope scope) {
			switch(scope) {
				case USER:
					return new File(System.getProperty("user.home")+"/.preferences/");
				case USER_BYHOST:
					return new File(System.getProperty("user.home")+"/.preferences/ByHost/");
				case SYSTEM:
					return new File("/etc/preferences/");
			}
			throw new NullPointerException();
		}
		
		/** {@inheritDoc} */
		@Override
		public boolean isLowerCasePreferred() {
			return true;
		}
	}
	/**
	 * Class for the Mac operating system
	 */
	static class OSXSystemPath extends OperatingSystemPath {
		/** {@inheritDoc} */
		@Override
		public File getPListPath(final Scope scope) {
			switch(scope) {
				case USER:
					return new File(System.getProperty("user.home")+"/Library/Preferences/");
				case USER_BYHOST:
					return new File(System.getProperty("user.home")+"/Library/Preferences/ByHost/");
				case SYSTEM:
					return new File("/Library/Preferences/");
			}
			throw new NullPointerException();
		}
		
		/** {@inheritDoc} */
		@Override
		public String buildMachineUUID() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						Runtime.getRuntime().exec("system_profiler SPHardwareDataType")
						.getInputStream()));
				String line = reader.readLine();
				while(line != null) {
					if (line.trim().startsWith("Hardware UUID: "))
						return line.trim().substring("Hardware UUID: ".length());
					line = reader.readLine();
				}
				return super.buildMachineUUID();
			} catch (Exception e) {
				return super.buildMachineUUID();
			}
		}

		/** {@inheritDoc} */
		@Override
		public boolean isLowerCasePreferred() {
			return false;
		}
	}
	/**
	 * Class for the Linux operating system
	 */
	static class LinuxSystemPath extends DefaultSystemPath {
		/** {@inheritDoc} */
		@Override
		public String buildMachineUUID() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						Runtime.getRuntime().exec("hal-get-property --udi /org/freedesktop/Hal/devices/computer --key system.hardware.uuid")
						.getInputStream()));
				return reader.readLine();
			} catch (Exception e) {
				return super.buildMachineUUID();
			}
		}
	}
	
	/**
	 * Class for the Windows operating system
	 */
	static class WindowsSystemPath extends OperatingSystemPath {
		/** {@inheritDoc} */
		@Override
		public File getPListPath(Scope scope) {
			switch(scope) {
			case USER:
				return new File(System.getProperty("user.home")+"\\AppData\\Roaming\\Library\\Preferences\\");
			case USER_BYHOST:
				return new File(System.getProperty("user.home")+"\\AppData\\Roaming\\Library\\Preferences\\ByHost\\");
			case SYSTEM:
				return new File("C:\\Users\\Public\\AppData\\Local\\Library\\Preferences\\");
			}
			throw new NullPointerException();
		}
		
		/** {@inheritDoc} */
		@Override
		public boolean isLowerCasePreferred() {
			return false;
		}
	}
	
	// UUID Linux: hal-get-property --udi /org/freedesktop/Hal/devices/computer --key system.hardware.uuid
	// UUID OS X: system_profiler SPHardwareDataType | awk '/UUID/{sub(/^[ \t]+/, "")}; NR == 17 {print}'
	// UUID Windows: reg query "HKLM\SYSTEM\ControlSet001\Control\IDConfigDB\Hardware Profiles\0001" /v HwProfileGuid
	
	/**
	 * Get the instance for the current operating system
	 */
	public static OperatingSystemPath getInstance() {
		if (System.getProperty("os.name").toLowerCase().contains("mac"))
			return new OSXSystemPath();
		if (System.getProperty("os.name").toLowerCase().contains("linux"))
			return new LinuxSystemPath();
		if (System.getProperty("os.name").toLowerCase().contains("windows"))
			return new WindowsSystemPath();
		return new DefaultSystemPath();
	}
	
	/** The cached uuid value */
	private String uuid;

	/**
	 * Get the Property List file for a given domain and scope
	 * 
	 * @param domain	the domain
	 * @param scope	the scope
	 * @return	the property list file
	 */
	public final File getPListFile(String domain, final Scope scope) {
		if (domain == null)
			domain = isLowerCasePreferred() ? ".globalpreferences" : ".GlobalPreferences";
		if (scope.isByHost())
			if (isLowerCasePreferred())
				return new File((getPListPath(scope)+File.separator+domain+"."+buildMachineUUID()+".plist").toLowerCase());
			else
				return new File(getPListPath(scope)+File.separator+domain+"."+buildMachineUUID()+".plist");
		return new File(getPListPath(scope)+File.separator+domain+".plist");
	}
	
	/**
	 * Determines if the operating system prefers lowercase domains.
	 * This is generally true for operating systems that run on
	 * filesystems which are case sensitive,
	 * usually being everything other than Windows and Mac OS X.
	 * @return	whether lower case is preferred
	 */
	public abstract boolean isLowerCasePreferred();

	/**
	 * Get the path to the directory where defaults are stored for a given scope
	 * 
	 * @param scope	the scope
	 * @return	the directory
	 */
	public abstract File getPListPath(final Scope scope);

	/**
	 * Get the UUID of the machine running the program
	 * 
	 * @return	the UUID
	 */
	public final String getMachineUUID() {
		if (uuid == null) synchronized(this) {
			if (uuid == null)
				uuid = buildMachineUUID();
		}
		return uuid;
	}
	/**
	 * Overrideable method for extending classes to determine the machine UUID
	 * of the local machine. This value can be requested publicly using {@link #getMachineUUID()}
	 * @return	the UUID
	 */
	protected String buildMachineUUID() {
		StringBuilder result = new StringBuilder();
		try {
			// Basic UUID determination using MAC address,
			// when determining using more modern methods fail
			// or are not available
			for(byte b : NetworkInterface.getNetworkInterfaces().nextElement().getHardwareAddress()) {
				result.append(Integer.toString(b&0xFF, 0x10));
			}
			return result.toString();
		} catch (SocketException e) {
			// When all else fails
			return System.getProperty("user.name")
				+ System.getProperty("user.region")
				+ System.getProperty("user.language")
				+ "-"
				+ System.getProperty("os.name")
				+ System.getProperty("os.version")
				+ System.getProperty("os.arch");
		}
	}
	
	/**
	 * Generate a {@link FilenameFilter} to be used to find
	 * Property List files representing a domain.
	 * @param scope	The scope of the Property List files intended to be found, should correspond with the path being searched.
	 * @return	The {@link FilenameFilter}
	 */
	FilenameFilter getFilter(final Scope scope) {
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (scope.isByHost())
					return name != null && name.length() > 7+getMachineUUID().length() && name.substring(name.length()-7-getMachineUUID().length()).equalsIgnoreCase("."+getMachineUUID()+".plist");
				else
					return name != null && name.length() > 6 && name.substring(name.length()-6).equalsIgnoreCase(".plist");
			}};
	}

}
