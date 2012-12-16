package ammunitions;

import weapons.RangeCombatWeapon;

public class NeedleRifleAmmunition extends NeedlePistolAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2763606280203480569L;

	public NeedleRifleAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(3);
		setDamage(0);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(16);
		setRangeLongLowerBound(16);
		setRangeLongUpperBound(32);
		setHitRollModificationShort(1);
		setHitRollModificationLong(0);
		setAmmoRoll(6);
		setCost(0);
	}
}