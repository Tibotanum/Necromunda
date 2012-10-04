package weapons;

import necromunda.*;

public class Autocannon extends RangeCombatWeapon {
	public Autocannon() {
		setName("Autocannon");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setCost(300);
		
		setMoveOrFire(true);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new AutocannonAmmunition());
		getAmmunitions().add(new AutocannonSustainedFireAmmunition());		
	}

	private class AutocannonAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5192254677571040060L;

		public AutocannonAmmunition() {
			setName("Single Shot");
			setStrength(8);
			setDamage(0);
			setArmorSaveModification(-3);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(72);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
		}

		@Override
		public int getDamage() {
			int damage = Utils.rollD6();

			return damage;
		}
		
		@Override
		public String getDamageText() {
			return "D6";
		}
	}
	
	private class AutocannonSustainedFireAmmunition extends AutocannonAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1319239769163018884L;

		public AutocannonSustainedFireAmmunition() {
			setName("Sustained Fire");
			setShotHandler(new SustainedFireShotHandler(1, new StandardShotHandler(null)));
		}
	}
}
