/*
Abstract Property List Writer - LGPL 3.0 licensed
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.plist.NSObject;
import net.sf.plist.io.bin.BinaryWriter;
import net.sf.plist.io.domxml.DOMXMLWriter;

/**
 * Serializes a tree consisting of {@link NSObject}s to a property list.
 */
public abstract class PropertyListWriter {

	/**
	 * The default format used when {@link #write(NSObject, File)} is called.
	 * This usually is {@link PropertyListFormat#BINARY} on Mac OS X or {@link PropertyListFormat#XML} on other operating systems,
	 * but might differ since this field is <b>not</b> final.
	 * If you need a specific format you should <b>not</b> rely on the contents of this field to stay the same.
	 * Instead you should specify the format as an argument in the {@link #write(NSObject, File, PropertyListFormat)} method.
	 * If you do not need a specific format then it is safe to use the {@link #write(OutputStream)} method without {@link PropertyListFormat} argument.  
	 */
	public static PropertyListFormat DEFAULTFORMAT;
	
	static {
		DEFAULTFORMAT = getDefaultFormat();
	}
	
	/** The root object of the tree */
	final protected NSObject root;
	
	/**
	 * Construct a new PropertyListWriter
	 * @param root the root of the tree
	 */
	public PropertyListWriter(NSObject root) {
		this.root = root;
	}
	
	/**
	 * Get the default format for generating Property List files.
	 * @return	the default format
	 */
	protected static PropertyListFormat getDefaultFormat() {
		String osName = System.getProperty("os.name");
		if (osName != null && osName.toLowerCase().contains("mac"))
			return PropertyListFormat.BINARY;
		else
			return PropertyListFormat.XML;
	}

	/**
	 * Write the property list to a stream
	 * @param stream the stream to write the property list to
	 * @throws PropertyListException when generating the property list fails
	 * @throws IOException when writing to the stream fails
	 */
	public abstract void write(OutputStream stream) throws PropertyListException, IOException;
	
	/**
	 * Convert a tree to a property list and write it to a stream 
	 * @param root the root of the tree 
	 * @param stream the stream to write the property list to 
	 * @param format the format to use when writing
	 * @throws PropertyListException when generating the property list fails
	 * @throws ParserConfigurationException when unable to create an XML document
	 * @throws IOException when writing to the stream fails
	 */
	public static void write(NSObject root, OutputStream stream, PropertyListFormat format)
		throws PropertyListException, IOException
	{
		switch(format) {
			case BINARY:new BinaryWriter(root).write(stream);break;
			case XML:new DOMXMLWriter(root).write(stream);break;
			default:throw new NullPointerException("format");
		}
	}
	/**
	 * Convert a tree to a property list and write it to a stream
	 * @see PropertyListWriter#write(NSObject, OutputStream, PropertyListFormat)
	 */
	public static void write(NSObject root, File file)
		throws PropertyListException, IOException
	{
		write(root, file, null);
	}
	/**
	 * Convert a tree to a property list and write it to a file 
	 * @param root the root of the tree 
	 * @param file the file to write to 
	 * @param format the format to use when writing
	 * @throws PropertyListException when generating the property list fails
	 * @throws ParserConfigurationException when unable to create an XML document
	 * @throws IOException when writing to the stream fails
	 */
	public static void write(NSObject root, File file, PropertyListFormat format)
		throws PropertyListException, IOException
	{
		if (format == null) {
			format = PropertyListParser.getFormatForFile(file);
			if (format == null)
				format = DEFAULTFORMAT;
			PropertyListFormat.FORMATS.put(file, format);
		}
		write(root, new FileOutputStream(file), format);
	}

}
