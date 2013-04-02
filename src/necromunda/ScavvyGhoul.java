package necromunda;

public class ScavvyGhoul extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyGhoulProfile();
	}
	
	public ScavvyGhoul(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(0);
	}
}
