package weapons;

import ammunitions.AutopistolAmmunition;



public class Autopistol extends RangeCombatWeapon {
	public Autopistol() {
		setName("Autopistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(15);
		
		getAmmunitions().add(new AutopistolAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
