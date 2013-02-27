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
import static org.openflexo.hannah.TestUtil.writeFile;

import java.io.File;

import org.junit.Test;

public class ConflictingTests {

	private File baseFolder = new File("tmp/conflicting");
	
	private IterativeFileGenerator createGenerator(String name) {
		File outputFolder = new File(baseFolder, name);
		FileUtil.delete(outputFolder);
		return new IterativeFileGenerator(outputFolder);
	}
	
	@Test
	public void testOneFile1() throws Exception {
		IterativeFileGenerator generator = createGenerator("oneFile1");
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc\ndef\nijk\n");
		generator.end(ConflictHandler.user);
		
		assertContents(generator, "file1.txt", "abc\ndef\nijk\n");
		
		writeFile(generator, "file1.txt", "abc\nddd\nijk\n");
		assertContents(generator, "file1.txt", "abc\nddd\nijk\n");
		
		generator.start(ModificationHandler.accept);
		generator.generate("file1.txt", "abc\nfed\nijk\n");
		generator.end(ConflictHandler.user);
		
		assertContents(generator, "file1.txt", "abc\nddd\nijk\n");
	}

	@Test
	public void testOneFile2() throws Exception {
		IterativeFileGenerator generator = createGenerator("oneFile2");
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc\ndef\nijk\n");
		generator.end(ConflictHandler.generation);
		
		assertContents(generator, "file1.txt", "abc\ndef\nijk\n");
		
		writeFile(generator, "file1.txt", "abc\nddd\nijk\n");
		assertContents(generator, "file1.txt", "abc\nddd\nijk\n");
		
		generator.start(ModificationHandler.accept);
		generator.generate("file1.txt", "abc\nfed\nijk\n");
		generator.end(ConflictHandler.generation);
		
		assertContents(generator, "file1.txt", "abc\nfed\nijk\n");
	}
	

	@Test
	public void testOneFile3() throws Exception {
		IterativeFileGenerator generator = createGenerator("oneFile3");
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc\ndef\nijk\nlmn\nopq\nrst\nuvw\n");
		generator.end(ConflictHandler.user);
		
		assertContents(generator, "file1.txt", "abc\ndef\nijk\nlmn\nopq\nrst\nuvw\n");
		
		writeFile(generator, "file1.txt", "abc\nddd\nijk\nlmn\nooo\nrst\nuvw\n");
		assertContents(generator, "file1.txt", "abc\nddd\nijk\nlmn\nooo\nrst\nuvw\n");
		
		generator.start(ModificationHandler.accept);
		generator.generate("file1.txt", "abc\ndef\nijk\nlmn\nopq\nrst\nuvw\nxyz\n");
		generator.end(ConflictHandler.user);
		
		assertContents(generator, "file1.txt", "abc\nddd\nijk\nlmn\nooo\nrst\nuvw\nxyz\n");
	}
	
	@Test
	public void testOneFile4() throws Exception {
		IterativeFileGenerator generator = createGenerator("oneFile4");
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc\ndef\nijk\n");
		generator.end(ConflictHandler.user);
		
		assertContents(generator, "file1.txt", "abc\ndef\nijk\n");
		
		writeFile(generator, "file1.txt", "abc\nijk\n");
		assertContents(generator, "file1.txt", "abc\nijk\n");
		
		generator.start(ModificationHandler.accept);
		generator.generate("file1.txt", "abc\nfed\nijk\n");
		generator.end(ConflictHandler.user);
		
		assertContents(generator, "file1.txt", "abc\nijk\n");
	}
	
	
}
