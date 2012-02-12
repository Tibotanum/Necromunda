package necromunda;

import java.util.ArrayList;
import java.util.List;

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
}
