package org.openflexo.hannah;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.RebaseResult.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.xid.basics.Basics;
import org.xid.basics.generation.MergerUtil;
import org.xid.basics.system.BasicShell;

public class TestRebase {

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
		modify(git, shell, 1);

		
		generate(git, shell, 2);
		modify(git, shell, 2);
		
		generate(git, shell, 3);
		//modify(git, shell, 3);
		
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
		rebaseBranch(git, GENERATION);
		
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
	
	private static void rebaseBranch(Git git, String name) throws GitAPIException {
		ObjectId upstream = null;
		for ( Ref branch : git.branchList().call() ) {
			if ( branch.getName().endsWith(name) ) {
				upstream = branch.getObjectId();
				break;
			}
		}
		
		
		final RebaseCommand rebase = git.rebase();
		rebase.setUpstream(upstream);
		
		System.out.println("---- [Start Rebase] ----");
		RebaseResult result = rebase.call();
		System.out.println("Rebases current branch to '"+  name + "': " + result.getStatus() );
		
		if ( result.getStatus() == Status.STOPPED ) {
			List<DiffEntry> diffs = git.diff().call();
			for ( DiffEntry entry : diffs ) {
				System.out.println("- " + entry);
			}
		}
		
		System.out.println("---- [End Rebase] ----");
	}
	
}

