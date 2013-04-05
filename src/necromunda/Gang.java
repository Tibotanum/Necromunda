package necromunda;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Gang implements Serializable {
	public enum Phase {
		MOVEMENT("Movement"),
		SHOOTING("Shooting"),
		HAND_TO_HAND("Hand to Hand"),
		RECOVERY("Recovery");
		
		private String literal;
		
		private Phase(String literal) {
			this.literal = literal;
		}

		@Override
		public String toString() {
			return literal;
		}
	}
	
	private String name;
	private Set<Fighter> gangMembers;
	private House house;
	
	public Gang(String name, House house) {
		this.name = name;
		this.house = house;
		gangMembers = new HashSet<Fighter>();
	}
	
	public List<Fighter> getHostileFighters(List<Gang> gangs) {
		List<Fighter> hostileFighters = new ArrayList<Fighter>();
		
		for (Gang gang : gangs) {
			if (gang != this) {
				hostileFighters.addAll(gang.getGangMembers());
			}
		}
		
		return hostileFighters;
	}

	public void addFighter(Fighter fighter) {
		gangMembers.add(fighter);
	}
	
	public void removeFighter(Fighter fighter) {
		gangMembers.remove(fighter);
	}
	
	public Set<Fighter> getGangMembers() {
		return gangMembers;
	}

	public void setGangMembers(Set<Fighter> gangMembers) {
		this.gangMembers = gangMembers;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", name, house);
	}
	
	public int getGangRating() {
		int rating = 0;
		
		for (Fighter gangMember : gangMembers) {
			rating += gangMember.getValue();
		}
		
		return rating;
	}
	
	public void turnStarted() {
		for (Fighter fighter : gangMembers) {
			fighter.turnStarted();
		}
	}
	
	public void turnEnded() {
		for (Fighter fighter : gangMembers) {
			fighter.turnEnded();
		}
	}

	public House getHouse() {
		return house;
	}
}
