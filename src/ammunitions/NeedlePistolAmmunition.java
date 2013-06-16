package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.*;

public class NeedlePistolAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8774251900088135911L;

	public NeedlePistolAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(3);
		setDamage(0);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(8);
		setRangeLongLowerBound(8);
		setRangeLongUpperBound(16);
		setHitRollModificationShort(2);
		setHitRollModificationLong(0);
		setAmmoRoll(6);
		setCost(0);
	}
	
	@Override
	public boolean dealDamageTo(Fighter... fighters) {
		final int inflictedWounds = 1; 
		
		for (Fighter fighter : fighters) {
			int remainingWounds = fighter.getProfile().getCurrentWounds();
			remainingWounds -= inflictedWounds;
			
			if (remainingWounds < 1) {
				for (int i = 0 ; i < (remainingWounds * -1) + 1; i++) {
					fighter.poison();
				}
				
				remainingWounds = 1;
			}
			
			fighter.getProfile().setCurrentWounds(remainingWounds);
			
			Necromunda.setStatusMessage(String.format("%s wounded automatically. %s wounds were inflicted.", getWeapon().getName(), inflictedWounds));
		}
		
		return true;
	}
}