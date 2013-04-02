package weapons;

import ammunitions.ThrowingAxeAmmunition;

public class ThrowingAxe extends RangeCombatWeapon {
	public ThrowingAxe() {
		setName("Throwing Axe");
		setRangeCombatWeaponType(RangeCombatWeaponType.SCALY);
		setCost(6);
		
		getAmmunitions().add(new ThrowingAxeAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
