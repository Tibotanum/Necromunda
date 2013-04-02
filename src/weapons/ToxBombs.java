package weapons;

import ammunitions.ToxBombAmmunition;
import necromunda.Necromunda;
import necromunda.Utils;

public class ToxBombs extends Grenades {
	public ToxBombs() {
		setName("Tox Bombs");
		setCost(20);
		
		getAmmunitions().add(new ToxBombAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
