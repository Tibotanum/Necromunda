package weapons;

public class Lasgun extends RangeCombatWeapon {
	public Lasgun() {
		setName("Lasgun");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setCost(25);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new LasgunAmmunition());		
	}
	
	private class LasgunAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 865951843364580956L;

		public LasgunAmmunition() {
			setStrength(3);
			setDamage(1);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(12);
			setRangeLongLowerBound(12);
			setRangeLongUpperBound(24);
			setHitRollModificationShort(1);
			setHitRollModificationLong(0);
			setAmmoRoll(2);
			setCost(0);
		}
	}
}
