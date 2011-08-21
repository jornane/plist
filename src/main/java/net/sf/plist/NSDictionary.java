/*
Property List Dictionary - LGPL licensed
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

import java.util.HashMap;
import java.util.Map;

public class NSDictionary extends NSObject {

	public final Map<String,NSObject> theDictionary;
	
	public NSDictionary() {
		this.theDictionary = new HashMap<String,NSObject>();
	}
	public NSDictionary(Map<String,NSObject> theMap) {
		this.theDictionary = theMap;
	}
	
	@Override
	public Map<String,NSObject> getValue() {
		return theDictionary;
	}

}
