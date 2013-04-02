package necromunda;

public class RedemptionistCrusader extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RedemptionistCrusaderProfile();
	}
	
	public RedemptionistCrusader(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(50);
	}
}
