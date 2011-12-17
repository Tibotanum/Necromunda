package weapons;

import weapons.RangeCombatWeapon.WeaponType;

public abstract class Grenades extends RangeCombatWeapon {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4643302737861005986L;

	public Grenades() {
		setWeaponType(WeaponType.GRENADE);
		setScattering(true);
		setTemplateAttached(false);
	}

	@Override
	public int getMaximumRange() {
		int range = getOwner().getStrength() * 2 + 2;
		
		if (range > 12) {
			range = 12;
		}
		
		return range;
	}
}
