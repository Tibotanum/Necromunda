package necromunda;

public class RedemptionistPriest extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RedemptionistPriestProfile();
	}
	
	public RedemptionistPriest(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(160);
	}
}