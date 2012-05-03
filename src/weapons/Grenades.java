package weapons;

public abstract class Grenades extends RangeCombatWeapon {
	public Grenades() {
		setRangeCombatWeaponType(RangeCombatWeaponType.GRENADE);
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
