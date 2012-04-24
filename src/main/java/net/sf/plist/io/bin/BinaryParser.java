/*
Property List Binary Parser - LGPL 3.0 licensed
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import net.sf.plist.*;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

/**
 * Parses binary property list files to a tree consisting of {@link NSObject}s
 */
public class BinaryParser extends PropertyListParser implements BinaryFields {

	/** Bytes expected at the start of the file */
	static final protected byte[] STARTMAGIC = "bplist00".getBytes();
	/** Bytes expected near the end of the file */
	static final protected byte[] ENDMAGIC = new byte[6];
	
	// The stream is private to keep the possibility open for a custom seekable object
	/** The stream */
	private Seekable stream;
	
	/** Size of offset entries in bytes */
	protected byte offsetEntrySize;
	/** Size of object references in bytes */
	protected byte objRefSize;
	/** Number of objects in stream */
	protected int numObjects;
	/** Starting address of the root object */
	protected long rootAddr;
	/** Starting address of the offset table */
	protected long offsetTableOffset;
	/** The offset table */
	protected int[] offsetTable;
	/** The parse result */
	protected NSObject result;
	/** The exception indicating why parsing failed */
	protected PropertyListException pleResult;
	
	/**
	 * Convert an array of bytes to a long
	 * @param bytes the byte array (8 bytes max)
	 * @return the long
	 */
	public static long getLong(byte... bytes) { // Get a long, 64 bits
		// bytes.length <= 8
		// byte&0xFFL makes it a positive long
		long l = 0;
		for(int it=0;it<bytes.length;it++)
			l |= (bytes[it]&0xFFL) << (8L*(bytes.length-it-1L));
		return l;
	}
	/**
	 * Convert an array of bytes to a integer
	 * @param bytes the byte array (4 bytes max)
	 * @return the int
	 */
	public static int getInteger(byte... bytes) { // Get an int, 32 bits
		// bytes.length <= 4
		// byte&0xFF makes it a positive int
		int i = 0;
		for(int it=0;it<bytes.length;it++)
			i |= (bytes[it]&0xFF) << (8*(bytes.length-it-1));
		return i;
	}
	
	/** @see PropertyListParser#parse(File) */
	public BinaryParser(File file) throws PropertyListException, IOException {
		super(file, null);
		if (file == null)
			throw new NullPointerException("file");
	}
	
	/** @see PropertyListParser#PropertyListParser(InputStream) */
	public BinaryParser(InputStream input) {
		super(input);
		if (input == null)
			throw new NullPointerException("input");
	}
	
	/** {@inheritDoc} */
	@Override
	public synchronized NSObject parse() throws PropertyListException {
		if (result != null) return result;
		try {
			if (pleResult != null) throw pleResult;
			byte[] magicStartTest = new byte[8];
			byte[] magicEndTest = new byte[6];
			byte[] metaData = new byte[26];
			// Check if the stream contains a binary property list
			if (file != null)
				stream = new SeekableFile(file, "r");
			else if (input != null)
				stream = new SeekableInputStream(input);
			else
				throw new NullPointerException("Both file and input are null, this should never happen. Please report it on http://plist.sf.net .");
			stream.read(magicStartTest);
			if (!Arrays.equals(STARTMAGIC, magicStartTest))
				throw new PropertyListException("File is not a binary property list.");
			stream.seek(this.stream.length()-32);
			stream.read(magicEndTest);
			if (!Arrays.equals(ENDMAGIC, magicEndTest))
				throw new PropertyListException("File is not a binary property list.");
			stream.read(metaData);
			
			// Read metadata
			offsetEntrySize = metaData[0];
			objRefSize = metaData[1];
			numObjects = getInteger(metaData[6],metaData[7],metaData[8],metaData[9]);
			rootAddr = 8L+getLong(metaData[10],metaData[11],metaData[12],metaData[13],metaData[14],metaData[15],metaData[16],metaData[17]);
			offsetTableOffset = getLong(metaData[18],metaData[19],metaData[20],metaData[21],metaData[22],metaData[23],metaData[24],metaData[25]);
			
			// Read offset table
			offsetTable = new int[numObjects];
			byte[] offsetBytes = new byte[numObjects*offsetEntrySize];
			stream.seek(offsetTableOffset);
			stream.read(offsetBytes);
			for (int i=0;i<numObjects; i++) {
				byte[] offsetTableEntry = new byte[offsetEntrySize];
				System.arraycopy(offsetBytes, i*offsetEntrySize, offsetTableEntry, 0, offsetEntrySize);
				offsetTable[i] = getInteger(offsetTableEntry);
			}
			return result = parseNode(rootAddr);
		} catch (IOException e) {
			throw pleResult = new PropertyListException("Unable to parse binary property list", e);
		} catch (PropertyListException ple) {
			throw pleResult = ple; // store the exception so it can be re-thrown when parse is called again
		}
	}
	
	/**
	 * Parse a node to a NSObject on a given start address
	 * @param addr the start address
	 * @return the NSObject
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSObject parseNode(long addr) throws IOException, PropertyListException {
		stream.seek(addr);
		final int identifier = stream.read();
		if (identifier < 0)
			throw new PropertyListException("Premature end of file.");
		final byte length = (byte) (identifier&0xF);
		final byte type = (byte) ((identifier>>4)&0xF);
		
		if (type == NULL) switch(length) { // In this case, length is the actual value
			case NULL: return null;
			case BOOLTRUE: return NSBoolean.TRUE;
			case BOOLFALSE: return NSBoolean.FALSE;
		}
		switch(type) {
			case INT: return parseInteger(length);
			case REAL: return parseReal(length);
			case DATE: return parseDate(length);
			case DATA: return parseData(length);
			case ASCIISTRING: return parseASCII(length);
			case UNICODESTRING: return parseUnicode(length);
			case UID: return parseUid(length);
			case ARRAY: return parseArray(length);
			case SET: return parseSet(length);
			case DICT: return parseDictionary(length);
		}
		throw new PropertyListException("Invalid objectclass: "+Integer.toString(identifier|0, 16).toUpperCase());
	}
	
	/**
	 * Parse a node to a NSInteger on a given start address
	 * @param length the length byte
	 * @return the NSInteger
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSInteger parseInteger(byte length) throws IOException {
		byte[] buffer = new byte[1<<length]; // 2^length
		stream.read(buffer);
		return new NSInteger(getLong(buffer));
	}
	
	/**
	 * Parse a node to a NSReal on a given start address
	 * @param length the length byte
	 * @return the NSReal
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSReal parseReal(byte length) throws IOException {
		byte[] buffer = new byte[1<<length]; // 2^length
		stream.read(buffer);
		return new NSReal(Double.longBitsToDouble(getLong(buffer)));
	}
	
	/**
	 * Parse a node to a NSDate on a given start address
	 * @param length the length byte
	 * @return the NSDate
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSDate parseDate(byte length) throws IOException {
		assert length == 8;
		return new NSDate(Double.longBitsToDouble(stream.readLong()));
	}
	
	/**
	 * Parse a node to a NSData on a given start address
	 * @param length the length byte
	 * @return the NSData
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSData parseData(byte length) throws IOException, PropertyListException {
		byte[] buffer = new byte[getLength(length)];
		stream.read(buffer);
		return new NSData(buffer);
	}
	
	/**
	 * Parse a node to a NSString on a given start address, parsing with an ASCII charset
	 * @param length the length byte
	 * @return the NSString
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSString parseASCII(byte length) throws IOException, PropertyListException {
		byte[] buffer = new byte[getLength(length)];
		stream.read(buffer);
		return new NSString(new String(buffer, ASCIICHARSET));
	}
	
	/**
	 * Parse a node to a NSString on a given start address, parsing with an unicode charset
	 * @param length the length byte
	 * @return the NSString
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSString parseUnicode(byte length) throws IOException, PropertyListException {
		byte[] buffer = new byte[2*getLength(length)];
		stream.read(buffer);
		return new NSString(new String(buffer, UNICODECHARSET));
	}
	
	/**
	 * Parse a NSUID (not implemented)
	 * @param length the length byte
	 * @return null
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSObject parseUid(byte length) throws IOException, PropertyListException {
		//byte[] buffer = new byte[length+1];
		//file.read(buffer);
		// TODO how should those be handled?
		return null;
	}
	
	/**
	 * Parse a node to a NSArray on a given start address
	 * @param length the length byte
	 * @return the NSArray
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSArray parseArray(byte length) throws IOException, PropertyListException {
		int length2 = getLength(length);
		byte[] buffer = new byte[length2*objRefSize];
		ArrayList<NSObject> result = new ArrayList<NSObject>(length2);
		stream.read(buffer);
		for(int i=0;i<length2;i++) {
			byte[] objRefArr = new byte[objRefSize];
			System.arraycopy(buffer, objRefSize*i, objRefArr, 0, objRefSize);
			int objRef = getInteger(objRefArr);
			result.add(parseNode(offsetTable[objRef]));
		}
		return new NSArray(result);
	}
	
	/**
	 * Parse a NSSet (not implemented)
	 * @param length the length byte
	 * @return null
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSObject parseSet(byte length) throws IOException, PropertyListException {
		//byte[] buffer = new byte[getLength(length)*objRefSize];
		//file.read(buffer);
		// TODO how should those be handled?
		return null;
	}
	
	/**
	 * Parse a node to a NSDictionary on a given start address
	 * @param length the length byte
	 * @return the NSDictionary
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected NSDictionary parseDictionary(byte length) throws IOException, PropertyListException {
		int length2 = getLength(length);
		byte[] buffer = new byte[2*length2*objRefSize];
		TreeMap<String,NSObject> result = new TreeMap<String,NSObject>();
		stream.read(buffer);
		for(int i=0;i<length2;i++) {
			byte[] keyRefArr = new byte[objRefSize];
			System.arraycopy(buffer, objRefSize*i, keyRefArr, 0, objRefSize);
			int keyRef = getInteger(keyRefArr);
			byte[] objRefArr = new byte[objRefSize];
			System.arraycopy(buffer, length2*objRefSize+objRefSize*i, objRefArr, 0, objRefSize);
			int objRef = getInteger(objRefArr);
			result.put(parseNode(offsetTable[keyRef]).toString(), parseNode(offsetTable[objRef]));
		}
		return new NSDictionary(result);
	}
	
	/**
	 * <p>Read the length</p>
	 * <p>When the lengthMask is 0xF (0000 1111), an integer follows containing the actual length</p>
	 * <p>lengthMask must be equal or smaller than LENMASK</p>
	 * @param lengthMask the length mask
	 * @return the actual length (equal to lengthMask if lengthMask != 0xF)
	 * @throws IOException when reading the stream failed while parsing
	 * @throws PropertyListException when parsing fails
	 */
	protected int getLength(byte lengthMask) throws IOException, PropertyListException {
		if (lengthMask==LENMASK) {
			final int data = stream.read();
			if (data == -1)
				throw new PropertyListException("Unexpected EOF while reading object length.");
			final byte type = (byte) ((data>>4)&0xF);
			if (type != INT)
				throw new PropertyListException("Expecting object length to be integer.");
			final int intLen = 1<<(data&0xF);
			final byte[] intData = new byte[intLen];
			stream.read(intData);
			return getInteger(intData);
		} else return lengthMask;
	}

}
