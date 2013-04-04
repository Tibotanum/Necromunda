package necromunda;

public class RatskinTotemWarrior extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RatskinTotemWarriorProfile();
	}
	
	public RatskinTotemWarrior(String name, Gang ownGang) {
		super(name, new RatskinTotemWarriorProfile(), ownGang);
		setCost(60);
	}
}
