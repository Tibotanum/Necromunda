package necromunda;

public class Ratskin extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RatskinProfile();
	}
	
	public Ratskin(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(50);
	}
}
