package necromunda;

public class RatskinTotemWarrior extends Fighter {
	public RatskinTotemWarrior(String name, Gang ownGang) {
		super(name, new RatskinTotemWarriorProfile(), ownGang);
		setCost(60);
	}
}
