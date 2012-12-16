package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.Necromunda;

public class ScatterShotAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7900951292694830944L;

	public ScatterShotAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
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