package necromunda;

public class Leader extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new LeaderProfile();
	}
	
	public Leader(String name, Gang ownGang) {
		super(name, new LeaderProfile(), ownGang);
		setCost(120);
	}
}
