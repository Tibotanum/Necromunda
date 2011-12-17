package weapons;

import necromunda.Necromunda;

public class GrenadesAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2235011795921074799L;
	
	@Override
	public int getEffectiveScatterDistance(float shotDistance, int scatterDistance) {
		scatterDistance = scatterDistance / 2;
		return scatterDistance;
	}

	@Override
	public void sustainMalfunction() {
		getWeapon().setBroken(true);
		Necromunda.appendToStatusMessage(String.format("Your %s have run out.", getWeapon().getName()));
	}
}
