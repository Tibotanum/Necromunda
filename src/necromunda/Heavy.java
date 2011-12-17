package necromunda;

public class Heavy extends Fighter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4883802413217771826L;

	public static FighterProfile getTemplateProfile() {
		return new HeavyProfile();
	}
	
	public Heavy(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(60);
	}
}
