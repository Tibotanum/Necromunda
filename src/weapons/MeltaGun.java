package weapons;

import necromunda.Utils;

public class MeltaGun extends RangeCombatWeapon {
	public MeltaGun() {
		setName("Melta-Gun");
		setWeaponType(WeaponType.SPECIAL);
		setCost(95);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new MeltaGunAmmunition());
	}

	private class MeltaGunAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8876412069246242754L;

		public MeltaGunAmmunition() {
			setStrength(8);
			setDamage(0);
			setArmorSaveModification(-4);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(6);
			setRangeLongLowerBound(6);
			setRangeLongUpperBound(12);
			setHitRollModificationShort(1);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
		}
		
		@Override
		public String getDamageText() {
			return "D6";
		}
		
		@Override
		public int getDamage() {
			int damage = Utils.rollD6();

			return damage;
		}
	}
}
