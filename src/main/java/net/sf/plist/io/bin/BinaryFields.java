/*
Property List Binary Writer - LGPL 3.0 licensed
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
package net.sf.plist.io.bin;

import java.nio.charset.Charset;

interface BinaryFields {

	/** NULL byte */
	static byte          NULL = 0x0;
	
	// Values
	/** Byte indicating a {@link Boolean#FALSE} value */
	static byte     BOOLFALSE = 0x8;
	/** Byte indicating a {@link Boolean#TRUE} value */
	static byte      BOOLTRUE = 0x9;
	/** Filler byte */
	static byte          FILL = 0xF;
	
	// Object Types
	/** Byte mask indicating a integer value */
	static byte           INT = 0x1;
	/** Byte mask indicating a real value */
	static byte          REAL = 0x2;
	/** Byte mask indicating a date value */
	static byte          DATE = 0x3;
	/** Byte mask indicating a binary data value */
	static byte          DATA = 0x4;
	/** Byte mask indicating a ascii string value */
	static byte   ASCIISTRING = 0x5;
	/** Byte mask indicating a unicode string value */
	static byte UNICODESTRING = 0x6;
	/** Byte mask indicating a uid value (not implemented) */
	static byte           UID = 0x8;
	/** Byte mask indicating a array value */
	static byte         ARRAY = 0xA;
	/** Byte mask indicating a set value (not implemented) */
	static byte           SET = 0xC;
	/** Byte mask indicating a dictionary value */
	static byte          DICT = 0xD;
	
	/** Mask for extracting the objecttype (result must be shifted right 4 bits) */
	static byte OBJMASK = (byte)0xF0;
	/** Mask for extracting the length */
	static byte LENMASK = 0xF;
	
	/** Charset to parse ASCII strings */
	static Charset ASCIICHARSET = Charset.forName("US-ASCII");
	/** Charset to parse unicode strings */
	static Charset UNICODECHARSET = Charset.forName("UTF-16BE");
	
	/** Epoch constant, used to calculate dates */
	static long EPOCH = 978307200000L;

}
