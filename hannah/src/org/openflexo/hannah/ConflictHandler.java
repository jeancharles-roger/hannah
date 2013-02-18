package org.openflexo.hannah;

import java.util.List;

import org.openflexo.hannah.Conflict.Resolution;

/**
 * <p>A {@link ConflictHandler} is a callback called after a generation cycle
 * to handle conflict that may exit. For each generation the callback is called
 * once with all conflict. If there is no conflict it's called with an empty
 * list of conflict.</p> 
 * 
 * @author Jean-Charles Roger (jeancharles.roger@gmail.com)
 *
 */
public interface ConflictHandler {

	/**
	 * <p>Allows to handle the conflicts.</p>
	 * @param conflicts list of {@link Conflict} to handle (never null, may be empty). 
	 */
	void conflicts(List<Conflict> conflicts);
	
	/**
	 * {@link ConflictHandler} that resolves conflicts using USER version.
	 */
	public final static ConflictHandler userResolution = new ConflictHandler() {
		
		@Override
		public void conflicts(List<Conflict> conflicts) {
			for ( Conflict conflict : conflicts ) {
				conflict.setResolution(Resolution.USER);
			}
		}
	};
	
	/**
	 * {@link ConflictHandler} that resolves conflicts using GENERATION version.
	 */
	public final static ConflictHandler generationResolution = new ConflictHandler() {
		
		@Override
		public void conflicts(List<Conflict> conflicts) {
			for ( Conflict conflict : conflicts ) {
				conflict.setResolution(Resolution.GENERATION);
			}
		}
	};
}
