package ammunitions;

import weapons.*;
import necromunda.Necromunda;

public class FragMissileAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2843206583776734477L;

	public FragMissileAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Frag Missiles");
		setStrength(4);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(72);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(0);
		setCost(35);
		
		setTemplated(true);
		setTemplateRadius(1.5f);
		
		setScattering(true);
	}
	
	@Override
	public void sustainMalfunction() {
		getWeapon().setBroken(true);
		Necromunda.appendToStatusMessage(String.format("Your %s have run out and your %s cannot be fired anymore.", getName(), getWeapon().getName()));
	}
}