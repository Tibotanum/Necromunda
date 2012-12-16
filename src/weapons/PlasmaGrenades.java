package weapons;


import ammunitions.SinglePlasmaGrenadesAmmunition;
import necromunda.Necromunda;

public class PlasmaGrenades extends Grenades {
	public PlasmaGrenades() {
		setName("Plasma Grenades");
		setCost(41);
		
		getAmmunitions().add(new SinglePlasmaGrenadesAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
