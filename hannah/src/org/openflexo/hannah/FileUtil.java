package org.openflexo.hannah;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>Sets of utility function for files.</p>
 * 
 * @author Jean-Charles Roger (jeancharles.roger@gmail.com)
 *
 */
public class FileUtil {

	/** Writes given contents to destination file. */
	public static void writeFile(File destinationFile, String contents, String encoding) throws IOException {
		OutputStream stream = new BufferedOutputStream( new FileOutputStream(destinationFile));
		stream.write(contents.getBytes(encoding));
		stream.close();
	}

	/**
	 * <p>Loads a file contents as a String using platform encoding.</p>
	 * @param file file to load.
	 * @return the file contents as a {@link String}.
	 * @throws Exception 
	 */
	public static String loadContents(File file) throws IOException {
		final StringBuilder contents = new StringBuilder();
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		try {
			final byte[] buffer = new byte[1024];
			int read = in.read(buffer);
			while ( read >= 0 ) {
				contents.append(new String(buffer, 0, read));
				read = in.read(buffer);
			}
		} finally {
			in.close();
		}
		return contents.toString();
	}
	

	/** Checks if file contents is equals to given contents. */
	public static boolean checkContents(File file, String contents, String encoding) throws IOException {
  		int contentsIndex = 0;
		final int contentsLength = contents.length();
		final BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));

		try { 
			byte[] buffer = new byte[256];
			int read = inStream.read(buffer);
			while ( read >= 0 ) {
				// creates string from file
				String readString = new String(buffer, 0, read, encoding);
				final int endIndex = contentsIndex+readString.length();
				
				// creates string from contents (and check size)
				if ( contentsIndex >= contentsLength || endIndex > contentsLength ) return false;
				String contentsLocalString = contents.substring(contentsIndex, endIndex);
				contentsIndex = endIndex +1;
				// tests equality
				if ( !readString.equals(contentsLocalString) ) return false;
				
				// until now contents of string and file is equal.
				read = inStream.read(buffer);
			}
		} finally {
			inStream.close();
		}
		return contentsIndex > contentsLength;
	}
	
	/**
	 * <p>Recursively deletes given file or folder.</p>
	 * @param file to delete, also works with folders.
	 */
	public static void delete(File file) {
		if ( file.isDirectory() ) {
			// removes members.
			final File[] children = file.listFiles();
			if ( children != null ) {
				for ( File member : children ) {
					delete(member);
				}
			}
		}
		// delete file
		file.delete();
	}
	

}
