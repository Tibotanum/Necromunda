package ammunitions;

import weapons.*;
import necromunda.*;

public class PlasmaGunHighEnergyAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2963564536087989613L;

	public PlasmaGunHighEnergyAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("High Energy");
		setStrength(7);
		setDamage(1);
		setArmorSaveModification(-2);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(6);
		setRangeLongLowerBound(6);
		setRangeLongUpperBound(24);
		setHitRollModificationShort(1);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setCost(0);
		setShotHandler(new SustainedFireShotHandler(1, new StandardShotHandler(null)));
	}
	
	@Override
	public void trigger() {
		getWeapon().setTurnCounter(2);
		getWeapon().setEnabled(false);
		Necromunda.setStatusMessage(String.format("Your %s cannot be fired for one turn.", getWeapon().getName()));
	}
}