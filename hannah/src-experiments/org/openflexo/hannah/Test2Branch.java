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
import java.io.PrintWriter;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.xid.basics.Basics;
import org.xid.basics.generation.MergerUtil;
import org.xid.basics.system.BasicShell;

public class Test2Branch {

	private final static String GENERATION = "generation";
	private final static String MASTER = "master";
	
	private static Git createGitRepo(File gitDir) throws GitAPIException {
		final InitCommand initCommand = Git.init();
		initCommand.setDirectory(gitDir);
		return initCommand.call();
    }

	 public static void main(String[] args) throws Exception {
		final File gitDir = new File("tmp/test1");
		final BasicShell shell = new BasicShell(gitDir, new PrintWriter(System.out));
		shell.setVerbose(false);
		shell.rm(".", null, Basics.RECURSIVE|Basics.HIDDEN);
		gitDir.mkdirs();

		
		// init Git repository
		Git git = createGitRepository(gitDir);

		// Generates using Generation branch
		generate(git, shell, 1);
		mergeBranch(git, GENERATION);
		modify(git, shell, 1);

		
		generate(git, shell, 2);
		mergeBranch(git, GENERATION);
		modify(git, shell, 2);
		
		generate(git, shell, 3);
		mergeBranch(git, GENERATION);
		
	}

	private static Git createGitRepository(final File gitDir) throws Exception {
		Git git = createGitRepo(gitDir);
		MergerUtil.writeFile(new File(gitDir, "README"), "Master branch", "UTF-8");
		addAndCommitAll(git, "Initial commit");
		
		createBranch(git, GENERATION);
		return git;
	}
	
	private static void generate(Git git, BasicShell shell, int serie) throws Exception {
		checkoutBranch(git, GENERATION);
		shell.cp("../../../Example1/src-gen"+serie+"/", ".", null, Basics.RECURSIVE | Basics.OVERWRITE);
		System.out.println("--> Generated code " + serie);
		addAndCommitAll(git, "Generation " + serie);
		checkoutBranch(git, MASTER);
	}
 
	private static void modify(Git git, BasicShell shell, int serie) throws Exception {
		shell.cp("../../../Example1/src-mod"+ serie +"/", ".", null, Basics.RECURSIVE | Basics.OVERWRITE);
		System.out.println("--> Modified code " + serie);
		addAndCommitAll(git, "Modification " + serie);
	}
	private static void addAndCommitAll(Git git, String message) throws GitAPIException {
		git.add().addFilepattern(".").call();
		
		final RevCommit call = git.commit().setMessage(message).call();
		System.out.println("Committed: " + call.getFullMessage());
	}
	
	private static void createBranch(Git git, String name) throws GitAPIException {
		git.branchCreate().setName(name).call();
	}
	
	private static void checkoutBranch(Git git, String name) throws GitAPIException {
		final Ref ref = git.checkout().setName(name).call();
		System.out.println("Checking out '" + ref.getName() + "'.");
	}
	
	private static void mergeBranch(Git git, String name) throws Exception {
		Ref generationRef = git.getRepository().getRef("refs/heads/" + name);
		final MergeResult generationMergeResult = git.merge().
				include(generationRef).
				call();
		
		
		System.out.println("Merging '"+ name +"' into 'master': " + generationMergeResult.getMergeStatus());
		
		if ( generationMergeResult.getMergeStatus() == MergeStatus.CONFLICTING ) {
			/*
			StringBuilder conflicts = new StringBuilder();
			conflicts.append("Conflicts: ");
			for ( Entry<String, int[][]> entry : generationMergeResult.getConflicts().entrySet() ) {
				conflicts.append("[");
				conflicts.append(entry.getKey());
				conflicts.append(",");
				conflicts.append(Arrays.deepToString(entry.getValue()));
				conflicts.append("] ");
			}
			System.out.println("Conflicts: "+ conflicts);
			*/
			
			Map<String, int[][]> allConflicts = generationMergeResult.getConflicts();
			 for (String path : allConflicts.keySet()) {
			        int[][] c = allConflicts.get(path);
			        System.out.println("Conflicts in file " + path);
			        for (int i = 0; i < c.length; ++i) {
			                System.out.println("  Conflict #" + i);
			                for (int j = 0; j < (c[i].length) - 1; ++j) {
			                        if (c[i][j] >= 0)
			                                System.out.println("    Chunk for "
			                                                + generationMergeResult.getMergedCommits()[j] + " starts on line #"
			                                                + c[i][j]);
			                }
			        }
			 }
		}
	}
}

