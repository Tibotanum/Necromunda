package necromunda;

public class RedemptionistCrusader extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RedemptionistCrusaderProfile();
	}
	
	public RedemptionistCrusader(String name, Gang ownGang) {
		super(name, new RedemptionistCrusaderProfile(), ownGang);
		setCost(50);
	}
}
