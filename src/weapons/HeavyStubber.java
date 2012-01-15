package weapons;

import necromunda.Utils;

public class HeavyStubber extends RangeCombatWeapon {
	public HeavyStubber() {
		setName("Heavy Stubber");
		setWeaponType(WeaponType.HEAVY);
		setCost(120);
		
		setMoveOrFire(true);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new HeavyStubberAmmunition());
		getAmmunitions().add(new HeavyStubberSustainedFireAmmunition());	
	}
	
	private class HeavyStubberAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2804743574330691883L;

		public HeavyStubberAmmunition() {
			setName("Single Shot");
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(40);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
		}
	}
	
	private class HeavyStubberSustainedFireAmmunition extends HeavyStubberAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2062770147726272997L;
		
		public HeavyStubberSustainedFireAmmunition() {
			setName("Sustained Fire");
		}

		@Override
		public void resetNumberOfShots() {
			setNumberOfShots(Utils.rollD(3) + Utils.rollD(3));
		}
	}
}
