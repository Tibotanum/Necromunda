package weapons;

import ammunitions.MeltaGunAmmunition;


public class MeltaGun extends RangeCombatWeapon {
	public MeltaGun() {
		setName("Melta-Gun");
		setRangeCombatWeaponType(RangeCombatWeaponType.SPECIAL);
		setCost(95);
		
		getAmmunitions().add(new MeltaGunAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
