package org.openflexo.hannah;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

public class TestUtil {

	
	public static void writeFile(VersionnedFileGenerator generator, String filename, String contents) throws IOException{
		final File absoluteFile = new File(generator.getOutputFolder(), filename);
		FileUtil.writeFile(absoluteFile, contents, "UTF-8");
	}
	
	public static void assertContents(VersionnedFileGenerator generator, String filename, String contents) throws IOException{
		final File absoluteFile = new File(generator.getOutputFolder(), filename);
		boolean equals = FileUtil.checkContents(absoluteFile, contents, "UTF-8");
		assertTrue("File '"+ filename +"' doesn't contains '"+ contents +"'.", equals);
	}

	public static void assertExists(VersionnedFileGenerator generator, String filename) throws IOException{
		final File completeFile = new File(generator.getOutputFolder(), filename);
		assertTrue("File '"+ filename +"' should exist.", completeFile.exists());
	}

	public static void assertDoesntExist(VersionnedFileGenerator generator, String filename) throws IOException{
		final File completeFile = new File(generator.getOutputFolder(), filename);
		assertFalse("File '"+ filename +"' should not exist.", completeFile.exists());
	}
	
}
