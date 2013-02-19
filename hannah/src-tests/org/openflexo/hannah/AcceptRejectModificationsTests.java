package org.openflexo.hannah;

import static org.openflexo.hannah.TestUtil.assertContents;
import static org.openflexo.hannah.TestUtil.writeFile;

import java.io.File;

import org.junit.Test;

public class AcceptRejectModificationsTests {

	private File baseFolder = new File("tmp/acceptReject");
	
	private VersionnedFileGenerator createGenerator(String name) {
		File outputFolder = new File(baseFolder, name);
		FileUtil.delete(outputFolder);
		return new VersionnedFileGenerator(outputFolder);
	}
	
	@Test
	public void testAccept() throws Exception {
		VersionnedFileGenerator generator = createGenerator("accept");
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc\ndef\nijk\n");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "abc\ndef\nijk\n");
		
		writeFile(generator, "file1.txt", "abc\nddd\nijk\n");
		assertContents(generator, "file1.txt", "abc\nddd\nijk\n");
		
		generator.start(ModificationHandler.accept);
		generator.generate("file1.txt", "abc\nfed\nijk\n");
		generator.end(ConflictHandler.user);
		
		assertContents(generator, "file1.txt", "abc\nddd\nijk\n");
	}

	@Test
	public void testReject() throws Exception {
		VersionnedFileGenerator generator = createGenerator("reject");
		generator.start(TestUtil.noModification);
		generator.generate("file1.txt", "abc\ndef\nijk\n");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "abc\ndef\nijk\n");
		
		writeFile(generator, "file1.txt", "abc\nddd\nijk\n");
		assertContents(generator, "file1.txt", "abc\nddd\nijk\n");
		
		generator.start(ModificationHandler.reject);
		generator.generate("file1.txt", "abc\nfed\nijk\n");
		generator.end(TestUtil.noConflict);
		
		assertContents(generator, "file1.txt", "abc\nfed\nijk\n");
	}
	

}