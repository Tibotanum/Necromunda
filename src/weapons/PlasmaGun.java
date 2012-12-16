package weapons;

import ammunitions.*;
import necromunda.*;

public class PlasmaGun extends RangeCombatWeapon {
	public PlasmaGun() {
		setName("Plasma Gun");
		setRangeCombatWeaponType(RangeCombatWeaponType.SPECIAL);
		setCost(70);
		
		getAmmunitions().add(new PlasmaGunLowEnergyAmmunition(this));
		getAmmunitions().add(new PlasmaGunHighEnergyAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
