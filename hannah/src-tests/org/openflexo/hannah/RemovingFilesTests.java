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

import static org.openflexo.hannah.TestUtil.assertContents;
import static org.openflexo.hannah.TestUtil.assertDoesntExist;

import java.io.File;

import org.junit.Test;
import org.openflexo.hannah.Conflict.Resolution;

public class RemovingFilesTests {

	private File baseFolder = new File("tmp/removingFiles");
	
	private VersionnedFileGenerator createGenerator(String name) {
		File outputFolder = new File(baseFolder, name);
		FileUtil.delete(outputFolder);
		return new VersionnedFileGenerator(outputFolder);
	}
	
	@Test
	public void testOneFile() throws Exception {
		VersionnedFileGenerator generator = createGenerator("oneFile");

		// generation one
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc");
		generator.generate("file2.txt", "abc");
		generator.end(Resolution.USER);

		assertContents(generator, "file1.txt", "abc");
		assertContents(generator, "file2.txt", "abc");
		
		// generation two
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "cba");
		generator.end(Resolution.USER);
		
		assertContents(generator, "file1.txt", "cba");
		assertDoesntExist(generator, "file2.txt");
	}

	@Test
	public void testTwoFiles() throws Exception {
		VersionnedFileGenerator generator = createGenerator("twoFiles");
		
		// generation one
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc");
		generator.generate("file2.txt", "abc");
		generator.generate("file3.txt", "abc");
		generator.end(Resolution.USER);
		
		assertContents(generator, "file1.txt", "abc");
		assertContents(generator, "file2.txt", "abc");
		assertContents(generator, "file3.txt", "abc");
		
		// generation two
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "cba");
		generator.end(Resolution.USER);
		
		assertContents(generator, "file1.txt", "cba");
		assertDoesntExist(generator, "file2.txt");
		assertDoesntExist(generator, "file3.txt");
	}

}
