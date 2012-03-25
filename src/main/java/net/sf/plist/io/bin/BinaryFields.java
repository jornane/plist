package net.sf.plist.io.bin;

import java.nio.charset.Charset;

interface BinaryFields {

	/** NULL byte */
	static public byte          NULL = 0x0;
	
	// Values
	/** Byte indicating a {@link Boolean#FALSE} value */
	static public byte     BOOLFALSE = 0x8;
	/** Byte indicating a {@link Boolean#TRUE} value */
	static public byte      BOOLTRUE = 0x9;
	/** Filler byte */
	static public byte          FILL = 0xF;
	
	// Object Types
	/** Byte mask indicating a integer value */
	static public byte           INT = 0x1;
	/** Byte mask indicating a real value */
	static public byte          REAL = 0x2;
	/** Byte mask indicating a date value */
	static public byte          DATE = 0x3;
	/** Byte mask indicating a binary data value */
	static public byte          DATA = 0x4;
	/** Byte mask indicating a ascii string value */
	static public byte   ASCIISTRING = 0x5;
	/** Byte mask indicating a unicode string value */
	static public byte UNICODESTRING = 0x6;
	/** Byte mask indicating a uid value (not implemented) */
	static public byte           UID = 0x8;
	/** Byte mask indicating a array value */
	static public byte         ARRAY = 0xA;
	/** Byte mask indicating a set value (not implemented) */
	static public byte           SET = 0xC;
	/** Byte mask indicating a dictionary value */
	static public byte          DICT = 0xD;
	
	/** Mask for extracting the objecttype (result must be shifted right 4 bits) */
	static public byte OBJMASK = (byte)0xF0;
	/** Mask for extracting the length */
	static public byte LENMASK = 0xF;
	
	/** Charset to parse ASCII strings */
	static public Charset ASCIICHARSET = Charset.forName("US-ASCII");
	/** Charset to parse unicode strings */
	static public Charset UNICODECHARSET = Charset.forName("UTF-16BE");
	
	/** Epoch constant, used to calculate dates */
	static public long EPOCH = 978307200000L;

}
