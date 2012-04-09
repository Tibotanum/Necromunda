package necromunda;

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
