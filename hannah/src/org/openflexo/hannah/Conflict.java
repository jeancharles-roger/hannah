package org.openflexo.hannah;

public interface Conflict {

	public enum Resolution {
		USER,
		GENERATION
	}
	
	Resolution getResolution();
	
	void setResolution(Resolution resolution);
	
	// TODO handles conflict description
}
