package necromunda;

public class Scavvy extends Fighter {
	public Scavvy(String name, Gang ownGang) {
		super(name, new ScavvyProfile(), ownGang);
		setCost(120);
	}
}

