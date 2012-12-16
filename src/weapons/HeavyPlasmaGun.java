package weapons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import ammunitions.*;

import com.jme3.bounding.BoundingSphere;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;


public class HeavyPlasmaGun extends RangeCombatWeapon {
	public HeavyPlasmaGun() {
		setName("Heavy Plasma Gun");
		setRangeCombatWeaponType(RangeCombatWeaponType.HEAVY);
		setCost(285);
		
		setMoveOrFire(true);
		
		getAmmunitions().add(new HeavyPlasmaGunLowEnergyAmmunition(this));
		getAmmunitions().add(new HeavyPlasmaGunHighEnergyAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
