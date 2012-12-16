package weapons;

import ammunitions.*;
import necromunda.*;

public class HeavyBolter extends RangeCombatWeapon {
	public HeavyBolter() {
		setName("Heavy Bolter");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setCost(180);
		
		setMoveOrFire(true);
		
		getAmmunitions().add(new HeavyBolterAmmunition(this));
		getAmmunitions().add(new HeavyBolterSustainedFireAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
