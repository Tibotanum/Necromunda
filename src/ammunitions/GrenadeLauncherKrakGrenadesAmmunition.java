package ammunitions;

import weapons.*;
import necromunda.Utils;

public class GrenadeLauncherKrakGrenadesAmmunition extends GrenadesAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7717425847670438865L;

	public GrenadeLauncherKrakGrenadesAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Krak Grenades");
		setStrength(6);
		setDamage(0);
		setArmorSaveModification(-3);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(60);
		setHitRollModificationShort(0);
		setHitRollModificationLong(-2);
		setAmmoRoll(0);
		setCost(50);
		
		setTemplated(true);
		setTemplateRadius(GrenadeLauncher.MINIMUM_TEMPLATE_SIZE);
	}

	@Override
	public int getDamage() {
		int damage = Utils.rollD6();

		return damage;
	}
}