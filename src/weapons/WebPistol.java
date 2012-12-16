package weapons;

import ammunitions.WebPistolAmmunition;
import necromunda.Fighter;
import necromunda.Necromunda;

public class WebPistol extends RangeCombatWeapon {
	public WebPistol() {
		setName("Web Pistol");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(134);
		
		getAmmunitions().add(new WebPistolAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
	
	public static void dealWebDamage(Fighter fighter) {
		final int inflictedWounds = 1;
		int remainingWounds = fighter.getProfile().getCurrentWounds();
		remainingWounds -= inflictedWounds;
		
		Necromunda.appendToStatusMessage(String.format("%s wounds were inflicted by the web.", inflictedWounds));
		
		if (remainingWounds < 1) {
			for (int i = 0 ; i < (remainingWounds * -1) + 1; i++) {
				fighter.injure(false);
			}
			
			remainingWounds = 1;
		}
		
		fighter.getProfile().setCurrentWounds(remainingWounds);
	}
}
