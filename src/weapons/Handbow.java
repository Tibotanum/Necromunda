package weapons;

import ammunitions.HandbowAmmunition;

public class Handbow extends RangeCombatWeapon {
	public Handbow() {
		setName("Handbow");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setMoveOrFire(true);
		setCost(5);
		
		getAmmunitions().add(new HandbowAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
