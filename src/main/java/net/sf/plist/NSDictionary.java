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
