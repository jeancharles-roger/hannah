package org.openflexo.angelina;

import java.io.File;
import java.util.List;

/**
 * <p> A {@link Merger} allows to merge new contents into files.
 * @author Jean-Charles Roger
 *
 */
public class Merger {

	/**
	 * <p>Merges a file with the new contents for this file. If the destination
	 * file already exist it may ask for </p>
	 * 
	 * @param destinationFile  destination file for new contents.
	 * @param contents new contents.
	 * @param encoding encoding to use.
	 * @throws Exception
	 */
	public void merge(File destinationFile, String contents, String encoding) throws Exception {
		
	}
	
	public List<File> getMergedFileList() {
		return null;
	}
	
}
