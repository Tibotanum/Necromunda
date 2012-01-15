package weapons;

import necromunda.Necromunda;
import necromunda.Utils;


public class StubGun extends RangeCombatWeapon {
	public StubGun() {
		setName("Stub Gun");
		setWeaponType(WeaponType.PISTOL);
		setCost(10);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new StubGunAmmunition());
		getAmmunitions().add(new DumDumAmmunition());
	}
	
	private class StubGunAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7439431181537876014L;

		public StubGunAmmunition() {
			setName("Regular Bullets");
			setStrength(3);
			setDamage(1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(8);
			setRangeLongLowerBound(8);
			setRangeLongUpperBound(16);
			setHitRollModificationShort(0);
			setHitRollModificationLong(-1);
			setAmmoRoll(4);
			setCost(0);
		}
	}
	
	private class DumDumAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9209998110675546504L;

		public DumDumAmmunition() {
			setName("Dum-dum Bullets");
			setStrength(4);
			setDamage(1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(8);
			setRangeLongLowerBound(8);
			setRangeLongUpperBound(16);
			setHitRollModificationShort(0);
			setHitRollModificationLong(-1);
			setAmmoRoll(4);
			setCost(5);
		}
		
		@Override
		public void sustainMalfunction() {
			int ammoRoll = Utils.rollD6();
			
			if (ammoRoll < getAmmoRoll()) {
				setBroken(true);
				explode();
			}
		}
	}
}
