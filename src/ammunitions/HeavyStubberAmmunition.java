package ammunitions;

import weapons.RangeCombatWeapon;

public class HeavyStubberAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2804743574330691883L;

	public HeavyStubberAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Single Shot");
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(40);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setCost(0);
	}
}