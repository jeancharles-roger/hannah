package org.openflexo.hannah;

import org.eclipse.jgit.diff.DiffEntry;

public interface Modification {

	boolean isAccept();
	
	void setAccept(boolean accept);
	
	// TODO handle modification description
	DiffEntry getDiff();
	
	public static class Stub implements Modification {
		
		private boolean accept = true;
		private final DiffEntry diff;
		
		public Stub(DiffEntry diff) {
			this.diff = diff;
		}
		
		@Override
		public boolean isAccept() {
			return accept;
		}
		
		@Override
		public void setAccept(boolean accept) {
			this.accept = accept;
		}
		
		@Override
		public DiffEntry getDiff() {
			return diff;
		}
	}
}
