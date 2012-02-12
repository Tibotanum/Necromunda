package necromunda;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.jme3.scene.Node;

public class BuildingNode extends Node {
	private List<Ladder> ladders;
	
	public BuildingNode(String name) {
		super(name);
		ladders = new ArrayList<Ladder>();
	}

	public List<Ladder> getLadders() {
		return ladders;
	}

	public void setLadders(List<Ladder> ladders) {
		this.ladders = ladders;
	}

	@Override
	public Node clone(boolean cloneMaterials) {
		BuildingNode buildingNode = (BuildingNode)super.clone(cloneMaterials);
		
		List<Ladder> visitedLadders = new ArrayList<Ladder>();
		
		Set<Ladder> clonedLadders = new HashSet<Ladder>();
		
		for (Ladder ladder : ladders) {
			if (visitedLadders.contains(ladder)) {
				continue;
			}
			
			Ladder clonedLadder = new Ladder(ladder.getLineNode().getLocalTranslation());
			Ladder clonedPeerLadder = new Ladder(ladder.getPeer().getLineNode().getLocalTranslation());
			clonedLadder.setPeer(clonedPeerLadder);
			clonedPeerLadder.setPeer(clonedLadder);
			
			clonedLadders.add(clonedLadder);
			clonedLadders.add(clonedPeerLadder);
			
			buildingNode.attachChild(clonedLadder.getLineNode());
			buildingNode.attachChild(clonedPeerLadder.getLineNode());
			
			visitedLadders.add(clonedPeerLadder);
		}
		
		buildingNode.setLadders(new ArrayList<Ladder>(clonedLadders));
		
		return buildingNode;
	}
}
