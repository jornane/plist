package net.sf.plist.io.bin;

import java.nio.charset.Charset;

public interface BinaryFields {

	/** NULL byte */
	static final public byte          NULL = 0x0;
	
	// Values
	/** Byte indicating a {@link Boolean#FALSE} value */
	static final public byte     BOOLFALSE = 0x8;
	/** Byte indicating a {@link Boolean#TRUE} value */
	static final public byte      BOOLTRUE = 0x9;
	/** Filler byte */
	static final public byte          FILL = 0xF;
	
	// Object Types
	/** Byte mask indicating a integer value */
	static final public byte           INT = 0x1;
	/** Byte mask indicating a real value */
	static final public byte          REAL = 0x2;
	/** Byte mask indicating a date value */
	static final public byte          DATE = 0x3;
	/** Byte mask indicating a binary data value */
	static final public byte          DATA = 0x4;
	/** Byte mask indicating a ascii string value */
	static final public byte   ASCIISTRING = 0x5;
	/** Byte mask indicating a unicode string value */
	static final public byte UNICODESTRING = 0x6;
	/** Byte mask indicating a uid value (not implemented) */
	static final public byte           UID = 0x8;
	/** Byte mask indicating a array value */
	static final public byte         ARRAY = 0xA;
	/** Byte mask indicating a set value (not implemented) */
	static final public byte           SET = 0xC;
	/** Byte mask indicating a dictionary value */
	static final public byte          DICT = 0xD;
	
	/** Mask for extracting the objecttype (result must be shifted right 4 bits) */
	static final public byte OBJMASK = (byte)0xF0;
	/** Mask for extracting the length */
	static final public byte LENMASK = 0xF;
	
	/** Charset to parse ASCII strings */
	static final public Charset ASCIICHARSET = Charset.forName("US-ASCII");
	/** Charset to parse unicode strings */
	static final public Charset UNICODECHARSET = Charset.forName("UTF-16BE");
	
	/** Epoch constant, used to calculate dates */
	static final public long EPOCH = 978307200000L;

}
