package ammunitions;

import weapons.*;
import necromunda.*;

public class SuperKrakMissileAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 414280688889729893L;

	public SuperKrakMissileAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Super Krak Missiles");
		setStrength(8);
		setDamage(1);
		setArmorSaveModification(-6);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(72);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(0);
		setCost(115);
		
		setScattering(true);
	}
	
	@Override
	public String getDamageText() {
		return "D6";
	}
	
	@Override
	public int getDamage() {
		int damage = Utils.rollD6();

		return damage;
	}
	
	@Override
	public void sustainMalfunction() {
		getWeapon().setBroken(true);
		Necromunda.appendToStatusMessage(String.format("Your %s have run out and your %s cannot be fired anymore.", getName(), getWeapon().getName()));
	}
}