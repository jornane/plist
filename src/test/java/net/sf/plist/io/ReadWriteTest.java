package net.sf.plist.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeMap;

import net.sf.plist.*;
import net.sf.plist.io.bin.BinaryParser;
import net.sf.plist.io.domxml.DOMXMLParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public final class ReadWriteTest {

	private static final NSDictionary DICT;
	private static final ByteArrayInputStream BINARYFILE, XMLFILE;
	private static final GregorianCalendar CAL;
	private static final byte[] DATA;
	private static TreeMap<String, NSObject> TESTMAP;
	
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File emptyFile;
	
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public NullPointerException nullE = new NullPointerException();
    public CompoundPropertyListException compoundE = new CompoundPropertyListException(
    		new PropertyListException[]{
    			new PropertyListException("File is not a binary property list."),	
    			new PropertyListException("The property list is not a valid XML document."),	
    		}
    	);
    
	static {
		BINARYFILE = new ByteArrayInputStream(new byte[]{
				0x62, 0x70, 0x6C, 0x69, 0x73, 0x74, 0x30, 0x30,
				(byte) 0xDB, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
				0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x15,
				0x0F, 0x10, 0x11, 0x12, 0x09, 0x13, 0x14, 0x55,
				0x41, 0x72, 0x72, 0x61, 0x79, 0x57, 0x42, 0x6F,
				0x6F, 0x6C, 0x65, 0x61, 0x6E, 0x54, 0x44, 0x61,
				0x74, 0x65, 0x5A, 0x44, 0x69, 0x63, 0x74, 0x69,
				0x6F, 0x6E, 0x61, 0x72, 0x79, 0x55, 0x46, 0x6C,
				0x6F, 0x61, 0x74, 0x57, 0x49, 0x6E, 0x74, 0x65,
				0x67, 0x65, 0x72, 0x53, 0x4D, 0x69, 0x6E, 0x55,
				0x53, 0x74, 0x6F, 0x72, 0x79, 0x56, 0x53, 0x74,
				0x72, 0x69, 0x6E, 0x67, 0x53, 0x55, 0x49, 0x44,
				0x53, 0x55, 0x54, 0x46, (byte) 0xA9, 0x0D, 0x0E, 0x0F,
				0x10, 0x11, 0x12, 0x09, 0x13, 0x14, 0x09, 0x33,
				(byte) 0xC1, (byte) 0xB9, 0x3F, (byte) 0x96, 0x20, 0x00, 0x00, 0x00,
				0x23, 0x40, 0x45, 0x23, (byte) 0xD7, 0x0A, 0x3D, 0x70,
				(byte) 0xA4, 0x10, 0x2A, 0x13, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x5F, 0x10, (byte) 0xFF, 0x4C,
				0x6F, 0x72, 0x65, 0x6D, 0x20, 0x69, 0x70, 0x73,
				0x75, 0x6D, 0x20, 0x64, 0x6F, 0x6C, 0x6F, 0x72,
				0x20, 0x73, 0x69, 0x74, 0x20, 0x61, 0x6D, 0x65,
				0x74, 0x2C, 0x20, 0x63, 0x6F, 0x6E, 0x73, 0x65,
				0x63, 0x74, 0x65, 0x74, 0x75, 0x72, 0x20, 0x61,
				0x64, 0x69, 0x70, 0x69, 0x73, 0x63, 0x69, 0x6E,
				0x67, 0x20, 0x65, 0x6C, 0x69, 0x74, 0x2E, 0x20,
				0x41, 0x6C, 0x69, 0x71, 0x75, 0x61, 0x6D, 0x20,
				0x74, 0x75, 0x72, 0x70, 0x69, 0x73, 0x20, 0x70,
				0x75, 0x72, 0x75, 0x73, 0x2C, 0x20, 0x74, 0x65,
				0x6D, 0x70, 0x6F, 0x72, 0x20, 0x65, 0x74, 0x20,
				0x6C, 0x75, 0x63, 0x74, 0x75, 0x73, 0x20, 0x76,
				0x65, 0x6C, 0x2C, 0x20, 0x6D, 0x61, 0x74, 0x74,
				0x69, 0x73, 0x20, 0x65, 0x67, 0x65, 0x74, 0x20,
				0x65, 0x6C, 0x69, 0x74, 0x2E, 0x20, 0x4E, 0x75,
				0x6C, 0x6C, 0x61, 0x6D, 0x20, 0x73, 0x69, 0x74,
				0x20, 0x61, 0x6D, 0x65, 0x74, 0x20, 0x6D, 0x61,
				0x74, 0x74, 0x69, 0x73, 0x20, 0x65, 0x72, 0x6F,
				0x73, 0x2E, 0x20, 0x41, 0x6C, 0x69, 0x71, 0x75,
				0x61, 0x6D, 0x20, 0x66, 0x65, 0x6C, 0x69, 0x73,
				0x20, 0x65, 0x73, 0x74, 0x2C, 0x20, 0x66, 0x65,
				0x75, 0x67, 0x69, 0x61, 0x74, 0x20, 0x73, 0x65,
				0x64, 0x20, 0x74, 0x69, 0x6E, 0x63, 0x69, 0x64,
				0x75, 0x6E, 0x74, 0x20, 0x74, 0x69, 0x6E, 0x63,
				0x69, 0x64, 0x75, 0x6E, 0x74, 0x2C, 0x20, 0x6D,
				0x61, 0x6C, 0x65, 0x73, 0x75, 0x61, 0x64, 0x61,
				0x20, 0x76, 0x69, 0x76, 0x65, 0x72, 0x72, 0x61,
				0x20, 0x76, 0x65, 0x6C, 0x69, 0x74, 0x2E, 0x20,
				0x49, 0x6E, 0x20, 0x75, 0x74, 0x20, 0x6C, 0x6F,
				0x72, 0x65, 0x6D, 0x20, 0x69, 0x64, 0x20, 0x61,
				0x72, 0x63, 0x75, 0x20, 0x63, 0x72, 0x61, 0x73,
				0x20, 0x61, 0x6D, 0x65, 0x74, 0x2E, (byte) 0x80, 0x1C,
				0x64, 0x23, 0x18, 0x21, (byte) 0xE7, 0x21, (byte) 0xEA, 0x23,
				0x25, (byte) 0xD9, 0x02, 0x03, 0x05, 0x06, 0x07, 0x08,
				0x09, 0x0A, 0x0B, 0x0D, 0x0E, 0x0F, 0x10, 0x11,
				0x12, 0x09, 0x13, 0x14, 0x00, 0x08, 0x00, 0x1F,
				0x00, 0x25, 0x00, 0x2D, 0x00, 0x32, 0x00, 0x3D,
				0x00, 0x43, 0x00, 0x4B, 0x00, 0x4F, 0x00, 0x55,
				0x00, 0x5C, 0x00, 0x60, 0x00, 0x64, 0x00, 0x6E,
				0x00, 0x6F, 0x00, 0x78, 0x00, (byte) 0x81, 0x00, (byte) 0x83,
				0x00, (byte) 0x8C, 0x01, (byte) 0x8E, 0x01, (byte) 0x90, 0x01, (byte) 0x99,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x01,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x16,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, (byte) 0xAC});
		
		XMLFILE = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
				"<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">" +
				"<plist version=\"1.0\">" +
				"<dict>" +
				"<key>Array</key>" +
				"<array>" +
				"<true/>" +
				"<date>1987-07-31T06:00:00Z</date>" +
				"<real>42.28</real>" +
				"<integer>42</integer>" +
				"<integer>-1</integer>" +
				"<string>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam turpis purus, tempor et luctus vel, mattis eget elit. Nullam sit amet mattis eros. Aliquam felis est, feugiat sed tincidunt tincidunt, malesuada viverra velit. In ut lorem id arcu cras amet.</string>" +
				"<string>String</string>" +
				"<dict>" +
				"<key>CF$UID</key>" +
				"<integer>28</integer>" +
				"</dict>" +
				"<string>⌘⇧⇪⌥</string>" +
				"</array>" +
				"<key>Boolean</key>" +
				"<true/>" +
				"<key>Date</key>" +
				"<date>1987-07-31T06:00:00Z</date>" +
				"<key>Dictionary</key>" +
				"<dict>" +
				"<key>Boolean</key>" +
				"<true/>" +
				"<key>Date</key>" +
				"<date>1987-07-31T06:00:00Z</date>" +
				"<key>Float</key>" +
				"<real>42.28</real>" +
				"<key>Integer</key>" +
				"<integer>42</integer>" +
				"<key>Min</key>" +
				"<integer>-1</integer>" +
				"<key>Story</key>" +
				"<string>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam turpis purus, tempor et luctus vel, mattis eget elit. Nullam sit amet mattis eros. Aliquam felis est, feugiat sed tincidunt tincidunt, malesuada viverra velit. In ut lorem id arcu cras amet.</string>" +
				"<key>String</key>" +
				"<string>String</string>" +
				"<key>UID</key>" +
				"<dict>" +
				"<key>CF$UID</key>" +
				"<integer>28</integer>" +
				"</dict>" +
				"<key>UTF</key>" +
				"<string>⌘⇧⇪⌥</string>" +
				"</dict>" +
				"<key>Float</key>" +
				"<real>42.28</real>" +
				"<key>Integer</key>" +
				"<integer>42</integer>" +
				"<key>Min</key>" +
				"<integer>-1</integer>" +
				"<key>Story</key>" +
				"<string>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam turpis purus, tempor et luctus vel, mattis eget elit. Nullam sit amet mattis eros. Aliquam felis est, feugiat sed tincidunt tincidunt, malesuada viverra velit. In ut lorem id arcu cras amet.</string>" +
				"<key>String</key>" +
				"<string>String</string>" +
				"<key>UID</key>" +
				"<dict>" +
				"<key>CF$UID</key>" +
				"<integer>28</integer>" +
				"</dict>" +
				"<key>UTF</key>" +
				"<string>⌘⇧⇪⌥</string>" +
				"</dict>" +
				"</plist>").getBytes());
		
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
		//contents.put("Data", new NSData(DATA));
		TESTMAP.put("UTF", new NSString("⌘⇧⇪⌥"));
		TESTMAP.put("UID", new NSUID(28));
		
		NSDictionary dict = new NSDictionary(TESTMAP);
		NSArray array = new NSArray(TESTMAP.values().toArray(new NSObject[0]));
		
		TESTMAP.put("Dictionary", dict);
		TESTMAP.put("Array", array);
		
		DICT = new NSDictionary(TESTMAP);
	}
	
    @Before
    public void createTestData() throws IOException {
        emptyFile = folder.newFile("empty");
    }
	
	@Test
	public void readBinTest() throws PropertyListException, IOException {
		assertEquals(DICT, new BinaryParser(BINARYFILE).parse());
	}
	@Test
	public void readXMLTest() throws PropertyListException, IOException {
		assertEquals(DICT, new DOMXMLParser(XMLFILE).parse());
	}
	@Test
	public void writeBinTest() throws PropertyListException, IOException {
		writeTest(PropertyListFormat.BINARY);
	}
	@Test
	public void writeXMLTest() throws PropertyListException, IOException {
		writeTest(PropertyListFormat.XML);
	}
	public void writeTest(PropertyListFormat format) throws PropertyListException, IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PropertyListWriter.write(DICT, stream, format);
		assertEquals(DICT, PropertyListParser.parse(
				new ByteArrayInputStream(stream.toByteArray()))
			);
	}
	
	@Test
	public void emptyFileTest() throws Exception {
		thrown.expect(CompoundPropertyListException.class);
		thrown.expectMessage("File is not a binary property list.");
		thrown.expectMessage("The property list is not a valid XML document.");
		PropertyListParser.parse(emptyFile);
	}
	
	@Test
	public void nullFileTest() throws Exception {
		thrown.expect(NullPointerException.class);
		PropertyListParser.parse((File) null);
	}
	
	@Test
	public void nullInputStreamTest() throws Exception {
		thrown.expect(NullPointerException.class);
		PropertyListParser.parse((InputStream) null);
	}
	
    @After
    public void cleanUp() {
       assertTrue(emptyFile.exists());
    }
	
}
