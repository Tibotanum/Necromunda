package weapons;

import ammunitions.BoltgunAmmunition;


public class Boltgun extends RangeCombatWeapon {
	public Boltgun() {
		setName("Boltgun");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setCost(35);
		
		getAmmunitions().add(new BoltgunAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
