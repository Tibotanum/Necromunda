package necromunda;

import java.util.*;

import com.jme3.scene.*;
import com.jme3.scene.Spatial.CullHint;

public abstract class NecromundaNode extends Node {
	
	public NecromundaNode(String name) {
		super(name);
	}
	
	public abstract List<Spatial> getVisualSpatials();
	
	public abstract List<Geometry> getBoundingVolumes();
	
	public void displayVisualSpatials(boolean display) {
		displaySpatials(getVisualSpatials(), display);
	}
	
	public void displayBoundingVolumes(boolean display) {
		displaySpatials(getBoundingVolumes(), display);
	}
	
	private void displaySpatials(List<? extends Spatial> spatials, boolean display) {
		if (display) {
			for (Spatial spatial : spatials) {
				spatial.setCullHint(CullHint.Never);
			}
		}
		else {
			for (Spatial spatial : spatials) {
				spatial.setCullHint(CullHint.Always);
			}
		}
	}
}
