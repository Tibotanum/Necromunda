package necromunda;

public class RatskinChief extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RatskinChiefProfile();
	}
	
	public RatskinChief(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(120);
	}
}