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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.merge.MergeChunk;
import org.eclipse.jgit.merge.MergeResult;
import org.openflexo.hannah.Conflict.Resolution;

/**
 * <p> {@link ConflictingFile} represents a file that is in conflict after a
 * generation cycle. It contains a list of {@link Conflict} present in the 
 * file.</p>
 * 
 * @author Jean-Charles Roger (jeancharles.roger@gmail.com)
 *
 */
public class ConflictingFile {

	/** File path in the Hannah repository. */
	private final String path;
	
	/** Computed list of {@link Conflict}s. */
	private final List<Conflict> conflicts;
	
	/** {@link MergeResult} used to construct the file contents. */
	private final MergeResult<RawText> result;
	
	public ConflictingFile(String path, MergeResult<RawText> result) {
		this.path = path;
		this.result = result;
		this.conflicts = computeConflicts();
	}
	
	/**
	 * <p>Computes the {@link Conflict} for given file using result.</p>
	 */
	private List<Conflict> computeConflicts() {
		final List<Conflict> conflicts = new ArrayList<Conflict>();
		
		MergeChunk userChunk = null;
		for (MergeChunk chunk : result) {
			switch ( chunk.getConflictState() ) {
			case FIRST_CONFLICTING_RANGE:
				userChunk = chunk;
				break;
				
			case NEXT_CONFLICTING_RANGE:
				assert userChunk != null;

				final Conflict conflict = new Conflict(
						userChunk.getBegin(), userChunk.getEnd(), 
						chunk.getBegin(), chunk.getEnd()
					);
				conflicts.add(conflict);
				
				// resets user
				userChunk = null;
				break;
			}
		}
		
		assert userChunk == null;

		return conflicts;
	}
	
	/**
	 * <p>Returns true if chunk should be printed. A chunk is printed when:
	 * <ul>
	 * <li>the chunk is not conflicting.</li>
	 * <li>the chunk is conflicting and the corresponding {@link Conflict} 
	 * resolution corresponds to it's side (USER or GENERATION).</li>
	 * </ul>
	 * </p>
	 * @param chunk chunk to test.
	 * @return true if needs to be printed.
	 */
	private boolean print(MergeChunk chunk) {
		switch (chunk.getConflictState() ) {
		case FIRST_CONFLICTING_RANGE:
			for ( Conflict conflict : conflicts ) {
				if ( conflict.getUserBegin() == chunk.getBegin() && conflict.getUserEnd() == chunk.getEnd() ) {
					return conflict.getResolution() == Resolution.USER;
				}
			}
			return false;
	
		case NEXT_CONFLICTING_RANGE:
			for ( Conflict conflict : conflicts ) {
				if ( conflict.getGenerationBegin() == chunk.getBegin() && conflict.getGenerationEnd() == chunk.getEnd() ) {
					return conflict.getResolution() == Resolution.GENERATION;
				}
			}
			return false;
			
		}
		return true;
	}

	/**
	 * <p>Path for file in Hannah repository.</p>
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * <p>List of {@link Conflict} in file.</p>
	 */
	public List<Conflict> getConflicts() {
		return conflicts;
	}

	/**
	 * <p>Returns the contents of the file using the resolution for each
	 * {@link Conflict}.</p>
	 */
	public String getContents() {
		final StringBuilder text = new StringBuilder();
		
		for (final MergeChunk chunk : result) {
			final RawText seq = result.getSequences().get(chunk.getSequenceIndex());
			
			// checks if it needs to be printed
			if ( print(chunk) ) {
				for (int i = chunk.getBegin(); i < chunk.getEnd(); i++) {
					text.append(seq.getString(i));
					text.append("\n");
				}
			}
		}

		return text.toString();
	}
	
}
