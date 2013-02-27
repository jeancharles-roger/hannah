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


/**
 * <p>A {@link Conflict} is a representation of a conflict inside a file. It 
 * contains the description of one conflict between user code and generation
 * code. It also contains the {@link Resolution} to apply to it.</p>
 * 
 * @author Jean-Charles Roger (jeancharles.roger@gmail.com)
 *
 */
public class Conflict {

	/**
	 * <p>A {@link Resolution} enum allows to choose between solution to 
	 * resolve a {@link Conflict}. It can be either User or Generation.</p>
	 * 
	 * @author Jean-Charles Roger (jeancharles.roger@gmail.com)
	 *
	 */
	public enum Resolution {
		USER,
		GENERATION
	}
	
	/** The {@link Conflict} resolution, User by default. */
	private Resolution resolution = Resolution.USER;
	
	/** The user beginning line for conflict. */
	private final int userBegin;
	
	/** The user ending line for conflict. */
	private final int userEnd;
	
	/** The generation beginning line for conflict. */
	private final int generationBegin;
	
	/** The generation ending line for conflict. */
	private final int generationEnd;
	
	/* Protected constructor. */
	protected Conflict(int userStart, int userEnd, int generationStart, int generationEnd) {
		this.userBegin = userStart;
		this.userEnd = userEnd;
		this.generationBegin = generationStart;
		this.generationEnd = generationEnd;
	}

	/** Gets {@link Resolution} for conflict. */
	public Resolution getResolution() {
		return resolution;
	}

	/** Sets {@link Resolution} for conflict. */
	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	/** The user beginning line for conflict. */
	public int getUserBegin() {
		return userBegin;
	}
	
	/** The user ending line for conflict. */
	public int getUserEnd() {
		return userEnd;
	}
	
	/** The generation beginning line for conflict. */
	public int getGenerationBegin() {
		return generationBegin;
	}
	
	/** The generation ending line for conflict. */
	public int getGenerationEnd() {
		return generationEnd;
	}
		
}
