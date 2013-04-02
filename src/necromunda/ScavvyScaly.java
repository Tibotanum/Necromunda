package necromunda;

public class ScavvyScaly extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyScalyProfile();
	}
	
	public ScavvyScaly(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(120);
	}
}
