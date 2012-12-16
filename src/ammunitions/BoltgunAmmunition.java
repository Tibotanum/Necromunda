package ammunitions;

import weapons.RangeCombatWeapon;

public class BoltgunAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7952193648773110706L;

	public BoltgunAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(12);
		setRangeLongLowerBound(12);
		setRangeLongUpperBound(24);
		setHitRollModificationShort(1);
		setHitRollModificationLong(0);
		setAmmoRoll(6);
		setCost(0);
	}
}