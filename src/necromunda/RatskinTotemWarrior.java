package necromunda;

public class RatskinTotemWarrior extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RatskinTotemWarriorProfile();
	}
	
	public RatskinTotemWarrior(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(60);
	}
}
