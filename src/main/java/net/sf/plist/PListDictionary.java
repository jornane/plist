package net.sf.plist;

import java.util.HashMap;
import java.util.Map;

public class PListDictionary extends PListObject {

	public final Map<String,PListObject> theDictionary;
	
	public PListDictionary() {
		this.theDictionary = new HashMap<String,PListObject>();
	}
	public PListDictionary(Map<String,PListObject> theMap) {
		this.theDictionary = theMap;
	}
	
	@Override
	public Map<String,PListObject> getValue() {
		return theDictionary;
	}

}
