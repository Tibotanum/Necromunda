package necromunda;

public class ScavvyScaly extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyScalyProfile();
	}
	
	public ScavvyScaly(String name, Gang ownGang) {
		super(name, new ScavvyScalyProfile(), ownGang);
		setCost(120);
	}
}
