package necromunda;

import java.util.*;

import com.jme3.material.Material;
import com.jme3.scene.*;

public class PathNode extends Node implements NecromundaNode {
	public PathNode(String name) {
		super(name);
	}
	
	public void setMaterial(Material material) {
		Spatial base = getChild("pathBoxGeometry");
		base.setMaterial(material);
	}

	public List<Geometry> getBoundingVolumes() {
		List<Geometry> boundingVolumes = new ArrayList<Geometry>();
		Geometry boundingVolume = (Geometry)getChild("pathBoxGeometry");
		boundingVolumes.add(boundingVolume);
		return boundingVolumes;
	}
}
