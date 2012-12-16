package ammunitions;

import java.awt.Color;

import weapons.*;

import necromunda.*;

public class HeavyPlasmaGunHighEnergyAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2963564536087989613L;

	public HeavyPlasmaGunHighEnergyAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("High Energy");
		setStrength(10);
		setDamage(0);
		setArmorSaveModification(-6);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(72);
		setHitRollModificationShort(0);
		setHitRollModificationLong(0);
		setAmmoRoll(4);
		setCost(0);
		
		setTemplated(true);
		setTemplateRadius(1.5f);
		
		Color color = new Color(Color.cyan.getRed() / 255f, Color.cyan.getGreen() / 255f, Color.cyan.getBlue() / 255f, 0.5f);
		setTemplateColor(color);
	}
	
	@Override
	public void trigger() {
		getWeapon().setTurnCounter(2);
		getWeapon().setEnabled(false);
		Necromunda.appendToStatusMessage(String.format("Your %s cannot be fired for one turn.", getWeapon().getName()));
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
}