package necromunda;

public class Leader extends Fighter {
	public Leader(String name, Gang ownGang) {
		super(name, new LeaderProfile(), ownGang);
		setCost(120);
	}
}
