package weapons;

import necromunda.*;

public class PlasmaGun extends PlasmaWeapon {
	public PlasmaGun() {
		setName("Plasma Gun");
		setRangeCombatWeaponType(RangeCombatWeaponType.SPECIAL);
		setCost(70);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new LowEnergyAmmunition());
		getAmmunitions().add(new HighEnergyAmmunition());
	}
	
	private class LowEnergyAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2012713264749606092L;

		public LowEnergyAmmunition() {
			setName("Low Energy");
			setStrength(5);
			setDamage(1);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(6);
			setRangeLongLowerBound(6);
			setRangeLongUpperBound(16);
			setHitRollModificationShort(1);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
		}
	}
	
	private class HighEnergyAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2963564536087989613L;

		public HighEnergyAmmunition() {
			setName("High Energy");
			setStrength(7);
			setDamage(1);
			setArmorSaveModification(-2);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(6);
			setRangeLongLowerBound(6);
			setRangeLongUpperBound(24);
			setHitRollModificationShort(1);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
			setShotHandler(new SustainedFireShotHandler(new StandardShotHandler(null)));
		}
		
		@Override
		public void trigger() {
			turnCounter = 2;
			setEnabled(false);
			Necromunda.appendToStatusMessage(String.format("Your %s cannot be fired for one turn.", getWeapon().getName()));
		}
	}
}
