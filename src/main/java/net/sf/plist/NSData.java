/*
Property List Binary data - LGPL 3.0 licensed
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
package net.sf.plist;

import java.io.ByteArrayInputStream;

/**
 * <p>Represents a binary blob.</p>
 * 
 * <p>In this implementation, a primitive byte array or a {@link ByteArrayInputStream} is used to represent the {@link NSData}.</p>
 * @see ByteArrayInputStream
 */
public final class NSData extends NSObject {

	private final byte[] theData;
	
	/**
	 * Constructor.
	 * @param theData value of the new object
	 */
	public NSData(byte[] theData) {
		this.theData = theData;
	}
	
	/**
	 * {@inheritDoc}
	 * @see #toBytes()
	 */
	@Override
	public byte[] getValue() {
		return toBytes();
	}
	/**
	 * Get a {@link ByteArrayInputStream} which can be used to read the contents of this object.
	 * @return the {@link ByteArrayInputStream}
	 */
	public ByteArrayInputStream stream() {
		return new ByteArrayInputStream(theData);
	}
	/**
	 * <p>Return the contents of this object as primitive array.<br />
	 * Because the array is copied to keep this object immutable,
	 * it's recommended to use {@link #stream()} instead.</p>
	 * @return the array
	 */
	@Override
	public byte[] toBytes() {
		byte[] result = new byte[theData.length];
		System.arraycopy(theData, 0, result, 0, theData.length);
		return result;
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isTrue() {
		return theData.length > 0;
	}
	
	/** {@inheritDoc} */
	@Override
	public long toLong() {
		long l = 0;
		for(int it=0;it<theData.length;it++)
			l |= (theData[it]&0xFFL) << (8L*(theData.length-it-1L));
		return l;
	}
	
	/** {@inheritDoc} */
	@Override
	public double toDouble() {
		return Double.longBitsToDouble(toLong());
	}

}
