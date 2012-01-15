package necromunda;

public class Leader extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new LeaderProfile();
	}
	
	public Leader(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(120);
	}
}
