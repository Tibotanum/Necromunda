package necromunda;

public class RatskinBrave extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RatskinBraveProfile();
	}
	
	public RatskinBrave(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(25);
	}

	@Override
	public boolean isReliable() {
		return false;
	}
}
