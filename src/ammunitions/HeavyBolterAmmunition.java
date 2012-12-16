package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.Utils;

public class HeavyBolterAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4144430805390368568L;

	public HeavyBolterAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Single Shot");
		setStrength(5);
		setDamage(0);
		setArmorSaveModification(-2);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(40);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(6);
		setCost(0);
	}
	
	@Override
	public String getDamageText() {
		return "D3";
	}
	
	@Override
	public int getDamage() {
		int damage = Utils.rollD(3);

		return damage;
	}
}