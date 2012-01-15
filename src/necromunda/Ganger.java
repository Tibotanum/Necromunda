package necromunda;

public class Ganger extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new GangerProfile();
	}
	
	public Ganger(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(50);
	}
}
