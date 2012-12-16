package ammunitions;

import weapons.*;
import necromunda.Utils;

public class AutocannonAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5192254677571040060L;

	public AutocannonAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Single Shot");
		setStrength(8);
		setDamage(0);
		setArmorSaveModification(-3);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(72);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setCost(0);
	}

	@Override
	public int getDamage() {
		int damage = Utils.rollD6();

		return damage;
	}
	
	@Override
	public String getDamageText() {
		return "D6";
	}
}