package org.openflexo.hannah;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class NonConflictingTests {

	private File baseFolder = new File("tmp/nonConflicting");
	private VersionnedFileGenerator generator;
	
	private VersionnedFileGenerator createGenerator(String name) {
		return new VersionnedFileGenerator(new File(baseFolder, name));
	}
	
	@Test
	public void testSimpleFile1() throws IOException {
		generator = createGenerator("simpleFile1");
		
		generator.startGeneration();
		generator.generate("file1.txt", "abc", "UTF-8");
		generator.endGeneration();
	}

}
