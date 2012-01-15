package necromunda;

public class Heavy extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new HeavyProfile();
	}
	
	public Heavy(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(60);
	}
}
