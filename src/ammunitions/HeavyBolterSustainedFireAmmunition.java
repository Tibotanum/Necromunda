package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.*;

public class HeavyBolterSustainedFireAmmunition extends HeavyBolterAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1530560520189183631L;
	
	public HeavyBolterSustainedFireAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Sustained Fire");
		setShotHandler(new SustainedFireShotHandler(2, new StandardShotHandler(null)));
	}
}