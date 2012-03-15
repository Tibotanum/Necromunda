package necromunda;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class BuildingNode extends Node {
	public BuildingNode(String name) {
		super(name);
	}

	@Override
	public Node clone(boolean cloneMaterials) {
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
}
