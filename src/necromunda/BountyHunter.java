package necromunda;

public class BountyHunter extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new BountyHunterProfile();
	}
	
	public BountyHunter(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(175);
	}
}