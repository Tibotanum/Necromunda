package ammunitions;

import weapons.RangeCombatWeapon;
import necromunda.*;

public class HeavyStubberSustainedFireAmmunition extends HeavyStubberAmmunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2062770147726272997L;
	
	public HeavyStubberSustainedFireAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Sustained Fire");
		setShotHandler(new SustainedFireShotHandler(2, new StandardShotHandler(null)));
	}
}