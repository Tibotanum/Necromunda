package necromunda;

public class ScavvyBoss extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyBossProfile();
	}
	
	public ScavvyBoss(String name, Gang ownGang) {
		super(name, new ScavvyBossProfile(), ownGang);
		setCost(120);
	}
}
