package necromunda;

public class ScavvyGhoul extends Fighter {
	public ScavvyGhoul(String name, Gang ownGang) {
		super(name, new ScavvyGhoulProfile(), ownGang);
		setCost(0);
	}
}
