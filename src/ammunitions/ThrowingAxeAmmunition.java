package ammunitions;

import weapons.RangeCombatWeapon;

public class ThrowingAxeAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6541390889041081861L;

	public ThrowingAxeAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(5);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(6);
		setRangeLongLowerBound(6);
		setRangeLongUpperBound(12);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(6);
		setCost(0);
	}
}
