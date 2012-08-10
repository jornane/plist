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
	/**	The {@link InputStream} being parsed */
	protected final InputStream input;
	
	/**
	 * Construct a new PropertyListParser which will parse a InputStream
	 * @param input the InputStream to parse
	 */
	public PropertyListParser(final InputStream input) {
		this(null, input);
	}
	/**
	 * Construct a new PropertyListParser. input represents the file (if not null).
	 * @param file the file to parse or <code>null</code>
	 * @param input the InputStream to parse
	 */
	protected PropertyListParser(final File file, final InputStream input) {
		this.file = file;
		this.input = input;
	}
	/**
	 * Parse the Property List input (provided in the constructor) to a tree of {@link NSObject}s
	 * @return the root {@link NSObject} of the parsed Property List
	 * @throws PropertyListException when parsing the Property List failed
	 */
	public abstract NSObject parse() throws PropertyListException;
	
	/**
	 * Parse a Property List file.
	 * @param file the file to parse
	 * @return the root {@link NSObject} of the parsed Property List
	 * @throws PropertyListException when parsing the file failed
	 * @throws IOException when reading the file failed
	 */
	public static NSObject parse(final File file) throws PropertyListException, IOException {
		if (file == null)
			throw new NullPointerException("file");
		try {
			return new BinaryParser(file).parse();
		} catch (PropertyListException e1) {
			try {
				return new DOMXMLParser(file).parse();
			} catch (PropertyListException e2) {
				throw new CompoundPropertyListException(e1, e2);
			}
		}
	}

	/**
	 * <p>Parse a Property List InputStream.</p>
	 * <p>It is recommended to use an implementation where {@link InputStream#markSupported()} returns true.<br />
	 * {@link InputStream}s not meeting this recommendation will be wrapped inside a {@link BufferedInputStream}, which does.<br />
	 * Mark is required to make it possible to try multiple parsing methods without consuming the {@link InputStream}.<br />
	 * <p>Warning: This method will <b>not</b> explicitly close the {@link InputStream}.
	 * You will have to close the stream yourself by calling {@link InputStream#close()} after {@link #parse(InputStream)}.
	 * @param input the InputStream to parse (instance with mark support recommended)
	 * @return the root {@link NSObject} of the parsed property list
	 * @throws PropertyListException when parsing the input failed
	 * @throws IOException when reading the input failed
	 */
	public static NSObject parse(final InputStream input) throws PropertyListException, IOException {
		if (!input.markSupported())
			return parse(new BufferedInputStream(input));
		try {
			input.mark(Integer.MAX_VALUE);
			return new BinaryParser(input).parse();
		} catch (PropertyListException e1) {
			try {
				input.reset();
				input.mark(Integer.MAX_VALUE);
				return new DOMXMLParser(input).parse();
			} catch (PropertyListException e2) {
				throw new CompoundPropertyListException(e1, e2);
			}
		}
	}
}
