package weapons;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import necromunda.Necromunda;
import necromunda.Utils;

import com.jme3.bounding.BoundingSphere;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

public class GrenadeLauncher extends RangeCombatWeapon {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1952085375372489650L;
	
	public GrenadeLauncher() {
		setName("Grenade Launcher");
		setWeaponType(WeaponType.SPECIAL);
		setMoveOrFire(true);
		setScattering(true);
		setCost(130);
		
		setTemplateAttached(false);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new FragGrenadesAmmunition());
		getAmmunitions().add(new KrakGrenadesAmmunition());
		getAmmunitions().add(new PlasmaGrenadesAmmunition());
	}
	
	private class FragGrenadesAmmunition extends GrenadesAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2843206583776734477L;

		public FragGrenadesAmmunition() {
			setName("Frag Grenades");
			setStrength(3);
			setDamage(1);
			setArmorSaveModification(-1);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(60);
			setHitRollModificationShort(0);
			setHitRollModificationLong(-1);
			setAmmoRoll(0);
			setCost(30);
			
			setTemplated(true);
			setTemplateRadius(2.0f);
		}
	}
	
	private class KrakGrenadesAmmunition extends GrenadesAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7717425847670438865L;

		public KrakGrenadesAmmunition() {
			setName("Krak Grenades");
			setStrength(6);
			setDamage(0);
			setArmorSaveModification(-3);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(60);
			setHitRollModificationShort(0);
			setHitRollModificationLong(-2);
			setAmmoRoll(0);
			setCost(50);
			
			setTemplated(true);
			setTemplateRadius(MINIMUM_TEMPLATE_SIZE);
		}

		@Override
		public int getDamage() {
			int damage = Utils.rollD6();

			return damage;
		}
	}
	
	private class PlasmaGrenadesAmmunition extends GrenadesAmmunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1811255775428471282L;

		public PlasmaGrenadesAmmunition() {
			setName("Plasma Grenades");
			setStrength(5);
			setDamage(1);
			setArmorSaveModification(-2);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(60);
			setHitRollModificationShort(0);
			setHitRollModificationLong(-1);
			setAmmoRoll(0);
			setCost(41);
			
			setTemplated(true);
			setTemplateRadius(1.5f);
			setTemplatePersistent(true);
			
			Color color = new Color(Color.cyan.getRed() / 255f, Color.cyan.getGreen() / 255f, Color.cyan.getBlue() / 255f, 0.5f);
			setTemplateColor(color);
		}

		@Override
		public float getDriftDistance() {
			return Utils.rollD6();
		}

		@Override
		public float getDriftAngle() {
			return Utils.getRandomAngle();
		}

		@Override
		public boolean isTemplateToBeRemoved() {
			int roll = Utils.rollD6();
			
			if ((roll >= 1) && (roll <= 3)) {
				return true;
			}
			else {
				return false;
			}
		}

		@Override
		public boolean isTemplateMoving() {
			int roll = Utils.rollD6();
			
			if (roll == 6) {
				return true;
			}
			else {
				return false;
			}
		}
	}
}
