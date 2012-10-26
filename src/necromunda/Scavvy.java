package necromunda;

public class Scavvy extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyBossProfile();
	}
	
	public Scavvy(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(120);
	}
}

