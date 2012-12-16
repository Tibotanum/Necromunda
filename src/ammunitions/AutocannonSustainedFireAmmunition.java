package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.*;

public class AutocannonSustainedFireAmmunition extends AutocannonAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1319239769163018884L;

	public AutocannonSustainedFireAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Sustained Fire");
		setShotHandler(new SustainedFireShotHandler(1, new StandardShotHandler(null)));
	}
}