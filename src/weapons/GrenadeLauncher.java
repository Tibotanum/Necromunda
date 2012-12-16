package weapons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import necromunda.Necromunda;

import ammunitions.*;

import com.jme3.bounding.BoundingSphere;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

public class GrenadeLauncher extends RangeCombatWeapon {
	public GrenadeLauncher() {
		setName("Grenade Launcher");
		setRangeCombatWeaponType(RangeCombatWeaponType.SPECIAL);
		setMoveOrFire(true);
		
		setCost(130);
		
		getAmmunitions().add(new GrenadeLauncherFragGrenadesAmmunition(this));
		getAmmunitions().add(new GrenadeLauncherKrakGrenadesAmmunition(this));
		getAmmunitions().add(new GrenadeLauncherPlasmaGrenadesAmmunition(this));
		
		setCurrentAmmunition(getAmmunitions().get(0));
	}
}
