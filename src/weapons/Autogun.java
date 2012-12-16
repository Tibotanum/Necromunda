package weapons;

import ammunitions.Ammunition;

public class Autogun extends RangeCombatWeapon {
	public Autogun() {
		setName("Autogun");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setCost(20);
		
		getAmmunitions().add(new AutogunAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
	
	private class AutogunAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2513570087094030686L;

		public AutogunAmmunition(RangeCombatWeapon weapon) {
			super(weapon);
			setStrength(3);
			setDamage(1);
			setArmorSaveModification(0);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(12);
			setRangeLongLowerBound(12);
			setRangeLongUpperBound(24);
			setHitRollModificationShort(1);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
		}
	}
}
