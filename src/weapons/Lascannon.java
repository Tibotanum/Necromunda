package weapons;

import ammunitions.LascannonAmmunition;


public class Lascannon extends RangeCombatWeapon {
	public Lascannon() {
		setName("Lascannon");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setCost(400);
		
		setMoveOrFire(true);
		
		getAmmunitions().add(new LascannonAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
