package ammunitions;

import weapons.RangeCombatWeapon;

public class SingleFragGrenadesAmmunition extends GrenadesAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2843206583776734477L;

	public SingleFragGrenadesAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Frag Grenades");
		setStrength(3);
		setDamage(1);
		setArmorSaveModification(-1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(0);
		setRangeLongLowerBound(0);
		setRangeLongUpperBound(0);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(0);
		setCost(0);
		
		setTemplated(true);
		setTemplateRadius(2.0f);
	}
}