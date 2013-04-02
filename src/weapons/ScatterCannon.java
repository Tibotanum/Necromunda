package weapons;

import ammunitions.ScatterCannonAmmunition;

public class ScatterCannon extends RangeCombatWeapon {
	public ScatterCannon() {
		setName("Scatter Cannon");
		setRangeCombatWeaponType(RangeCombatWeaponType.SCALY);
		setCost(80);
		
		getAmmunitions().add(new ScatterCannonAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}