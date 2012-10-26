package necromunda;

public class Scaley extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyBossProfile();
	}
	
	public Scaley(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(120);
	}
}
