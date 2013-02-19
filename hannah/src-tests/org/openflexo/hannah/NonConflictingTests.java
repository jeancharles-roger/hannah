package org.openflexo.hannah;

import static org.openflexo.hannah.TestUtil.assertContents;

import java.io.File;

import org.junit.Test;

public class NonConflictingTests {

	private File baseFolder = new File("tmp/nonConflicting");
	
	private VersionnedFileGenerator createGenerator(String name) {
		File outputFolder = new File(baseFolder, name);
		FileUtil.delete(outputFolder);
		return new VersionnedFileGenerator(outputFolder);
	}
	
	@Test
	public void testOneFile() throws Exception {
		VersionnedFileGenerator generator = createGenerator("oneFile");
		
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "abc");
		
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "cba");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "cba");
	}

	@Test
	public void testTwoFiles() throws Exception {
		VersionnedFileGenerator generator = createGenerator("twoFiles");
		
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "abc");
		
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc");
		generator.generate("file2.txt", "cba");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "abc");
		assertContents(generator, "file2.txt", "cba");
	}

}
