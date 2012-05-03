package weapons;

import java.io.Serializable;

import necromunda.Necromunda;

public class PlasmaPistol extends PlasmaWeapon {
	public PlasmaPistol() {
		setName("Plasma Pistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(25);
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
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(8);
			setRangeLongLowerBound(8);
			setRangeLongUpperBound(16);
			setHitRollModificationShort(2);
			setHitRollModificationLong(-1);
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
			setStrength(6);
			setDamage(1);
			setArmorSaveModification(-3);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(8);
			setRangeLongLowerBound(8);
			setRangeLongUpperBound(16);
			setHitRollModificationShort(2);
			setHitRollModificationLong(-1);
			setAmmoRoll(4);
			setCost(0);
		}
		
		@Override
		public void trigger() {
			turnCounter = 2;
			setEnabled(false);
			Necromunda.appendToStatusMessage(String.format("Your %s cannot be fired for one turn.", getWeapon().getName()));
		}
	}
}
