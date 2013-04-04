package necromunda;

public class RatskinBrave extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RatskinBraveProfile();
	}
	
	public RatskinBrave(String name, Gang ownGang) {
		super(name, new RatskinBraveProfile(), ownGang);
		setCost(25);
	}

	@Override
	public boolean isReliable() {
		return false;
	}
}
