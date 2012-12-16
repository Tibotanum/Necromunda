package weapons;

import ammunitions.*;


public class Shotgun extends RangeCombatWeapon {
	public Shotgun() {
		setName("Shotgun");
		setRangeCombatWeaponType(RangeCombatWeaponType.BASIC);
		setCost(10);
		
		getAmmunitions().add(new SolidSlugAmmuition(this));
		getAmmunitions().add(new ScatterShotAmmunition(this));
		getAmmunitions().add(new ManStopperAmmunition(this));
		getAmmunitions().add(new HotShotAmmunition(this));
		getAmmunitions().add(new BoltAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
