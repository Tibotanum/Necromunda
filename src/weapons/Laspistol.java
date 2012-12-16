package weapons;

import ammunitions.LaspistolAmmunition;



public class Laspistol extends RangeCombatWeapon {
	public Laspistol() {
		setName("Laspistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(15);
		
		getAmmunitions().add(new LaspistolAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
