package weapons;

import necromunda.Necromunda;

public abstract class PlasmaWeapon extends RangeCombatWeapon {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5038115166567405443L;
	protected int turnCounter = 0;
	
	@Override
	public void turnStarted() {
		if (turnCounter > 0) {
			turnCounter--;
		}
		else {
			turnCounter = 0;
		}
		
		if (turnCounter <= 0) {
			setEnabled(true);
		}
		else {
			setEnabled(false);
		}
	}
}
