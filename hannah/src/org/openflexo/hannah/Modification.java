package org.openflexo.hannah;

import org.eclipse.jgit.diff.DiffEntry;

public interface Modification {

	boolean isAccepted();
	
	void setAccepted(boolean accept);
	
	// TODO handle modification description
	DiffEntry getDiff();
}
