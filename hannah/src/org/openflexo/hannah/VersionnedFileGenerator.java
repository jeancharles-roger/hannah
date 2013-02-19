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
import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;

/**
 * <p>The {@link VersionnedFileGenerator} allows to generate files using 
 * versioning and user modification supports. It uses Git as back-end for 
 * merges and diffs.</p>
 * 
 * TODO describe the life cycle.
 * TODO add API for modification and conflict descriptions
 * 
 * @author Jean-Charles Roger 
 *
 */
public class VersionnedFileGenerator {

	private final static String GIT_REPOSITORY_FILENAME = ".git";
	
	private final static String GENERATION = "generation";
	private final static String MASTER = "master";
	
	private final List<String> NOT_DELETED_FILES = Arrays.asList(GIT_REPOSITORY_FILENAME, "Dummy");
	
	/** 
	 * <p>Base output folder for generator. All filename given for generation
	 * are prefixed by output folder.</p>
	 */
	private final File outputFolder;
	
	private Git git;
	
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
	 * 
	 * @param callback called for each user modification.
	 * 
	 * @throws IOException for any file manipulation gone wrong.
	 * @throws GitAPIException  if Git can't manipulate the repository.
	 */
	public void start(ModificationHandler callback) throws IOException, GitAPIException {
		// creates output folder if needed.
		if ( outputFolder.exists() == false ) {
			outputFolder.mkdirs();
		}
		
		// checks that output folder is a folder and accessible.
		if ( outputFolder.isDirectory() == false || outputFolder.canRead() == false || outputFolder.canWrite() == false ) {
			throw new IOException("Folder '"+ outputFolder +"' isn't accessible.");
		}

		// is the folder already a generation folder, checks the git file.
		if ( new File(outputFolder, GIT_REPOSITORY_FILENAME).exists() == false ) {
			// no repository, creates the repository.
			git = Git.init().setDirectory(outputFolder).setBare(false).call();
			
			// creates the master branch
			FileUtil.writeFile(new File(outputFolder, "Dummy"), "For master branch creation", "UTF-8");
			git.add().addFilepattern("Dummy").call();
			git.commit().setMessage("Creates master branch.").call();

			// create the generation branch
			git.branchCreate().setName(GENERATION).call();
			
		} else {
			// repository exists, opens it.
			git = Git.open(outputFolder);
		}

		if ( git.diff().call().size() > 0 ) {
			// commits user modifications (without discrimination)
			git.add().addFilepattern(".").call();
			git.rm().addFilepattern(".").call();
			git.commit().setMessage("User modifications").call();
		}
		
		// checkouts generation branch
		git.checkout().setName(GENERATION).call();
		
		// clear files before new generation
		final File[] children = outputFolder.listFiles();
		if ( children != null ) { 
			for ( File child : children ) {
				if ( NOT_DELETED_FILES.contains(child.getName()) == false ) {
					FileUtil.delete(child);
				}
			}
		}
	}
	
	public void generate(String filename, String contents) throws IOException {
		generate(filename, contents, "UTF-8");
	}
	
	/**
	 * <p>Create file with given file name and contents. The filename is a
	 * relative path 
	 * @param filename
	 * @param contents
	 * @param encoding
	 * @throws IOException
	 */
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
	public void end(ConflictHandler callback) throws IOException, GitAPIException {
		final Status status = git.status().call();

		// checks if needs commit.
		if ( status.isClean() == false ) {
			git.add().addFilepattern(".").call();
			git.commit().setMessage("Generation").call();
		}
		// checks out master branch
		git.checkout().setName(MASTER).call();
		
		// merges generation branch with master (resolving conflict with USER).
		final Ref generationHead = git.getRepository().getRef(GENERATION);
		git.merge().include(generationHead).setStrategy(MergeStrategy.OURS).call();
	}
	
}
