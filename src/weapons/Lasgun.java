package weapons;

import ammunitions.LasgunAmmunition;


public class Lasgun extends RangeCombatWeapon {
	public Lasgun() {
		setName("Lasgun");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setCost(25);
		
		getAmmunitions().add(new LasgunAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
