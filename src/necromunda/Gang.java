package necromunda;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;


public class Gang implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2367078332448161962L;

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
	
	public enum House {
		ORLOCK("Orlock"),
		GOLIATH("Goliath"),
		ESCHER("Escher"),
		VAN_SAAR("Van Saar"),
		DELAQUE("Delaque"),
		CAWDOR("Cawdor");
		
		private String literal;
		
		private House(String displayName) {
			this.literal = displayName;
		}
		
		@Override
		public String toString() {
			return literal;
		}
	}
	
	private String name;
	private Phase phase;
	private Set<Fighter> gangMembers;
	private Set<Fighter> outOfActionGangMembers;
	public House house;
	
	public Gang(String name, House house) {
		this.name = name;
		this.house = house;
		phase = Phase.MOVEMENT;
		gangMembers = new HashSet<Fighter>();
		outOfActionGangMembers = new HashSet<Fighter>();
	}
	
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	
	public List<Fighter> getHostileGangers(List<Gang> gangs) {
		List<Fighter> hostileGangers = new ArrayList<Fighter>();
		
		for (Gang gang : gangs) {
			if (gang != this) {
				hostileGangers.addAll(gang.getGangMembers());
			}
		}
		
		return hostileGangers;
	}

	public Phase getPhase() {
		return phase;
	}

	public void addGanger(Fighter ganger) {
		gangMembers.add(ganger);
	}
	
	public void removeGanger(Fighter ganger) {
		gangMembers.remove(ganger);
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

	public Set<Fighter> getOutOfActionGangMembers() {
		return outOfActionGangMembers;
	}

	public void setOutOfActionGangMembers(Set<Fighter> outOfActionGangMembers) {
		this.outOfActionGangMembers = outOfActionGangMembers;
	}
	
	public int getGangRating() {
		int rating = 0;
		
		for (Fighter gangMember : gangMembers) {
			rating += gangMember.getValue();
		}
		
		return rating;
	}
	
	public void turnStarted() {
		for (Fighter ganger : gangMembers) {
			ganger.turnStarted();
		}

		setPhase(Phase.MOVEMENT);
	}
	
	public void turnEnded() {
		for (Fighter ganger : gangMembers) {
			ganger.turnEnded();
		}
	}
}
