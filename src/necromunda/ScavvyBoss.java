package necromunda;

public class ScavvyBoss extends Fighter {
	public ScavvyBoss(String name, Gang ownGang) {
		super(name, new ScavvyBossProfile(), ownGang);
		setCost(120);
	}
}
