package necromunda;

public class RedemptionistCrusader extends Fighter {
	public RedemptionistCrusader(String name, Gang ownGang) {
		super(name, new RedemptionistCrusaderProfile(), ownGang);
		setCost(50);
	}
}
