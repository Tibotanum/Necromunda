package necromunda;

public class BountyHunter extends Fighter {	
	public BountyHunter(String name, Gang ownGang) {
		super(name, new BountyHunterProfile(), ownGang);
		setCost(175);
	}
}