package necromunda;

public class ScavvyBoss extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyBossProfile();
	}
	
	public ScavvyBoss(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(120);
	}
}
