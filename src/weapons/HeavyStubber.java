package weapons;

import ammunitions.*;
import necromunda.*;

public class HeavyStubber extends RangeCombatWeapon {
	public HeavyStubber() {
		setName("Heavy Stubber");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setCost(120);
		
		setMoveOrFire(true);
		
		getAmmunitions().add(new HeavyStubberAmmunition(this));
		getAmmunitions().add(new HeavyStubberSustainedFireAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
