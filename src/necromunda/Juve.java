package necromunda;

public class Juve extends Fighter {
	public Juve(String name, Gang ownGang) {
		super(name, new JuveProfile(), ownGang);
		setCost(25);
	}

	@Override
	public boolean isReliable() {
		return false;
	}
}
