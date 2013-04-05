package necromunda;

public class Heavy extends Fighter {
	public Heavy(String name, Gang ownGang) {
		super(name, new HeavyProfile(), ownGang);
		setCost(60);
	}
}
