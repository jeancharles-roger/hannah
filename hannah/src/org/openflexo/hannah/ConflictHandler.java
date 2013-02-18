package org.openflexo.hannah;

import java.util.List;

import org.openflexo.hannah.Conflict.Resolution;

/**
 * <p>A {@link ConflictHandler} is a callback called after a generation cycle
 * to handle {@link Conflict} that may exist. For each generation the callback 
 * is called once with all {@link Conflict}. If there is no conflict it's 
 * still called with an empty list of {@link Conflict}.</p> 
 * 
 * <p>Conflicts can be handled by selecting the {@link Resolution#GENERATION}
 * side or {@link Resolution#USER} side. Each {@link Conflict} can be 
 * individually resolved. For more complex resolutions, check {@link Conflict}.
 * If no resolution is set, all conflicts will resolved choosing user side.</p>
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
	public final static ConflictHandler user = new ConflictHandler() {
		
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
	public final static ConflictHandler generation = new ConflictHandler() {
		
		@Override
		public void conflicts(List<Conflict> conflicts) {
			for ( Conflict conflict : conflicts ) {
				conflict.setResolution(Resolution.GENERATION);
			}
		}
	};
}
