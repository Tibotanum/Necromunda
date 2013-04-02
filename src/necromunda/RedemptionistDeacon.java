package necromunda;

public class RedemptionistDeacon extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new RedemptionistDeaconProfile();
	}
	
	public RedemptionistDeacon(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(60);
	}
}
