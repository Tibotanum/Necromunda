package ammunitions;

import weapons.RangeCombatWeapon;

public class SolidSlugAmmuition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9053836337878232673L;

	public SolidSlugAmmuition(RangeCombatWeapon weapon) {
		super(weapon);
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