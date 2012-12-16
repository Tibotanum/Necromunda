package ammunitions;

import weapons.RangeCombatWeapon;

public class HotShotAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4914324649224569739L;

	public HotShotAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
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
		setRerollWound(true);
	}
}