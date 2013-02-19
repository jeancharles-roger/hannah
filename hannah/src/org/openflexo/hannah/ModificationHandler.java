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

/**
 * <p>A {@link ModificationHandler} is a callback called before a generation 
 * cycle to handle modifications made by the user since the last generation, if
 * any. It's called once for all the modifications. If there is no 
 * {@link Modification} it's still called with an empty list of 
 * {@link Modification}.</p>
 * 
 * <p>Modifications can be either accepted or rejected (not accepted) 
 * individually. Dealing which each {@link Modification} is the opportunity to
 * report the modification to the source model (if possible) and reject the
 * {@link Modification}. By default all {@link Modification} are accepted.</p> 
 * 
 * @author Jean-Charles Roger (jeancharles.roger@gmail.com)
 *
 */
public interface ModificationHandler {

	/**
	 * <p>Allows to handle all the {@link Modification}s.</p>
	 * @param modifications list of {@link Modification}s to handle (never null, may be empty).
	 */
	void modifications(List<Modification> modifications);
	
	/**
	 * {@link ModificationHandler} that accepts all modifications.
	 */
	public static final ModificationHandler accept = new ModificationHandler() {
		@Override
		public void modifications(List<Modification> modifications) {
			// not needed, the default is accepted, but still do it :).
			for ( Modification modification : modifications ) {
				modification.setAccept(true);
			}
		}
	};
	
	/**
	 * {@link ModificationHandler} that rejects all modifications.
	 */
	public static final ModificationHandler reject = new ModificationHandler() {
		@Override
		public void modifications(List<Modification> modifications) {
			for ( Modification modification : modifications ) {
				modification.setAccept(false);
			}
		}
	};
	
}
