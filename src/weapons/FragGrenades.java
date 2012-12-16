package weapons;

import ammunitions.SingleFragGrenadesAmmunition;
import necromunda.Necromunda;

public class FragGrenades extends Grenades {
	public FragGrenades() {
		setName("Frag Grenades");
		setCost(30);
		
		getAmmunitions().add(new SingleFragGrenadesAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
