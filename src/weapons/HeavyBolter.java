package weapons;

import necromunda.*;

public class HeavyBolter extends RangeCombatWeapon {
	public HeavyBolter() {
		setName("Heavy Bolter");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setCost(180);
		
		setMoveOrFire(true);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new HeavyBolterAmmunition());
		getAmmunitions().add(new HeavyBolterSustainedFireAmmunition());		
	}

	private class HeavyBolterAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4144430805390368568L;

		public HeavyBolterAmmunition() {
			setName("Single Shot");
			setStrength(5);
			setDamage(0);
			setArmorSaveModification(-2);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(40);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(6);
			setCost(0);
		}
		
		@Override
		public String getDamageText() {
			return "D3";
		}
		
		@Override
		public int getDamage() {
			int damage = Utils.rollD(3);

			return damage;
		}
	}
	
	private class HeavyBolterSustainedFireAmmunition extends HeavyBolterAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1530560520189183631L;
		
		public HeavyBolterSustainedFireAmmunition() {
			setName("Sustained Fire");
			setShotHandler(new SustainedFireShotHandler(2, new StandardShotHandler(null)));
		}
	}
}
