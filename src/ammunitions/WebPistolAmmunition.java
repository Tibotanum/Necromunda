package ammunitions;

import weapons.*;
import necromunda.*;

public class WebPistolAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8735765701503759368L;

	public WebPistolAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(0);
		setDamage(0);
		setArmorSaveModification(0);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(4);
		setRangeLongLowerBound(4);
		setRangeLongUpperBound(8);
		setHitRollModificationShort(0);
		setHitRollModificationLong(-1);
		setAmmoRoll(6);
		setCost(0);
	}
	
	@Override
	public boolean dealDamageTo(Fighter... fighters) {
		for (Fighter fighter : fighters) {
			fighter.setWebbed(true);
			Necromunda.appendToStatusMessage(String.format("%s has been webbed.", fighter));
		}
		
		return true;
	}
	
	@Override
	public void explode() {
		getWeapon().getOwner().setWebbed(true);
		Necromunda.appendToStatusMessage(String.format("%s has been webbed by his own weapon.", getWeapon().getOwner()));
	}
}