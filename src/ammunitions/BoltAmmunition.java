package ammunitions;

import weapons.RangeCombatWeapon;

public class BoltAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2992022096944437794L;

	public BoltAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Bolt");
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(4);
		setRangeLongLowerBound(4);
		setRangeLongUpperBound(24);
		setHitRollModificationShort(1);
		setHitRollModificationLong(0);
		setAmmoRoll(6);
		setCost(15);
	}
}