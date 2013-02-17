/* *********************************************************************
 *  This file is part of Hannah.
 *
 *  Hannah is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Hannah is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Hannah.  If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************* */

package org.openflexo.hannah;

import java.io.File;
import java.io.IOException;

/**
 * <p>The {@link VersionnedFileGenerator} allows to generate files using 
 * versionning and user modification supports. It uses Git as back-end for 
 * merges and diffs.</p>
 * 
 * TODO describe the life cycle.
 * TODO add API and callbacks for conflict handling
 * 
 * @author Jean-Charles Roger 
 *
 */
public class VersionnedFileGenerator {

	/** 
	 * <p>Base output folder for generator. All filename given for generation
	 * are prefixed by output folder.</p>
	 */
	private final File outputFolder;
	
	public VersionnedFileGenerator(File outputFolder) {
		assert outputFolder == null;
		
		this.outputFolder = outputFolder;
	}
	
	/**
	 * @return Retrieves generator output folder.
	 */
	public File getOutputFolder() {
		return outputFolder;
	}
	
	/**
	 * <p>Prepares the next generation. It collects the modifications made 
	 * since last generation. By default all modifications are kept and merged
	 * with the next generation, but the callback allows to cancel 
	 * modifications (for example if's pushed back to the model)..</p>
	 * @param callback called for each user modification.
	 * @throws IOException
	 */
	public void prepare(ModificationHandler callback) throws IOException {
		// does nothing yet
		outputFolder.mkdirs();
	}
	
	/**
	 * <p>Starts a new generation cycle.</p>
	 * @throws IOException
	 */
	public void start() throws IOException {
	}
	
	public void generate(String filename, String contents) throws IOException {
		generate(filename, contents, "UTF-8");
	}
	
	public void generate(String filename, String contents, String encoding) throws IOException {
		final File destinationFile = new File(outputFolder, filename);
		FileUtil.writeFile(destinationFile, contents, encoding);
	}

	/**
	 * <p>Ends the generation. It asks to resolve conflicts (if any). By 
	 * default conflict are resolved using user modifications.</p>
	 * @param callback
	 * @throws IOException
	 */
	public void end(ConflictHandler callback) throws IOException {
		// does nothing yet
	}
	
	/**
	 * <p>Closes the generation cycle. It merges accepted modification made by
	 * the generation.</p>
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		
	}
	
}
