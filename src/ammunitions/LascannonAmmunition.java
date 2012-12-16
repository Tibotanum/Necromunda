package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.Utils;

public class LascannonAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2450909993162563600L;

	public LascannonAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setStrength(9);
		setDamage(0);
		setArmorSaveModification(-6);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(60);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setCost(0);
	}
	
	@Override
	public int getDamage() {
		int damage = Utils.rollD6() + Utils.rollD6();

		return damage;
	}

	@Override
	public String getDamageText() {
		return "2D6";
	}
}