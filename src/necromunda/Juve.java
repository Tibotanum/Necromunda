package necromunda;

public class Juve extends Fighter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3857630510416946128L;

	public static FighterProfile getTemplateProfile() {
		return new JuveProfile();
	}
	
	public Juve(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(25);
	}

	@Override
	public boolean isReliableMate() {
		return false;
	}
}
