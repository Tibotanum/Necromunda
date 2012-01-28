package necromunda;

import com.jme3.math.Vector3f;

public class Building {
	private Vector3f position;
	private float rotationAngle;
	private String[] identifiers;
	
	public Building(float rotationAngle, Vector3f origin, String... identifiers) {
		this.position = origin;
		this.rotationAngle = rotationAngle;
		this.identifiers = identifiers;
	}

	public Vector3f getOrigin() {
		return position;
	}

	public void setOrigin(Vector3f origin) {
		this.position = origin;
	}

	public float getRotationAngle() {
		return rotationAngle;
	}

	public String[] getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(String[] identifiers) {
		this.identifiers = identifiers;
	}
}
