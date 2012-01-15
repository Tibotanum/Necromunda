package necromunda;

public class Juve extends Fighter {
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
