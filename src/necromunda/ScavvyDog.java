package necromunda;

public class ScavvyDog extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyDogProfile();
	}
	
	public ScavvyDog(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(0);
	}
}
