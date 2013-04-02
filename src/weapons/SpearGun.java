package weapons;

import ammunitions.SpearGunAmmunition;
import necromunda.Utils;

public class SpearGun extends RangeCombatWeapon {
	public SpearGun() {
		setName("Spear Gun");
		setRangeCombatWeaponType(RangeCombatWeaponType.SCALY);
		setCost(55);
		
		getAmmunitions().add(new SpearGunAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}