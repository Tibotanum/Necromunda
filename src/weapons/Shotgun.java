package weapons;

import necromunda.Necromunda;

public class Shotgun extends RangeCombatWeapon {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3107206319409527991L;

	public Shotgun() {
		setName("Shotgun");
		setWeaponType(WeaponType.BASIC);
		setCost(10);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new SolidSlugAmmuition());
		getAmmunitions().add(new ScatterShotAmmunition());
		getAmmunitions().add(new ManStopperAmmunition());
		getAmmunitions().add(new HotShotAmmunition());
		getAmmunitions().add(new BoltAmmunition());
	}
	
	private class SolidSlugAmmuition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9053836337878232673L;

		public SolidSlugAmmuition() {
			setName("Solid Slug");
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(0);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(4);
			setRangeLongLowerBound(4);
			setRangeLongUpperBound(18);
			setHitRollModificationShort(0);
			setHitRollModificationLong(-1);
			setAmmoRoll(4);
			setCost(0);
		}
	}
	
	private class ScatterShotAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7900951292694830944L;

		public ScatterShotAmmunition() {
			setName("Scatter Shot");
			setStrength(3);
			setDamage(1);
			setArmorSaveModification(0);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(4);
			setRangeLongLowerBound(4);
			setRangeLongUpperBound(18);
			setHitRollModificationShort(1);
			setHitRollModificationLong(-1);
			setAmmoRoll(4);
			setCost(0);
			
			setAdditionalTargetRange(Necromunda.STRAY_SHOT_RADIUS);
		}
	}
	
	private class ManStopperAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4922734622801938207L;

		public ManStopperAmmunition() {
			setName("Man Stopper");
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(-2);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(4);
			setRangeLongLowerBound(4);
			setRangeLongUpperBound(18);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(5);
		}
	}
	
	private class HotShotAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4914324649224569739L;

		public HotShotAmmunition() {
			setName("Hot Shot");
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(0);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(4);
			setRangeLongLowerBound(4);
			setRangeLongUpperBound(18);
			setHitRollModificationShort(0);
			setHitRollModificationLong(-1);
			setAmmoRoll(6);
			setCost(5);
		}
	}
	
	private class BoltAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2992022096944437794L;

		public BoltAmmunition() {
			setName("Bolt");
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(4);
			setRangeLongLowerBound(4);
			setRangeLongUpperBound(24);
			setHitRollModificationShort(1);
			setHitRollModificationLong(0);
			setAmmoRoll(6);
			setCost(15);
		}
	}
}
