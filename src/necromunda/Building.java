package necromunda;

import com.jme3.math.Vector3f;

public class Building {
	private String[] identifiers;
	
	public Building(String... identifiers) {
		this.identifiers = identifiers;
	}

	public String[] getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(String[] identifiers) {
		this.identifiers = identifiers;
	}
}
