package weapons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import ammunitions.FlamerAmmunition;

import com.jme3.bounding.BoundingSphere;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;


public class Flamer extends RangeCombatWeapon {
	

	public Flamer() {
		setName("Flamer");
		setRangeCombatWeaponType(RangeCombatWeaponType.SPECIAL);
		setCost(40);
		
		getAmmunitions().add(new FlamerAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
