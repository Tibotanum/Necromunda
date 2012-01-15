package weapons;

import java.awt.Color;

import necromunda.Necromunda;
import necromunda.Utils;

public class PlasmaGrenades extends Grenades {
	public PlasmaGrenades() {
		setName("Plasma Grenades");
		setCost(41);
	}

	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new PlasmaGrenadesAmmunition());
	}
	
	private class PlasmaGrenadesAmmunition extends GrenadesAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2663602518106487058L;

		public PlasmaGrenadesAmmunition() {
			setStrength(5);
			setDamage(1);
			setArmorSaveModification(-2);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(0);
			setRangeLongLowerBound(0);
			setRangeLongUpperBound(0);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(0);
			setCost(0);
			
			setTemplated(true);
			setTemplateRadius(1.5f);
			setTemplatePersistent(true);
			
			Color color = new Color(Color.cyan.getRed() / 255f, Color.cyan.getGreen() / 255f, Color.cyan.getBlue() / 255f, 0.5f);
			setTemplateColor(color);
		}

		@Override
		public float getDriftDistance() {
			return Utils.rollD6();
		}

		@Override
		public float getDriftAngle() {
			return Utils.getRandomAngle();
		}

		@Override
		public boolean isTemplateToBeRemoved() {
			int roll = Utils.rollD6();
			
			if ((roll >= 1) && (roll <= 3)) {
				return true;
			}
			else {
				return false;
			}
		}

		@Override
		public boolean isTemplateMoving() {
			int roll = Utils.rollD6();
			
			if (roll == 6) {
				return true;
			}
			else {
				return false;
			}
		}
	}

}
