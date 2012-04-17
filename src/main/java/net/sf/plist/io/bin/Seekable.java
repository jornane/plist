package net.sf.plist.io.bin;

import java.io.IOException;

/**
 * Generic searchable interface, used internally by {@link BinaryParser} and {@link BinaryWriter}.
 */
interface Seekable {

	/** @see RandomAccessFile#read(byte[]) */
	int read(byte[] bytes) throws IOException;
	
	/** @see length() */
	long length() throws IOException;
	
	/** @see length() */
	void seek(long bytes) throws IOException;
	
	/** @see close() */
	void close() throws IOException;
	
	/** @see read() */
	int read() throws IOException;
	
	/** @see readLong() */
	long readLong() throws IOException;

}
