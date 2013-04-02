package necromunda;

public class RedemptionistZealot extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new GangerProfile();
	}
	
	public RedemptionistZealot(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(60);
	}
}