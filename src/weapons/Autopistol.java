package weapons;


public class Autopistol extends RangeCombatWeapon {
	public Autopistol() {
		setName("Autopistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(15);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new AutopistolAmmunition());		
	}

	private class AutopistolAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2471970416255622834L;

		public AutopistolAmmunition() {
			setStrength(3);
			setDamage(1);
			setArmorSaveModification(0);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(8);
			setRangeLongLowerBound(8);
			setRangeLongUpperBound(16);
			setHitRollModificationShort(2);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
		}
	}
}
