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
	
}
