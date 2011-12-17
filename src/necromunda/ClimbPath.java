package necromunda;

import com.jme3.math.Vector3f;

public class ClimbPath {
	private float length;
	private Vector3f start;
	
	public ClimbPath(Vector3f start) {
		this.start = start;
	}

	public void addToLength(float length) {
		this.length += length;
	}

	public float getLength() {
		return length;
	}

	public Vector3f getStart() {
		return start;
	}
}
