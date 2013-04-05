package necromunda;

public class Ganger extends Fighter {
	public Ganger(String name, Gang ownGang) {
		super(name, new GangerProfile(), ownGang);
		setCost(50);
	}
}
