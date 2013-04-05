package necromunda;

public class Ratskin extends Fighter {
	public Ratskin(String name, Gang ownGang) {
		super(name, new RatskinProfile(), ownGang);
		setCost(50);
	}
}
