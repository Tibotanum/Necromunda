package ammunitions;

import weapons.RangeCombatWeapon;

public class PlasmaGunLowEnergyAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2012713264749606092L;

	public PlasmaGunLowEnergyAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Low Energy");
		setStrength(5);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(6);
		setRangeLongLowerBound(6);
		setRangeLongUpperBound(16);
		setHitRollModificationShort(1);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setCost(0);
	}
}