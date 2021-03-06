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

import java.util.List;

import org.openflexo.hannah.Conflict.Resolution;

/**
 * <p>A {@link ConflictHandler} is a callback called after a generation cycle
 * to handle {@link ConflictingFile} that may exist. For each generation the 
 * callback is called once with all {@link ConflictingFile}. If there is no 
 * conflict it's still called with an empty list of {@link ConflictingFile}.
 * </p> 
 * 
 * <p>Conflicts in files can be handled by selecting the 
 * {@link Resolution#GENERATION} side or {@link Resolution#USER} side. Each 
 * {@link Conflict} can be individually resolved. For more complex resolutions,
 * check {@link Conflict}. If no resolution is set, all conflicts will resolved 
 * choosing user side.</p>
 * 
 * @author Jean-Charles Roger (jeancharles.roger@gmail.com)
 *
 */
public interface ConflictHandler {

	/**
	 * <p>Allows to handle the conflicts.</p>
	 * @param conflicts list of {@link ConflictingFile} to handle (never null, may be empty). 
	 */
	void conflicts(List<ConflictingFile> conflictingFiles);
	
	/**
	 * {@link ConflictHandler} that resolves conflicts using USER version.
	 */
	public final static ConflictHandler user = new ConflictHandler() {
		@Override
		public void conflicts(List<ConflictingFile> conflictingFiles) {
			for ( final ConflictingFile file : conflictingFiles ) {
				for ( final Conflict conflict : file.getConflicts() ) {
					conflict.setResolution(Resolution.USER);
				}
			}
		}
	};
	
	/**
	 * {@link ConflictHandler} that resolves conflicts using GENERATION version.
	 */
	public final static ConflictHandler generation = new ConflictHandler() {
		@Override
		public void conflicts(List<ConflictingFile> conflictingFiles) {
			for ( final ConflictingFile file : conflictingFiles ) {
				for ( final Conflict conflict : file.getConflicts() ) {
					conflict.setResolution(Resolution.GENERATION);
				}
			}
		}
	};
}
