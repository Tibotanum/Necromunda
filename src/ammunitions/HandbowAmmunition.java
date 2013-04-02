package ammunitions;

import weapons.RangeCombatWeapon;

public class HandbowAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5788816572713667106L;

	public HandbowAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(8);
		setRangeLongLowerBound(8);
		setRangeLongUpperBound(16);
		setHitRollModificationShort(0);
		setHitRollModificationLong(-1);
		setAmmoRoll(4);
		setCost(0);
	}
}
