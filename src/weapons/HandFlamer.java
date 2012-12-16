package weapons;


import java.io.IOException;

import ammunitions.HandFlamerAmmunition;

import com.jme3.math.ColorRGBA;


public class HandFlamer extends Flamer {
	public HandFlamer() {
		setName("Hand Flamer");
		setRangeCombatWeaponType(RangeCombatWeaponType.PISTOL);
		setCost(15);
		
		getAmmunitions().add(new HandFlamerAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
