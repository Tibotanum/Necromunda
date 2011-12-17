package weapons;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.bounding.BoundingSphere;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

import necromunda.Necromunda;
import necromunda.Utils;

public class HeavyPlasmaGun extends PlasmaWeapon {
	/**
	 * 
	 */
	private static final long serialVersionUID = -629032065585270401L;

	public HeavyPlasmaGun() {
		setName("Heavy Plasma Gun");
		setWeaponType(WeaponType.HEAVY);
		setScattering(false);
		setCost(285);
		
		setTemplateAttached(false);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new LowEnergyAmmunition());
		getAmmunitions().add(new HighEnergyAmmunition());
	}
	
	private class LowEnergyAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2012713264749606092L;

		public LowEnergyAmmunition() {
			setName("Low Energy");
			setStrength(7);
			setDamage(0);
			setArmorSaveModification(-2);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(40);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
			
			setTemplated(true);
			setTemplateRadius(1.5f);
			
			Color color = new Color(Color.cyan.getRed() / 255f, Color.cyan.getGreen() / 255f, Color.cyan.getBlue() / 255f, 0.5f);
			setTemplateColor(color);
		}
		
		@Override
		public String getDamageText() {
			return "D3";
		}
		
		@Override
		public int getDamage() {
			int damage = Utils.rollD(3);

			return damage;
		}
	}
	
	private class HighEnergyAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2963564536087989613L;

		public HighEnergyAmmunition() {
			setName("High Energy");
			setStrength(10);
			setDamage(0);
			setArmorSaveModification(-6);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(20);
			setRangeLongLowerBound(20);
			setRangeLongUpperBound(72);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setCost(0);
			
			setTemplated(true);
			setTemplateRadius(1.5f);
			
			Color color = new Color(Color.cyan.getRed() / 255f, Color.cyan.getGreen() / 255f, Color.cyan.getBlue() / 255f, 0.5f);
			setTemplateColor(color);
		}
		
		@Override
		public void trigger() {
			turnCounter = 2;
			setEnabled(false);
			Necromunda.appendToStatusMessage(String.format("Your %s cannot be fired for one turn.", getWeapon().getName()));
		}
		
		@Override
		public String getDamageText() {
			return "D6";
		}
		
		@Override
		public int getDamage() {
			int damage = Utils.rollD6();

			return damage;
		}
	}
}
