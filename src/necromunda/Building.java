package necromunda;

import java.util.*;
import java.util.Map.Entry;

import com.jme3.math.Vector3f;

public class Building {
	private Map<String, String> identifierToTexture;
	private List<String> bounds;
	
	public Building() {
		this.identifierToTexture = new HashMap<String, String>();
		this.bounds = new ArrayList<String>();
	}
	
	public void put(String identifier) {
		putModel(identifier, identifier);
	}

	public void putModel(String identifier, String texture) {
		identifierToTexture.put(identifier, texture);
	}
	
	public String get(String identifier) {
		return identifierToTexture.get(identifier);
	}
	
	public Set<Entry<String, String>> getEntrySet() {
		return identifierToTexture.entrySet();
	}
	
	public void putBounds(String identifier) {
		bounds.add(identifier);
	}
	
	public List<String> getBounds() {
		return bounds;
	}
}
