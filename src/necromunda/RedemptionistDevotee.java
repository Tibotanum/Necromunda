package necromunda;

public class RedemptionistDevotee extends Fighter {
	public RedemptionistDevotee(String name, Gang ownGang) {
		super(name, new RedemptionistDevoteeProfile(), ownGang);
		setCost(25);
	}

	@Override
	public boolean isReliable() {
		return false;
	}
}
