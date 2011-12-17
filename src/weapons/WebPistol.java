package weapons;

import necromunda.Fighter;
import necromunda.Necromunda;

public class WebPistol extends RangeCombatWeapon {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1933132080175427580L;

	public WebPistol() {
		setName("Web Pistol");
		setWeaponType(WeaponType.PISTOL);
		setCost(134);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new WebPistolAmmunition());
	}
	
	public static void dealWebDamageTo(Fighter fighter) {
		final int inflictedWounds = 1;
		int remainingWounds = fighter.getRemainingWounds();
		remainingWounds -= inflictedWounds;
		
		Necromunda.appendToStatusMessage(String.format("%s wounds were inflicted by the web.", inflictedWounds));
		
		if (remainingWounds < 1) {
			for (int i = 0 ; i < (remainingWounds * -1) + 1; i++) {
				fighter.injure(false);
			}
			
			remainingWounds = 1;
		}
		
		fighter.setRemainingWounds(remainingWounds);
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
		public void dealDamageTo(Fighter... fighters) {
			for (Fighter fighter : fighters) {
				fighter.setWebbed(true);
				Necromunda.appendToStatusMessage(String.format("%s has been webbed.", fighter));
			}
		}
		
		@Override
		public void explode() {
			getOwner().setWebbed(true);
			Necromunda.appendToStatusMessage(String.format("%s has been webbed by his own weapon.", getOwner()));
		}
	}
}
