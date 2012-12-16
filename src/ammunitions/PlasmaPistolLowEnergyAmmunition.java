package ammunitions;

import weapons.RangeCombatWeapon;

public class PlasmaPistolLowEnergyAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2012713264749606092L;

	public PlasmaPistolLowEnergyAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Low Energy");
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(8);
		setRangeLongLowerBound(8);
		setRangeLongUpperBound(16);
		setHitRollModificationShort(2);
		setHitRollModificationLong(-1);
		setAmmoRoll(4);
		setCost(0);
	}
}