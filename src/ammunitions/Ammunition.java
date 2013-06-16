package ammunitions;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.swing.JButton;

import weapons.RangeCombatWeapon;

import com.jme3.math.ColorRGBA;

import necromunda.*;

public abstract class Ammunition implements Serializable {
	private String name;
	private RangeCombatWeapon weapon;
	private boolean addingToOwnersStrength;
	private int strength;
	private int damage;
	private int armorSaveModification;
	private int cost;
	private int rangeShortLowerBound;
	private int rangeShortUpperBound;
	private int rangeLongLowerBound;
	private int rangeLongUpperBound;
	private int hitRollModificationShort;
	private int hitRollModificationLong;
	private int ammoRoll;
	private boolean targeted;
	private float additionalTargetRange;
	private boolean templated;
	private boolean templatePersistent;
	private float templateRadius;
	private float templateLength;
	private Color templateColor;
	
	private boolean rerollWound;
	private boolean templateAttached;
	private boolean scattering;
	
	private ShotHandler shotHandler;
	
	public Ammunition() {
		this(null);
	}
	
	public Ammunition(RangeCombatWeapon weapon) {
		targeted = true;
		additionalTargetRange = 0;
		templateColor = new Color(1.0f, 0.5f, 0f, 0.5f);
		this.weapon = weapon;
		
		setShotHandler(new SingleShotHandler(new StandardShotHandler(null)));
	}
	
	public void trigger() {
	}
	
	public void handleShot(ShotInfo shotInfo) {
		shotHandler.handle(shotInfo);
	}
	
	public float getDriftDistance() {
		return 0;
	}
	
	public float getDriftAngle() {
		return 0;
	}
	
	public void reset() {
		shotHandler.reset();
	}
	
	public void sustainMalfunction() {
		int ammoRoll = Utils.rollD6();

		if (ammoRoll < getAmmoRoll()) {
			weapon.setBroken(true);
			Necromunda.setStatusMessage("Your weapon sustained a malfunction.");
		}

		if (ammoRoll == 1) {
			explode();
		}
	}
	
	public int getEffectiveScatterDistance(float shotDistance, int scatterDistance) {
		float maximumScatterDistance = shotDistance / 2;
		
		if (scatterDistance >= maximumScatterDistance) {
			scatterDistance = (int)maximumScatterDistance;
		}
		
		return scatterDistance;
	}
	
	public void explode() {
		int explosionRoll = Utils.rollD6();

		if (explosionRoll < getAmmoRoll()) {
			Necromunda.setStatusMessage("Your weapon exploded!");
			dealDamageTo(getStrength() - 1, weapon.getOwner());
		}
	}
	
	public boolean dealDamageTo(Fighter... fighters) {
		return dealDamageTo(getStrength(), fighters);
	}

	public boolean dealDamageTo(int strength, Fighter... fighters) {
		for(Fighter fighter : fighters) {
			int targetWoundRoll = Necromunda.STRENGTH_RESISTANCE_MAP[strength - 1][fighter.getToughness() - 1];
			int woundRoll = Utils.rollD6();
	
			if (woundRoll >= targetWoundRoll) {
				int wounds = fighter.getProfile().getCurrentWounds();
				int inflictedWounds = getDamage();
				wounds -= inflictedWounds;
	
				Necromunda.setStatusMessage(String.format("Target wound roll is %s. Rolled a %s and wounded. %s wounds were inflicted.", targetWoundRoll,
						woundRoll, inflictedWounds));
	
				if (wounds < 1) {
	
					for (int i = 0; i < (wounds * -1) + 1; i++) {
						fighter.injure(isHighImpact());
					}
	
					wounds = 1;
				}
	
				fighter.getProfile().setCurrentWounds(wounds);
			}
			else {
				Necromunda.setStatusMessage(String.format("Target wound roll is %s. Rolled a %s and wounded not.", targetWoundRoll, woundRoll));
				return !rerollWound;
			}
		}
		
		return true;
	}
	
	public String getProfileString() {
		String profileString = String.format("Range: %s-%s-%s HRM: %s/%s Strength: %s Damage: %s ASM: %s AR: %s", getRangeShortLowerBound(),
				getRangeShortUpperBound(), getRangeLongUpperBound(), getHitRollModificationShort(), getHitRollModificationLong(), getStrength(), getDamageText(),
				getArmorSaveModification(), getAmmoRoll());

		return profileString;
	}
	
	public String getDamageText() {
		return String.valueOf(getDamage());
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public RangeCombatWeapon getWeapon() {
		return weapon;
	}
	
	public void setWeapon(RangeCombatWeapon weapon) {
		this.weapon = weapon;
	}
	
	public boolean isAddingToOwnersStrength() {
		return addingToOwnersStrength;
	}
	
	public void setAddingToOwnersStrength(boolean addingToOwnersStrength) {
		this.addingToOwnersStrength = addingToOwnersStrength;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	public int getArmorSaveModification() {
		return armorSaveModification;
	}
	
	public void setArmorSaveModification(int armorSaveModification) {
		this.armorSaveModification = armorSaveModification;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getRangeShortLowerBound() {
		return rangeShortLowerBound;
	}
	
	public void setRangeShortLowerBound(int rangeShortLowerBound) {
		this.rangeShortLowerBound = rangeShortLowerBound;
	}
	
	public int getRangeShortUpperBound() {
		return rangeShortUpperBound;
	}
	
	public void setRangeShortUpperBound(int rangeShortUpperBound) {
		this.rangeShortUpperBound = rangeShortUpperBound;
	}
	
	public int getRangeLongLowerBound() {
		return rangeLongLowerBound;
	}
	
	public void setRangeLongLowerBound(int rangeLongLowerBound) {
		this.rangeLongLowerBound = rangeLongLowerBound;
	}
	
	public int getRangeLongUpperBound() {
		return rangeLongUpperBound;
	}
	
	public void setRangeLongUpperBound(int rangeLongUpperBound) {
		this.rangeLongUpperBound = rangeLongUpperBound;
	}
	
	public int getHitRollModificationShort() {
		return hitRollModificationShort;
	}
	
	public void setHitRollModificationShort(int hitRollModificationShort) {
		this.hitRollModificationShort = hitRollModificationShort;
	}
	
	public int getHitRollModificationLong() {
		return hitRollModificationLong;
	}
	
	public void setHitRollModificationLong(int hitRollModificationLong) {
		this.hitRollModificationLong = hitRollModificationLong;
	}
	
	public int getAmmoRoll() {
		return ammoRoll;
	}
	
	public void setAmmoRoll(int ammoRoll) {
		this.ammoRoll = ammoRoll;
	}
	
	public boolean isTargeted() {
		return targeted;
	}
	
	public void setTargeted(boolean targeted) {
		this.targeted = targeted;
	}

	public float getAdditionalTargetRange() {
		return additionalTargetRange;
	}

	public void setAdditionalTargetRange(float additionalTargetRange) {
		this.additionalTargetRange = additionalTargetRange;
	}
	
	public boolean isHighImpact() {
		if (getStrength() >= 7) {
			return true;
		}
		else {
			return false;
		}
	}

	public float getTemplateRadius() {
		return templateRadius;
	}

	public void setTemplateRadius(float templateRadius) {
		this.templateRadius = templateRadius;
	}

	public float getTemplateLength() {
		return templateLength;
	}

	public void setTemplateLength(float templateLength) {
		this.templateLength = templateLength;
	}

	public Color getTemplateColor() {
		return templateColor;
	}

	public void setTemplateColor(Color templateColor) {
		this.templateColor = templateColor;
	}

	public boolean isTemplated() {
		return templated;
	}

	public void setTemplated(boolean templated) {
		this.templated = templated;
	}

	public boolean isTemplatePersistent() {
		return templatePersistent;
	}

	public void setTemplatePersistent(boolean templatePersistent) {
		this.templatePersistent = templatePersistent;
	}
	
	public boolean isTemplateToBeRemoved() {
		return true;
	}
	
	public boolean isTemplateMoving() {
		return false;
	}

	public boolean isRerollWound() {
		return rerollWound;
	}

	public void setRerollWound(boolean rerollWound) {
		this.rerollWound = rerollWound;
	}
	
	public boolean isTemplateAttached() {
		return templateAttached;
	}

	public void setTemplateAttached(boolean templateAttached) {
		this.templateAttached = templateAttached;
	}

	public boolean isScattering() {
		return scattering;
	}

	public void setScattering(boolean scattering) {
		this.scattering = scattering;
	}

	public ShotHandler getShotHandler() {
		return shotHandler;
	}

	public void setShotHandler(ShotHandler shotHandler) {
		this.shotHandler = shotHandler;
	}
}