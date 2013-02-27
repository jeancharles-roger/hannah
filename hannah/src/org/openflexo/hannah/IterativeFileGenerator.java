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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeAlgorithm;
import org.eclipse.jgit.merge.MergeResult;

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
 * IterativeFileGenerator generator = createGenerator("output");
 * generator.start(ModificationHandler.accept);
 * generator.generate("file1.txt", "abc");
 * generator.generate("file2.txt", "def");
 * generator.end(ConflictHandler.user);
 * </code></pre>
 * </p>
 * 
 * @author Jean-Charles Roger 
 *
 */
public class IterativeFileGenerator {

	/**
	 * Git repository folder name.
	 */
	private final static String GIT_REPOSITORY_FILENAME = ".git";

	/**
	 * Hannah repository folder name.
	 */
	private final static String HANNAH_REPOSITORY_FILENAME = ".hannah";
	
	/**
	 * A dummy file name used to force the creation of the master branch.
	 */
	private final static String DUMMY_FILENAME = ".dummy";
	
	/**
	 * The generation branch name.
	 */
	private final static String GENERATION = "generation";
	
	/**
	 * The master branch name.
	 */
	private final static String MASTER = "master";
	
	/**
	 * When cleaning the output folder before generating, this list of file name is ignored.
	 */
	private final List<String> NOT_DELETED_FILES = Arrays.asList(GIT_REPOSITORY_FILENAME, DUMMY_FILENAME);
	
	/** 
	 * <p>Base output folder for generator. All filename given for generation
	 * are prefixed by output folder.</p>
	 */
	private final File outputFolder;
	
	/**
	 * <p>Hannah repository folder reference.</p>
	 */
	private final File hannahFolder;

	/**
	 * <p>Git repository folder reference.</p>
	 */
	private final File gitFolder;
	
	/**
	 * The {@link Git} runtime used to manipulate the repository.
	 */
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
				FileUtil.writeFile(new File(outputFolder, DUMMY_FILENAME), "For master branch creation\n", "UTF-8");
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
	 * @param callback callback to handle conflicts
	 * @throws IOException
	 */
	public void end(ConflictHandler callback) throws IOException, GitAPIException {
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
		final MergeStatus mergeStatus = git.merge().include(generationHead).call().getMergeStatus();
		
		// in case of conflicts, uses the resolution mode to choose the outcome
		if ( mergeStatus == MergeStatus.CONFLICTING ) {
			// maps to stores confliting files
			final Map<String, DirCacheEntry> baseEntry = new LinkedHashMap<String, DirCacheEntry>();
			final Map<String, DirCacheEntry> userEntry = new LinkedHashMap<String, DirCacheEntry>();
			final Map<String, DirCacheEntry> generationEntry = new LinkedHashMap<String, DirCacheEntry>();
			
			// for all conflicting entry collects base, user and generation entries. 
			final DirCache cache = repo.lockDirCache();
			for ( int i=0; i<cache.getEntryCount(); i++ ) {
				final DirCacheEntry entry = cache.getEntry(i);
				switch (entry.getStage()) {
				case DirCacheEntry.STAGE_1:
					baseEntry.put(entry.getPathString(), entry);
					break;
				case DirCacheEntry.STAGE_2:
					userEntry.put(entry.getPathString(), entry);
					break;
				case DirCacheEntry.STAGE_3:
					generationEntry.put(entry.getPathString(), entry);
					break;
				}
			}

			// creates list of conflicting files
			final List<ConflictingFile> conflictingFiles = new ArrayList<ConflictingFile>();
			final MergeAlgorithm mergeAlgorithm = new MergeAlgorithm();
			for ( final String path : baseEntry.keySet() ) {
				final RawText baseText = getRawText(baseEntry.get(path));
				final RawText userText = getRawText(userEntry.get(path));
				final RawText generationText = getRawText(generationEntry.get(path));
				
				final MergeResult<RawText> result = mergeAlgorithm.merge(RawTextComparator.DEFAULT, baseText, userText, generationText);
				conflictingFiles.add(new ConflictingFile(path, result));
				
			}
			// unlocks cache.
			cache.unlock();
			
			// calls the callback
			callback.conflicts(conflictingFiles);
			
			// prepares the reset command
			final ResetCommand reset = git.reset();

			// applies callback selections
			for ( final ConflictingFile conflictingFile : conflictingFiles ) {
				final File file = new File(repo.getWorkTree(), conflictingFile.getPath());
				FileUtil.writeFile(file, conflictingFile.getContents(), "UTF-8");
				
				reset.addPath(conflictingFile.getPath());
			}

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
		return new Modification(diff);
	}
	
	private RawText getRawText(DirCacheEntry entry) throws IOException {
		return getRawText(entry.getObjectId());
	}
	
	private RawText getRawText(ObjectId id) throws IOException {
		if ( ObjectId.zeroId().equals(id) ) {
			return RawText.EMPTY_TEXT;
		}
		final ObjectLoader loader = git.getRepository().open(id, Constants.OBJ_BLOB);
		return new RawText(loader.getCachedBytes());
	}
	
	
}
