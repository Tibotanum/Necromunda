package weapons;

import weapons.RangeCombatWeapon.WeaponType;

public abstract class Grenades extends RangeCombatWeapon {
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
