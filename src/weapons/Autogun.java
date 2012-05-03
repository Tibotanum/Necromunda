package weapons;

public class Autogun extends RangeCombatWeapon {
	public Autogun() {
		setName("Autogun");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setCost(20);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new AutogunAmmunition());		
	}
	
	private class AutogunAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2513570087094030686L;

		public AutogunAmmunition() {
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
