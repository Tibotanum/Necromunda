package necromunda;

public class Ganger extends Fighter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5525487967433437538L;

	public static FighterProfile getTemplateProfile() {
		return new GangerProfile();
	}
	
	public Ganger(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(50);
	}
}
