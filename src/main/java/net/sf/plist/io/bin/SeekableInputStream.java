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

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream in which seeking is possible.
 * This provides more flexibility than just using
 * {@link InputStream#mark(int)} and {@link InputStream#reset()}
 */
final class SeekableInputStream implements Seekable {

	/** The data source */
	private final InputStream input;
	/** Total length of the data source */
	private final int length;
	/** Current position in the data source */
	private int pos;
	
	/**
	 * Construct new instance, this will make a mark at the current position of input.
	 * After that, the input is consumed entirely to calculate the length.
	 * Because of this, input must support mark
	 * @param input	mark supporting {@link InputStream}
	 * @throws IOException	if consuming input fails
	 */
	public SeekableInputStream(InputStream input) throws IOException {
		this.input = input;
		input.mark(Integer.MAX_VALUE);
		byte[] buff = new byte[8000];
		int buffRead = 0;
		int bytesRead = 0;
		while((buffRead = input.read(buff)) != -1)
			bytesRead += buffRead;
		this.length = bytesRead;
		input.reset();
		pos = 0;
	}
	
	/** {@inheritDoc} */
	public int read(byte[] bytes) throws IOException {
		pos += bytes.length;
		return input.read(bytes);
	}
	
	/** {@inheritDoc} */
	public long length() throws IOException {
		return length;
	}
	
	/** {@inheritDoc} */
	public void seek(long pos) throws IOException {
		if (pos > Integer.MAX_VALUE)
			throw new IllegalArgumentException("No value bigger than Integer.MAX_VALUE allowed.");
		seek((int)pos);
	}
	
	/** @see #seek(long) */
	public void seek(int pos) throws IOException {
		if (pos < 0)
			throw new IllegalArgumentException("pos cannot be negative");
		if (pos < this.pos) {
			input.reset();
			input.skip(pos);
		} else {
			input.skip(pos - this.pos);
		}
		this.pos = pos;
	}
	
	/** {@inheritDoc} */
	public void close() throws IOException {
		input.close();
	}
	
	/** {@inheritDoc} */
	public int read() throws IOException {
		pos++;
		return input.read();
	}
	
	/** {@inheritDoc} */
	public long readLong() throws IOException {
		byte[] bytes = new byte[8];
		read(bytes);
		return BinaryParser.getLong(bytes);
	}

}
