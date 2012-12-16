package weapons;

import ammunitions.NeedlePistolAmmunition;
import necromunda.Fighter.State;

public class NeedlePistol extends RangeCombatWeapon {
	public NeedlePistol() {
		setName("Needle Pistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(112);
		
		getAmmunitions().add(new NeedlePistolAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
