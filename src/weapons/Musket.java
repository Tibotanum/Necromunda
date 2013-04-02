package weapons;

import ammunitions.MusketAmmunition;

public class Musket extends RangeCombatWeapon {
	public Musket() {
		setName("Musket");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setMoveOrFire(true);
		setCost(5);
		
		getAmmunitions().add(new MusketAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
