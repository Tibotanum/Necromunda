package ammunitions;

import java.awt.Color;

import weapons.RangeCombatWeapon;

import necromunda.Utils;

public class HeavyPlasmaGunLowEnergyAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2012713264749606092L;

	public HeavyPlasmaGunLowEnergyAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Low Energy");
		setStrength(7);
		setDamage(0);
		setArmorSaveModification(-2);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(20);
		setRangeLongLowerBound(20);
		setRangeLongUpperBound(40);
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
	public String getDamageText() {
		return "D3";
	}
	
	@Override
	public int getDamage() {
		int damage = Utils.rollD(3);

		return damage;
	}
}