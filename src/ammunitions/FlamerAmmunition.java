package ammunitions;

import weapons.RangeCombatWeapon;

public class FlamerAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6161133697747419382L;
	
	private final String outOfFuelMessage;

	public FlamerAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-2);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(0);
		setRangeLongLowerBound(0);
		setRangeLongUpperBound(0);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setTargeted(false);
		setCost(0);
		
		setTemplated(true);
		setTemplateRadius(1.25f);
		setTemplateLength(7.25f);
		
		setTemplateAttached(true);
		
		outOfFuelMessage = String.format("Your %s ran out of fuel.", getWeapon());
	}
	
	@Override
	public void trigger() {
		sustainMalfunction();
	}

	public String getOutOfFuelMessage() {
		return outOfFuelMessage;
	}
}