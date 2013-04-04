package necromunda;

public class ScavvyDog extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyDogProfile();
	}
	
	public ScavvyDog(String name, Gang ownGang) {
		super(name, new ScavvyDogProfile(), ownGang);
		setCost(0);
	}
}
