package weapons;

import necromunda.Utils;

public class KrakGrenades extends Grenades {
	public KrakGrenades() {
		setName("Krak Grenades");
		setCost(50);
	}

	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new KrakGrenadesAmmunition());	
	}
	
	private class KrakGrenadesAmmunition extends GrenadesAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4540864777900539093L;

		public KrakGrenadesAmmunition() {
			setName("Krak Grenades");
			setStrength(6);
			setDamage(0);
			setArmorSaveModification(-3);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(0);
			setRangeLongLowerBound(0);
			setRangeLongUpperBound(0);
			setHitRollModificationShort(-1);
			setHitRollModificationLong(-1);
			setAmmoRoll(0);
			setCost(0);
			
			setTemplated(true);
			setTemplateRadius(MINIMUM_TEMPLATE_SIZE);
		}

		@Override
		public int getDamage() {
			int damage = Utils.rollD6();

			return damage;
		}
	}
}
