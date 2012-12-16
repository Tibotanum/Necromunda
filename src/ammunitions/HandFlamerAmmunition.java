package ammunitions;

import weapons.*;
import necromunda.Necromunda;

public class HandFlamerAmmunition extends FlamerAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5481499472820080535L;

	public HandFlamerAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-2);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(0);
		setRangeLongLowerBound(0);
		setRangeLongUpperBound(0);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setCost(0);
	}

	@Override
	public void trigger() {
		getWeapon().setBroken(true);
		Necromunda.appendToStatusMessage(getOutOfFuelMessage());
	}
}