package weapons;

import necromunda.Fighter;
import necromunda.Necromunda;
import necromunda.Fighter.State;

public class NeedlePistol extends RangeCombatWeapon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3385621320949231800L;

	public NeedlePistol() {
		setName("Needle Pistol");
		setWeaponType(WeaponType.PISTOL);
		setCost(112);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new NeedlePistolAmmunition());
	}
	
	protected class NeedlePistolAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8774251900088135911L;

		public NeedlePistolAmmunition() {
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
		public void dealDamageTo(Fighter... fighters) {
			final int inflictedWounds = 1; 
			
			for (Fighter fighter : fighters) {
				int remainingWounds = fighter.getRemainingWounds();
				remainingWounds -= inflictedWounds;
				
				if (remainingWounds < 1) {
					for (int i = 0 ; i < (remainingWounds * -1) + 1; i++) {
						fighter.poison();
					}
					
					remainingWounds = 1;
				}
				
				fighter.setRemainingWounds(remainingWounds);
				
				Necromunda.appendToStatusMessage(String.format("%s wounded automatically. %s wounds were inflicted.", getWeapon().getName(), inflictedWounds));
			}
		}
	}
}
