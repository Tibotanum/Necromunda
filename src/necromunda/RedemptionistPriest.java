package necromunda;

public class RedemptionistPriest extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RedemptionistPriestProfile();
	}
	
	public RedemptionistPriest(String name, Gang ownGang) {
		super(name, new RedemptionistPriestProfile(), ownGang);
		setCost(160);
	}
}