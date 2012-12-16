package ammunitions;

import weapons.*;
import necromunda.Utils;

public class DumDumAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9209998110675546504L;

	public DumDumAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Dum-dum Bullets");
		setStrength(4);
		setDamage(1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(8);
		setRangeLongLowerBound(8);
		setRangeLongUpperBound(16);
		setHitRollModificationShort(0);
		setHitRollModificationLong(-1);
		setAmmoRoll(4);
		setCost(5);
	}
	
	@Override
	public void sustainMalfunction() {
		int ammoRoll = Utils.rollD6();
		
		if (ammoRoll < getAmmoRoll()) {
			getWeapon().setBroken(true);
			explode();
		}
	}
}