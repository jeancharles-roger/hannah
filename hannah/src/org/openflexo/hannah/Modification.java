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
