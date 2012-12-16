package ammunitions;

import weapons.RangeCombatWeapon;

public class LasgunAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 865951843364580956L;

	public LasgunAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(3);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(12);
		setRangeLongLowerBound(12);
		setRangeLongUpperBound(24);
		setHitRollModificationShort(1);
		setHitRollModificationLong(0);
		setAmmoRoll(2);
		setCost(0);
	}
}