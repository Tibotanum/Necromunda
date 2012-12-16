package ammunitions;

import weapons.*;
import necromunda.Necromunda;

public class PlasmaPistolHighEnergyAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2963564536087989613L;

	public PlasmaPistolHighEnergyAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("High Energy");
		setStrength(6);
		setDamage(1);
		setArmorSaveModification(-3);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(8);
		setRangeLongLowerBound(8);
		setRangeLongUpperBound(16);
		setHitRollModificationShort(2);
		setHitRollModificationLong(-1);
		setAmmoRoll(4);
		setCost(0);
	}
	
	@Override
	public void trigger() {
		getWeapon().setTurnCounter(2);
		getWeapon().setEnabled(false);
		Necromunda.appendToStatusMessage(String.format("Your %s cannot be fired for one turn.", getWeapon().getName()));
	}
}