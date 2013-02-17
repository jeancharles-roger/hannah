package org.openflexo.hannah;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class NonConflictingTests {

	private File baseFolder = new File("tmp/nonConflicting");
	
	private VersionnedFileGenerator createGenerator(String name) {
		return new VersionnedFileGenerator(new File(baseFolder, name));
	}
	
	private void checkFile(VersionnedFileGenerator generator, String filename, String contents) throws IOException{
		final File completeFile = new File(generator.getOutputFolder(), filename);
		boolean equals = FileUtil.checkContents(completeFile, contents, "UTF-8");
		assertTrue("File '"+ filename +"' doesn't contains '"+ contents +"'.", equals);
	}
	
	
	@Test
	public void testOneFile() throws IOException {
		VersionnedFileGenerator generator = createGenerator("oneFile");
		generator.prepare(null);
		generator.start();
		generator.generate("file1.txt", "abc");
		generator.end(null);
		generator.close();
		
		checkFile(generator, "file1.txt", "abc");
		
		generator.prepare(null);
		generator.start();
		generator.generate("file1.txt", "cba");
		generator.end(null);
		generator.close();
		
		checkFile(generator, "file1.txt", "cba");
	}

	@Test
	public void testTwoFiles() throws IOException {
		VersionnedFileGenerator generator = createGenerator("twoFiles");
		
		generator.prepare(null);
		generator.start();
		generator.generate("file1.txt", "abc");
		generator.end(null);
		generator.close();
		
		checkFile(generator, "file1.txt", "abc");
		
		generator.prepare(null);
		generator.start();
		generator.generate("file1.txt", "abc");
		generator.generate("file2.txt", "cba");
		generator.end(null);
		generator.close();
		
		checkFile(generator, "file1.txt", "abc");
		checkFile(generator, "file2.txt", "cba");
	}

}
