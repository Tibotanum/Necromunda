package weapons;


public class Laspistol extends RangeCombatWeapon {
	public Laspistol() {
		setName("Laspistol");
		setWeaponType(WeaponType.PISTOL);
		setCost(15);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new LaspistolAmmunition());
	}
	
	private class LaspistolAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7842188729750916878L;

		public LaspistolAmmunition() {
			setStrength(3);
			setDamage(1);
			setArmorSaveModification(0);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(8);
			setRangeLongLowerBound(8);
			setRangeLongUpperBound(16);
			setHitRollModificationShort(2);
			setHitRollModificationLong(0);
			setAmmoRoll(2);
			setCost(0);
		}
	}
}
