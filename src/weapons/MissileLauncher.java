package weapons;

import weapons.RangeCombatWeapon.RangeCombatWeaponType;


import ammunitions.*;

import com.jme3.math.ColorRGBA;

public class MissileLauncher extends RangeCombatWeapon {
	public MissileLauncher() {
		setName("Missile Launcher");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setMoveOrFire(true);	
		setCost(185);
		
		getAmmunitions().add(new SuperKrakMissileAmmunition(this));
		getAmmunitions().add(new FragMissileAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
