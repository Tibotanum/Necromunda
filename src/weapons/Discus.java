package weapons;

import ammunitions.DiscusAmmunition;

public class Discus extends RangeCombatWeapon {
	public Discus() {
		setName("Discus");
		setRangeCombatWeaponType(RangeCombatWeaponType.SCALY);
		setCost(6);
		
		getAmmunitions().add(new DiscusAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
