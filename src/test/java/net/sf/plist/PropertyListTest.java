package net.sf.plist;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.text.Collator;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeMap;

import org.junit.Test;

public final class PropertyListTest {

	private static final GregorianCalendar CAL;
	private static final byte[] DATA;
	private static TreeMap<String, NSObject> TESTMAP;
	
	static {
		DATA = new byte[255];
		for(byte i=1;i!=0;i++)
			DATA[i<0?i+128:i] = i;
		
		TESTMAP = new TreeMap<String,NSObject>(Collator.getInstance());
		CAL = new GregorianCalendar(0, 0, 0, 0, 0, 0);
		CAL.setTimeZone(TimeZone.getTimeZone("GMT"));
		CAL.set(1987, 6, 31, 6, 0, 0);
		
		TESTMAP.put("Boolean", new NSBoolean(true));
		TESTMAP.put("Date", new NSDate(CAL.getTime()));
		TESTMAP.put("Float", new NSReal(42.28));
		TESTMAP.put("Integer", new NSInteger(42));
		TESTMAP.put("Min", new NSInteger(-1));
		TESTMAP.put("Story", new NSString("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam turpis purus, tempor et luctus vel, mattis eget elit. Nullam sit amet mattis eros. Aliquam felis est, feugiat sed tincidunt tincidunt, malesuada viverra velit. In ut lorem id arcu cras amet."));
		TESTMAP.put("String", new NSString("String"));
		TESTMAP.put("Data", new NSData(DATA));
		TESTMAP.put("UTF", new NSString("⌘⇧⇪⌥"));
		TESTMAP.put("UID", new NSUID(28));
		
		NSDictionary dict = new NSDictionary(TESTMAP);
		NSArray array = new NSArray(TESTMAP.values().toArray(new NSObject[0]));
		
		TESTMAP.put("Dictionary", dict);
		TESTMAP.put("Array", array);
	}
		
	@Test
	public void byteTest() {
		assertEquals(new Long(127),
				NSNumber.createInstance(new Byte((byte) 127)).getValue()
			);
	}
	@Test
	public void shortTest() {
		assertEquals(new Long(32767), 
				NSNumber.createInstance(new Short((short) 32767)).getValue()
			);
	}
	@Test
	public void integerTest() {
		assertEquals(new Long(2147483647), 
				NSNumber.createInstance(new Integer(2147483647)).getValue()
			);
	}
	@Test
	public void longTest() {
		assertEquals(new Long(9223372036854775807L), 
				NSNumber.createInstance(new Long(9223372036854775807L)).getValue()
			);
	}
	@Test
	public void bigIntegerTest() {
		assertEquals(new BigInteger("999999999999999999999999").doubleValue(), 
				NSNumber.createInstance(new BigInteger("999999999999999999999999")).getValue().doubleValue(),
				1
			);
	}
	
	@Test
	public void conversionTest() {
		assertEquals(new NSDictionary(TESTMAP), NSObject.fromObject(new NSDictionary(TESTMAP)));
	}
	
	@Test
	@SuppressWarnings("boxing")
	public void fromObjectTest() {
		@SuppressWarnings("unchecked")
		TreeMap<String,NSObject> expected = (TreeMap<String, NSObject>) TESTMAP.clone();
		expected.remove("UID");
		expected.remove("Dictionary");
		expected.remove("Array");
		TreeMap<String,Object> actual = new TreeMap<String, Object>();
		actual.put("Boolean", true);
		actual.put("Date", CAL.getTime());
		actual.put("Float", 42.28);
		actual.put("Integer", 42);
		actual.put("Min", -1);
		actual.put("Story", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam turpis purus, tempor et luctus vel, mattis eget elit. Nullam sit amet mattis eros. Aliquam felis est, feugiat sed tincidunt tincidunt, malesuada viverra velit. In ut lorem id arcu cras amet.");
		actual.put("String", "String");
		actual.put("Data", DATA);
		actual.put("UTF", "⌘⇧⇪⌥");
		
		assertEquals(new NSDictionary(expected), NSObject.fromObject(actual));
	}
	
	@Test
	@SuppressWarnings("boxing")
	public void toObjectTest() {
		@SuppressWarnings("unchecked")
		TreeMap<String,NSObject> actual = (TreeMap<String, NSObject>) TESTMAP.clone();
		actual.remove("UID");
		actual.remove("Dictionary");
		actual.remove("Array");
		actual.remove("Data"); // primitive arrays have #equals() implemented with ==
		TreeMap<String,Object> expected = new TreeMap<String, Object>();
		expected.put("Boolean", true);
		expected.put("Date", CAL.getTime());
		expected.put("Float", 42.28);
		expected.put("Integer", 42L);
		expected.put("Min", -1L);
		expected.put("Story", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam turpis purus, tempor et luctus vel, mattis eget elit. Nullam sit amet mattis eros. Aliquam felis est, feugiat sed tincidunt tincidunt, malesuada viverra velit. In ut lorem id arcu cras amet.");
		expected.put("String", "String");
		expected.put("UTF", "⌘⇧⇪⌥");
		
		assertEquals(expected, new NSDictionary(actual).toObject());
	}

}
