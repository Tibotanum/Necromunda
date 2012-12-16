package ammunitions;

import weapons.RangeCombatWeapon;

public class GrenadeLauncherFragGrenadesAmmunition extends GrenadesAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2843206583776734477L;

	public GrenadeLauncherFragGrenadesAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Frag Grenades");
		setStrength(3);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(60);
		setHitRollModificationShort(0);
		setHitRollModificationLong(-1);
		setAmmoRoll(0);
		setCost(30);
		
		setTemplated(true);
		setTemplateRadius(2.0f);
	}
}