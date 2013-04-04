package necromunda;

public class RedemptionistDeacon extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RedemptionistDeaconProfile();
	}
	
	public RedemptionistDeacon(String name, Gang ownGang) {
		super(name, new RedemptionistDeaconProfile(), ownGang);
		setCost(60);
	}
}
