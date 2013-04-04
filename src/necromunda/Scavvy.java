package necromunda;

public class Scavvy extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyBossProfile();
	}
	
	public Scavvy(String name, Gang ownGang) {
		super(name, new ScavvyProfile(), ownGang);
		setCost(120);
	}
}

