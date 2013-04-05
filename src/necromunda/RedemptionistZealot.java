package necromunda;

public class RedemptionistZealot extends Fighter {
	public RedemptionistZealot(String name, Gang ownGang) {
		super(name, new RedemptionistZealotProfile(), ownGang);
		setCost(60);
	}
}