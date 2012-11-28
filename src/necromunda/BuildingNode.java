package necromunda;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.jme3.scene.*;

public class BuildingNode extends Node implements NecromundaNode {
	public BuildingNode(String name) {
		super(name);
	}

	@Override
	public BuildingNode clone(boolean cloneMaterials) {
		BuildingNode buildingNode = (BuildingNode)super.clone(cloneMaterials);
		
		int index = 0;
		
		do {
			index = buildingNode.detachChildNamed("ladder");
		}
		while (index > -1);
		
		List<LadderNode> visitedLadders = new ArrayList<LadderNode>();
		
		for (LadderNode ladder : getLadderNodes()) {
			if (visitedLadders.contains(ladder)) {
				continue;
			}
			
			LadderNode clonedLadder = new LadderNode("ladder");
			clonedLadder.setLocalTranslation(ladder.getLocalTranslation());
			LadderNode clonedPeerLadder = new LadderNode("ladder");
			clonedPeerLadder.setLocalTranslation(ladder.getPeer().getLocalTranslation());
			clonedLadder.setPeer(clonedPeerLadder);
			clonedPeerLadder.setPeer(clonedLadder);
			
			buildingNode.attachChild(clonedLadder);
			buildingNode.attachChild(clonedPeerLadder);
			
			visitedLadders.add(ladder.getPeer());
		}
		
		return buildingNode;
	}
	
	public List<LadderNode> getLadderNodes() {
		List<LadderNode> ladderNodes = new ArrayList<LadderNode>();
		
		for (Spatial child : getChildren()) {
			if (child.getName().equals("ladder")) {
				ladderNodes.add((LadderNode)child);
			}
		}
		
		return ladderNodes;
	}

	public List<Geometry> getBoundingVolumes() {
		List<Geometry> boundingVolumes = new ArrayList<Geometry>();
		
		Node boundsNode = (Node)getChild("bounds");
		
		for (Spatial child : boundsNode.getChildren()) {
			Geometry boundingVolume = (Geometry)child;
			boundingVolumes.add(boundingVolume);
		}
		
		return boundingVolumes;
	}
}
