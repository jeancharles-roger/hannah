package org.openflexo.hannah;

import static org.openflexo.hannah.TestUtil.assertContents;
import static org.openflexo.hannah.TestUtil.assertDoesntExist;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class RemovingFilesTests {

	private File baseFolder = new File("tmp/removingFiles");
	
	private VersionnedFileGenerator createGenerator(String name) {
		return new VersionnedFileGenerator(new File(baseFolder, name));
	}
	
	
	@Test
	public void testOneFile() throws IOException {
		VersionnedFileGenerator generator = createGenerator("oneFile");

		// generation one
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc");
		generator.generate("file2.txt", "abc");
		generator.end(TestUtil.noConflict);

		assertContents(generator, "file1.txt", "abc");
		assertContents(generator, "file2.txt", "abc");
		
		// generation two
		generator.start(TestUtil.noModification);
		generator.start(ModificationHandler.accept);
		generator.generate("file1.txt", "cba");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "cba");
		assertDoesntExist(generator, "file2.txt");
	}

	@Test
	public void testTwoFiles() throws IOException {
		VersionnedFileGenerator generator = createGenerator("twoFiles");
		
		// generation one
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc");
		generator.generate("file2.txt", "abc");
		generator.generate("file3.txt", "abc");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "abc");
		assertContents(generator, "file2.txt", "abc");
		assertContents(generator, "file3.txt", "abc");
		
		// generation two
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "cba");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "cba");
		assertDoesntExist(generator, "file2.txt");
		assertDoesntExist(generator, "file3.txt");
	}

}
