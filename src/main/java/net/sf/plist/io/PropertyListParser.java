/*
Abstract Property List Parser - LGPL 3.0 licensed
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
package net.sf.plist.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sf.plist.NSObject;
import net.sf.plist.io.bin.BinaryParser;
import net.sf.plist.io.domxml.DOMXMLParser;

/**
 * Parses a property list to a tree consisting of {@link NSObject}s
 */
public abstract class PropertyListParser {

	/**	The file being parsed */
	protected final File file;
	/**	The inputstream being parsed */
	protected final InputStream input;
	
	/**
	 * Construct a new PropertyListParser which will parse a InputStream
	 * @param input the InputStream to parse
	 */
	public PropertyListParser(InputStream input) {
		this(null, input);
	}
	/**
	 * Construct a new PropertyListParser
	 * @param file the file to parse or <code>null</code>
	 * @param input the InputStream to parse
	 */
	protected PropertyListParser(File file, InputStream input) {
		this.file = file;
		this.input = input;
	}
	/**
	 * Parse the property list to a tree
	 * @return the root {@link NSObject} of the parsed property list
	 * @throws PropertyListException when parsing the property list failed for some reason
	 */
	public abstract NSObject parse() throws PropertyListException;
	
	/**
	 * Parse a property list file.
	 * @param file the file to parse
	 * @return the root {@link NSObject} of the parsed property list
	 * @throws PropertyListException when parsing the file failed for some reason
	 * @throws IOException when reading the file failed
	 */
	public static NSObject parse(File file) throws PropertyListException, IOException {
		if (file == null)
			throw new NullPointerException("file");
		try {
			return new BinaryParser(file).parse();
		} catch (PropertyListException e) {
			return new DOMXMLParser(file).parse();
		}
	}

	/**
	 * <p>Parse a property list InputStream.</p>
	 * <p>{@link InputStream#markSupported()} must be true.<br />
	 * When that isn't possible, it's recommended to read the entire stream into an {@link java.io.ByteArrayInputStream}.<br />
	 * This requirement is to make it possible to try multiple parsing methods without consuming the {@link InputStream}.<br />
	 * If an {@link InputStream} is used which does <b>not</b> support mark,
	 * the {@link InputStream} is packed into a {@link BufferedInputStream}, which does.</p>
	 * <p>This method will <b>not</b> intentionally close the {@link InputStream},
	 * you'll have to do that yourself after calling it.
	 * @param input the InputStream to parse (instance with mark support recommended)
	 * @return the root {@link NSObject} of the parsed property list
	 * @throws PropertyListException when parsing the input failed for some reason
	 * @throws IOException when reading the input failed
	 */
	public static NSObject parse(InputStream input) throws PropertyListException, IOException {
		if (input == null)
			throw new NullPointerException("input");
		if (!input.markSupported())
			return parse(new BufferedInputStream(input));
		try {
			input.mark(Integer.MAX_VALUE);
			return new BinaryParser(input).parse();
		} catch (Exception e) {
			input.reset();
			input.mark(Integer.MAX_VALUE);
			return new DOMXMLParser(input).parse();
		}
	}
}
