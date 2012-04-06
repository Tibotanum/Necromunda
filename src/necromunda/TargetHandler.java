package necromunda;

import java.io.Serializable;

import weapons.RangeCombatWeapon;

public abstract class TargetHandler implements Serializable {
	public abstract void target(Necromunda3dProvider necromunda3dProvider);
}
