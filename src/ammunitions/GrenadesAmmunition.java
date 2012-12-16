package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.Necromunda;

public class GrenadesAmmunition extends Ammunition {
	public GrenadesAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setScattering(true);
	}
	
	@Override
	public int getEffectiveScatterDistance(float shotDistance, int scatterDistance) {
		scatterDistance = scatterDistance / 2;
		return scatterDistance;
	}

	@Override
	public void sustainMalfunction() {
		getWeapon().setBroken(true);
		Necromunda.appendToStatusMessage(String.format("Your %s have run out.", getName()));
	}
}
