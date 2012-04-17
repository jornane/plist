package net.sf.plist.io.bin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

final class SeekableFile extends RandomAccessFile implements Seekable {

	public SeekableFile(File file, String mode) throws FileNotFoundException {
		super(file, mode);
	}

	public SeekableFile(String file, String mode) throws FileNotFoundException {
		super(file, mode);
	}

}
