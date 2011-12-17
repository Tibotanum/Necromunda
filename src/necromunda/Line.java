package necromunda;

import com.jme3.math.Vector3f;

public class Line extends com.jme3.math.Line {
	private boolean valid;
	
	public Line(Vector3f origin, Vector3f direction) {
		super(origin, direction);
	}

	public Vector3f getVector() {
		return getDirection().subtract(getOrigin());
	}
	
	public float length() {
		return getVector().length();
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
