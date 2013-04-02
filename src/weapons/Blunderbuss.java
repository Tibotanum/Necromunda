package weapons;

import ammunitions.BlunderbussAmmunition;

public class Blunderbuss extends RangeCombatWeapon {
	public Blunderbuss() {
		setName("Blunderbuss");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setCost(7);
		
		getAmmunitions().add(new BlunderbussAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
