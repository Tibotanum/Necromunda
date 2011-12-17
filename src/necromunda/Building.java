package necromunda;

import com.jme3.math.Vector3f;

public class Building {
	private Vector3f position;
	private float rotationAngle;
	private String identifier;
	
	public Building(float rotationAngle, Vector3f origin, String identifier) {
		this.position = origin;
		this.rotationAngle = rotationAngle;
		this.identifier = identifier;
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

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
