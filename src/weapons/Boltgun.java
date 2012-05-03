package weapons;

public class Boltgun extends RangeCombatWeapon {
	public Boltgun() {
		setName("Boltgun");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setCost(35);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new BoltgunAmmunition());		
	}
	
	private class BoltgunAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7952193648773110706L;

		public BoltgunAmmunition() {
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(12);
			setRangeLongLowerBound(12);
			setRangeLongUpperBound(24);
			setHitRollModificationShort(1);
			setHitRollModificationLong(0);
			setAmmoRoll(6);
			setCost(0);
		}
	}
}
