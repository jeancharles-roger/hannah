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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestUtil {

	public static void writeFile(IterativeFileGenerator generator, String filename, String contents) throws IOException{
		final File absoluteFile = new File(generator.getOutputFolder(), filename);
		FileUtil.writeFile(absoluteFile, contents, "UTF-8");
	}
	
	public static void assertContents(IterativeFileGenerator generator, String filename, String contents) throws IOException{
		final File absoluteFile = new File(generator.getOutputFolder(), filename);
		assertTrue("File '"+ filename +"' doesn't exist.", absoluteFile.exists());
		boolean equals = FileUtil.checkContents(absoluteFile, contents, "UTF-8");
		assertTrue("File '"+ filename +"' doesn't contains '"+ contents +"'.", equals);
	}

	public static void assertExists(IterativeFileGenerator generator, String filename) throws IOException{
		final File completeFile = new File(generator.getOutputFolder(), filename);
		assertTrue("File '"+ filename +"' should exist.", completeFile.exists());
	}

	public static void assertDoesntExist(IterativeFileGenerator generator, String filename) throws IOException{
		final File completeFile = new File(generator.getOutputFolder(), filename);
		assertFalse("File '"+ filename +"' should not exist.", completeFile.exists());
	}
	
	public static final ModificationHandler modifications = new ModificationHandler() {
		@Override
		public void modifications(List<Modification> modifications) {
			assertTrue("Modifications should be detected.", modifications.size() > 0);
		}
	};
	
	public static final ModificationHandler noModification = new ModificationHandler() {
		@Override
		public void modifications(List<Modification> modifications) {
			assertEquals("No modification should exist.", 0, modifications.size());
		}
	};
	
	public static final ConflictHandler conflicts = new ConflictHandler() {
		@Override
		public void conflicts(List<ConflictingFile> conflictingFiles) {
			assertTrue("Conflicts should be detected.", conflictingFiles.size() > 0);
		}
	};
	
	public static final ConflictHandler noConflict = new ConflictHandler() {
		@Override
		public void conflicts(List<ConflictingFile> conflictingFiles) {
			assertEquals("No conflict should exist.", 0, conflictingFiles.size());
		}
	};
	
	
}
