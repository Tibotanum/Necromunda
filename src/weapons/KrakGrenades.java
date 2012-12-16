package weapons;

import ammunitions.SingleKrakGrenadesAmmunition;


public class KrakGrenades extends Grenades {
	public KrakGrenades() {
		setName("Krak Grenades");
		setCost(50);
		
		getAmmunitions().add(new SingleKrakGrenadesAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
