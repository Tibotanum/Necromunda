package necromunda;

public class RedemptionistDevotee extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RedemptionistDevoteeProfile();
	}
	
	public RedemptionistDevotee(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(25);
	}

	@Override
	public boolean isReliable() {
		return false;
	}
}
