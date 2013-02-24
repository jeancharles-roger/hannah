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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.openflexo.hannah.Conflict.Resolution;

/**
 * <p>The {@link IterativeFileGenerator} allows to generate files using 
 * versions and user modification supports. It uses Git as back-end for 
 * merges and diffs. It preserves user modifications all along the generation
 * cycles.</p>
 * 
 * <p>An {@link IterativeFileGenerator} is really to use. When you need to 
 * generate code, just create an {@link IterativeFileGenerator} on the
 * destination folder. Then prepares the generation with the start method. All
 * generated file can be generated using the generate methods but it's not
 * required. At the end close the cycle with the end method.</p>
 * 
 * <p>It will look like this:
 * <pre><code>
 * IterativeFileGenerator generator = createGenerator("oneFile");
 * generator.start(TestUtil.noModification);
 * generator.generate("file1.txt", "abc");
 * generator.generate("file2.txt", "abc");
 * generator.end(Resolution.USER);
 * </code></pre>
 * </p>
 * 
 * TODO describe the life cycle.
 * TODO add API for modification and conflict descriptions
 * 
 * @author Jean-Charles Roger 
 *
 */
public class IterativeFileGenerator {

	private final static String GIT_REPOSITORY_FILENAME = ".git";
	private final static String HANNAH_REPOSITORY_FILENAME = ".hannah";
	private final static String DUMMY_FILENAME = ".dummy";
	
	private final static String GENERATION = "generation";
	private final static String MASTER = "master";
	
	private final List<String> NOT_DELETED_FILES = Arrays.asList(GIT_REPOSITORY_FILENAME, DUMMY_FILENAME);
	
	/** 
	 * <p>Base output folder for generator. All filename given for generation
	 * are prefixed by output folder.</p>
	 */
	private final File outputFolder;
	
	private final File hannahFolder;

	private final File gitFolder;
	
	private Git git;
	
	public IterativeFileGenerator(File outputFolder) {
		assert outputFolder == null;
		
		this.outputFolder = outputFolder;
		this.hannahFolder = new File(outputFolder, HANNAH_REPOSITORY_FILENAME);
		this.gitFolder = new File(outputFolder, GIT_REPOSITORY_FILENAME);
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

		if ( gitFolder.exists() ) {
			throw new IOException("Output folder is already a Git working copy.");
		}
		
		// is the folder already a generation folder, checks the git file.
		if ( hannahFolder.exists() == false ) {
			// no repository, creates the repository.
			git = Git.init().setDirectory(outputFolder).setBare(false).call();
			
			final String[] children = outputFolder.list();
			if ( children == null || children.length <= 1 ) {
				// if folder only contains '.git' creates a dummy file.
				FileUtil.writeFile(new File(outputFolder, DUMMY_FILENAME), "For master branch creation", "UTF-8");
			}

			// creates the master branch
			git.add().addFilepattern(".").call();
			git.commit().setMessage("Creates master branch.").call();
			
			// create the generation branch
			git.branchCreate().setName(GENERATION).call();
			
		} else {
			// repository exists, renames it and opens it.
			hannahFolder.renameTo(gitFolder);
			git = Git.open(outputFolder);
		}

		// retrieves diffs
		final List<DiffEntry> diffEntries = git.diff().call();
		if ( diffEntries.size() > 0 ) {
			// creates modifications and calls the handler.
			final List<Modification> modifications = createModificationList(diffEntries);
			if ( callback != null ) {
				callback.modifications(modifications);
			}
			
			// checks which modifications should be committed
			boolean somethingAccepted = false;
			final AddCommand add = git.add();
			for ( Modification modification : modifications ) {
				if ( modification.isAccept() ) {
					somethingAccepted = true;
					add.addFilepattern(modification.getDiff().getNewPath());
				}
			}
			
			// calls add on accepted modifications
			if ( somethingAccepted ) {
				add.call();
				git.commit().setMessage("User modifications").call();
			}
			
			// reverts un-commited diffs
			git.revert().call();
			
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
	 * @param resolution conflict resolution mode.
	 * @throws IOException
	 */
	public void end(Resolution resolution) throws IOException, GitAPIException {
		final Status status = git.status().call();

		// checks if needs commit.
		if ( status.isClean() == false ) {
			
			// checks for files to add
			boolean execute = false;
			final AddCommand add = git.add();
			for ( String filename : status.getModified() ) {
				execute = true;
				add.addFilepattern(filename);
			}
			
			for ( String filename : status.getUntracked() ) {
				execute = true;
				add.addFilepattern(filename);
			}
			if ( execute ) add.call();

			// checks for files to remove
			execute = false;
			final RmCommand rm = git.rm();
			for ( String filename : status.getMissing() ) {
				execute = true;
				rm.addFilepattern(filename);
			}
			if ( execute ) rm.call();
		
			git.commit().setMessage("Generation").call();
		}
		
		// checks out master branch
		git.checkout().setName(MASTER).call();
		
		// merges generation branch with master (resolving conflict with USER).
		final Repository repo = git.getRepository();
		final Ref generationHead = repo.getRef(GENERATION);
		final MergeResult merge = git.merge().include(generationHead).setStrategy(MergeStrategy.RESOLVE).call();
		
		// in case of conflicts, uses the resolution mode to choose the outcome
		if ( merge.getMergeStatus() == MergeStatus.CONFLICTING ) {
			// prepares the reset command
			final ResetCommand reset = git.reset();
			
			// for all conflicting entry in the cache, select the correct entry. 
			final int selectedStage = resolution == Resolution.USER ? DirCacheEntry.STAGE_2 : DirCacheEntry.STAGE_3;
			final DirCache cache = repo.lockDirCache();
			for ( int i=0; i<cache.getEntryCount(); i++ ) {
				final DirCacheEntry entry = cache.getEntry(i);
				if ( entry.getStage() == selectedStage ) {
					final String pathString = entry.getPathString();
					
					// if entry is the correct resolution (it's a conflict) checks it out. 
					final File file = new File(repo.getWorkTree(), pathString);
					DirCacheCheckout.checkoutEntry(repo, file, entry);
					
					// add path to reset command
					reset.addPath(pathString);
					
				}
			}
			cache.unlock();

			// resets repository state to allows commit.
			reset.call();
			// commit resolutions
			git.commit().setMessage("User/Generation merge conflicts resolutions.").call();
			
			
		}	
		
		// renames git repository to hannah
		gitFolder.renameTo(hannahFolder);
	}
	
	private List<Modification> createModificationList(List<DiffEntry> diffs) {
		List<Modification> modifications = new ArrayList<Modification>(diffs.size());
		for ( DiffEntry diff : diffs) {
			modifications.add(createModification(diff));
		}
		return modifications;
	}
	
	private Modification createModification(DiffEntry diff) {
		return new Modification.Stub(diff);
	}
}
