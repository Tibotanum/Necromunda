package necromunda;

public class RatskinChief extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RatskinChiefProfile();
	}
	
	public RatskinChief(String name, Gang ownGang) {
		super(name, new RatskinChiefProfile(), ownGang);
		setCost(120);
	}
}