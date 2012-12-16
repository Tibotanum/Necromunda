package weapons;

import ammunitions.BoltPistolAmmunition;



public class BoltPistol extends RangeCombatWeapon {
	public BoltPistol() {
		setName("Bolt Pistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(20);
		
		getAmmunitions().add(new BoltPistolAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
