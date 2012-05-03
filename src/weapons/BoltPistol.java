package weapons;


public class BoltPistol extends RangeCombatWeapon {
	public BoltPistol() {
		setName("Bolt Pistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(20);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new BoltPistolAmmunition());		
	}
	
	private class BoltPistolAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 58048862231548974L;

		public BoltPistolAmmunition() {
			setStrength(4);
			setDamage(1);
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
	}
}
