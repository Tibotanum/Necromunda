package weapons;

import necromunda.Fighter;
import necromunda.Necromunda;

public class NeedleRifle extends NeedlePistol {
	public NeedleRifle() {
		setName("Needle Rifle");
		setRangeCombatWeaponType(RangeCombatWeaponType.SPECIAL);
		setCost(242);
		
		getAmmunitions().add(new NeedleRifleAmmunition());
		
		setCurrentAmmunition(getAmmunitions().get(0));
		
		setAmmunitionProperties();
	}
	
	private class NeedleRifleAmmunition extends NeedlePistolAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2763606280203480569L;

		public NeedleRifleAmmunition() {
			setStrength(3);
			setDamage(0);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(16);
			setRangeLongLowerBound(16);
			setRangeLongUpperBound(32);
			setHitRollModificationShort(1);
			setHitRollModificationLong(0);
			setAmmoRoll(6);
			setCost(0);
		}
	}
}
