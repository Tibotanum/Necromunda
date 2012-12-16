package weapons;

import ammunitions.*;
import necromunda.*;

public class Autocannon extends RangeCombatWeapon {
	public Autocannon() {
		setName("Autocannon");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setCost(300);
		
		setMoveOrFire(true);
		
		getAmmunitions().add(new AutocannonAmmunition(this));
		getAmmunitions().add(new AutocannonSustainedFireAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
