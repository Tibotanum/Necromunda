package ammunitions;

import weapons.RangeCombatWeapon;

public class ManStopperAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4922734622801938207L;

	public ManStopperAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Man Stopper");
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-2);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(4);
		setRangeLongLowerBound(4);
		setRangeLongUpperBound(18);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setCost(5);
	}
}