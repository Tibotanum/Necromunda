package ammunitions;

import java.awt.Color;

import weapons.RangeCombatWeapon;

import necromunda.Utils;

public class GrenadeLauncherPlasmaGrenadesAmmunition extends GrenadesAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1811255775428471282L;

	public GrenadeLauncherPlasmaGrenadesAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Plasma Grenades");
		setStrength(5);
		setDamage(1);
		setArmorSaveModification(-2);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(60);
		setHitRollModificationShort(0);
		setHitRollModificationLong(-1);
		setAmmoRoll(0);
		setCost(41);
		
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