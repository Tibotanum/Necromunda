package weapons;

import ammunitions.*;
import necromunda.Necromunda;


public class StubGun extends RangeCombatWeapon {
	public StubGun() {
		setName("Stub Gun");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(10);
		
		getAmmunitions().add(new StubGunAmmunition(this));
		getAmmunitions().add(new DumDumAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
