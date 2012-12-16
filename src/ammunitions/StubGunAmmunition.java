package ammunitions;

import weapons.RangeCombatWeapon;

public class StubGunAmmunition extends Ammunition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7439431181537876014L;

	public StubGunAmmunition(RangeCombatWeapon weapon) {
		super(weapon);
		setName("Regular Bullets");
		setStrength(3);
		setDamage(1);
		setRangeShortLowerBound(0);
		setRangeShortUpperBound(8);
		setRangeLongLowerBound(8);
		setRangeLongUpperBound(16);
		setHitRollModificationShort(0);
		setHitRollModificationLong(-1);
		setAmmoRoll(4);
		setCost(0);
	}
}