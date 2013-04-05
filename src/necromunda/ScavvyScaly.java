package necromunda;

public class ScavvyScaly extends Fighter {
	public ScavvyScaly(String name, Gang ownGang) {
		super(name, new ScavvyScalyProfile(), ownGang);
		setCost(120);
	}
}
