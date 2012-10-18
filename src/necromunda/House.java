package necromunda;

public enum House {
	ORLOCK("Orlock"),
	GOLIATH("Goliath"),
	ESCHER("Escher"),
	VAN_SAAR("Van Saar"),
	DELAQUE("Delaque"),
	CAWDOR("Cawdor"),
	BOUNTY("Bounty Hunters");
	
	private String literal;
	
	private House(String displayName) {
		this.literal = displayName;
	}
	
	@Override
	public String toString() {
		return literal;
	}
}
