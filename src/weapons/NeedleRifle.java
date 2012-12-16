package weapons;

import ammunitions.NeedleRifleAmmunition;
import necromunda.Fighter;
import necromunda.Necromunda;

public class NeedleRifle extends NeedlePistol {
	public NeedleRifle() {
		setName("Needle Rifle");
		setRangeCombatWeaponType(RangeCombatWeaponType.SPECIAL);
		setCost(242);
		
		getAmmunitions().add(new NeedleRifleAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
