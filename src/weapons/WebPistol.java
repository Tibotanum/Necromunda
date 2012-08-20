package weapons;

import necromunda.Fighter;
import necromunda.Necromunda;

public class WebPistol extends RangeCombatWeapon {
	public WebPistol() {
		setName("Web Pistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(134);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new WebPistolAmmunition());
	}
	
	public static void dealWebDamage(Fighter fighter) {
		final int inflictedWounds = 1;
		int remainingWounds = fighter.getProfile().getCurrentWounds();
		remainingWounds -= inflictedWounds;
		
		Necromunda.appendToStatusMessage(String.format("%s wounds were inflicted by the web.", inflictedWounds));
		
		if (remainingWounds < 1) {
			for (int i = 0 ; i < (remainingWounds * -1) + 1; i++) {
				fighter.injure(false);
			}
			
			remainingWounds = 1;
		}
		
		fighter.getProfile().setCurrentWounds(remainingWounds);
	}

	private class WebPistolAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8735765701503759368L;

		public WebPistolAmmunition() {
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
			getOwner().setWebbed(true);
			Necromunda.appendToStatusMessage(String.format("%s has been webbed by his own weapon.", getOwner()));
		}
	}
}
