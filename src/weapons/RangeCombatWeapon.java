package weapons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ammunitions.Ammunition;

import necromunda.*;
import necromunda.Fighter.State;

public abstract class RangeCombatWeapon extends Weapon {
	public static float MINIMUM_TEMPLATE_SIZE = 0.01f;

	public enum RangeCombatWeaponType {
		PISTOL, BASIC, SPECIAL, HEAVY, GRENADE, SCALY
	}

	private RangeCombatWeaponType rangeCombatWeaponType;
	private boolean isBroken;
	private boolean moveOrFire;
	private List<Ammunition> ammunitions;
	private Ammunition currentAmmunition;
	private int turnCounter = 0;
	
	public RangeCombatWeapon() {
		ammunitions = new ArrayList<Ammunition>();
	}

	@Override
	public void reset() {
		for (Ammunition ammunition : getAmmunitions()) {
			ammunition.reset();
		}
		
		if (turnCounter > 0) {
			turnCounter--;
		}
		
		if (turnCounter == 0) {
			setEnabled(true);
		}
		else {
			setEnabled(false);
		}
	}

	@Override
	public String getProfileString() {
		return currentAmmunition.getProfileString();
	}
	
	public void handleShot(ShotInfo shotInfo) {
		currentAmmunition.handleShot(shotInfo);
	}

	@Override
	public void trigger() {
		currentAmmunition.trigger();
		getOwner().setHasShot(true);
		getOwner().setHidden(false);
	}

	public void hitRoll(int hitRoll) {
		if (hitRoll >= 6) {
			sustainMalfunction();
		}
	}

	public void sustainMalfunction() {
		currentAmmunition.sustainMalfunction();
	}
	
	public void explode() {
		currentAmmunition.explode();
	}

	public boolean dealDamageTo(Fighter... fighters) {
		return currentAmmunition.dealDamageTo(fighters);
	}

	public RangeCombatWeaponType getRangeCombatWeaponType() {
		return rangeCombatWeaponType;
	}

	public void setRangeCombatWeaponType(RangeCombatWeaponType rangeCombatWeaponType) {
		this.rangeCombatWeaponType = rangeCombatWeaponType;
	}

	protected int getHitRollModificationShort() {
		return currentAmmunition.getHitRollModificationShort();
	}

	protected int getHitRollModificationLong() {
		return currentAmmunition.getHitRollModificationLong();
	}

	public int getAmmoRoll() {
		return currentAmmunition.getAmmoRoll();
	}

	public boolean isBroken() {
		return isBroken;
	}

	public void setBroken(boolean isBroken) {
		this.isBroken = isBroken;
	}

	protected int getRangeShortLowerBound() {
		return currentAmmunition.getRangeShortLowerBound();
	}

	protected int getRangeShortUpperBound() {
		return currentAmmunition.getRangeShortUpperBound();
	}

	protected int getRangeLongLowerBound() {
		return currentAmmunition.getRangeLongLowerBound();
	}

	protected int getRangeLongUpperBound() {
		return currentAmmunition.getRangeLongUpperBound();
	}

	public int getMaximumRange() {
		return getRangeLongUpperBound();
	}

	public int getRangeModifier(float lineOfSightLength) {
		if (lineOfSightLength <= getRangeShortUpperBound()) {
			return getHitRollModificationShort();
		}
		else {
			return getHitRollModificationLong();
		}
	}

	public boolean isTargeted() {
		return currentAmmunition.isTargeted();
	}

	public boolean isMoveOrFire() {
		return moveOrFire;
	}

	public void setMoveOrFire(boolean moveOrFire) {
		this.moveOrFire = moveOrFire;
	}
	
	public boolean isScattering() {
		return currentAmmunition.isScattering();
	}
	
	public boolean isTemplated() {
		return currentAmmunition.isTemplated();
	}
	
	public boolean isTemplateAttached() {
		return currentAmmunition.isTemplateAttached();
	}
	
	public boolean isHighImpact() {
		return currentAmmunition.isHighImpact();
	}

	public float getAdditionalTargetRange() {
		return currentAmmunition.getAdditionalTargetRange();
	}

	public Ammunition getCurrentAmmunition() {
		return currentAmmunition;
	}

	public void setCurrentAmmunition(Ammunition currentAmmunition) {
		this.currentAmmunition = currentAmmunition;
	}

	public List<Ammunition> getAmmunitions() {
		return ammunitions;
	}

	@Override
	public String getDamageText() {
		return currentAmmunition.getDamageText();
	}

	public int getTurnCounter() {
		return turnCounter;
	}

	public void setTurnCounter(int turnCounter) {
		this.turnCounter = turnCounter;
	}
}
