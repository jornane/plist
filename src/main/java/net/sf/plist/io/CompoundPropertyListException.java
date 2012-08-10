/**
 * 
 */
package net.sf.plist.io;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author yorn
 *
 */
public class CompoundPropertyListException extends PropertyListException {
	private static final long serialVersionUID = 1L;

	protected PropertyListException[] why;
	
	/**
	 * @param reason
	 */
	public CompoundPropertyListException(PropertyListException... why) {
		super(getReason(why));
		this.why = why;
	}

	private static String getReason(PropertyListException... why) {
		String result = "\n";
		for(int i=0;i<why.length;i++)
			result += "\t"+why[i]+"\n";
		return result;
	}
	
	public List<? extends PropertyListException> getAllReasons() {
		return Collections.unmodifiableList(Arrays.asList(why));
	}

}
