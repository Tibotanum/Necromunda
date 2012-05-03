package weapons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.bounding.BoundingSphere;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;


public class Flamer extends RangeCombatWeapon {
	private final String outOfFuelMessage;

	public Flamer() {
		setName("Flamer");
		outOfFuelMessage = String.format("Your %s ran out of fuel.", this);
		setRangeCombatWeaponType(RangeCombatWeaponType.SPECIAL);
		setCost(40);
	}
	
	@Override
	public void addAmmunitions() {
		getAmmunitions().add(new FlamerAmmunition());		
	}

	public String getOutOfFuelMessage() {
		return outOfFuelMessage;
	}
	
	protected class FlamerAmmunition extends Ammunition {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6161133697747419382L;

		public FlamerAmmunition() {
			setStrength(4);
			setDamage(1);
			setArmorSaveModification(-2);
			setRangeShortLowerBound(0);
			setRangeShortUpperBound(0);
			setRangeLongLowerBound(0);
			setRangeLongUpperBound(0);
			setHitRollModificationShort(0);
			setHitRollModificationLong(0);
			setAmmoRoll(4);
			setTargeted(false);
			setCost(0);
			
			setTemplated(true);
			setTemplateRadius(1.25f);
			setTemplateLength(7.25f);
			
			setTemplateAttached(true);
		}
		
		@Override
		public void trigger() {
			sustainMalfunction();
		}		
	}
}
