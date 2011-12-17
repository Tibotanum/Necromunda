package necromunda;

public class Leader extends Fighter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7662079099501229795L;

	public static FighterProfile getTemplateProfile() {
		return new LeaderProfile();
	}
	
	public Leader(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(120);
	}
}
