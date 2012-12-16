package weapons;

import java.io.Serializable;

import ammunitions.*;



public class PlasmaPistol extends RangeCombatWeapon {
	public PlasmaPistol() {
		setName("Plasma Pistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(25);
		
		getAmmunitions().add(new PlasmaPistolLowEnergyAmmunition(this));
		getAmmunitions().add(new PlasmaPistolHighEnergyAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
