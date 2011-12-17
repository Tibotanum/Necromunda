package weapons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import necromunda.Fighter;
import necromunda.Necromunda;
import necromunda.Utils;
import necromunda.Fighter.State;

public abstract class RangeCombatWeapon extends Weapon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4885937292616950445L;
	
	public static float MINIMUM_TEMPLATE_SIZE = 0.01f;

	public enum WeaponType {
		PISTOL, BASIC, SPECIAL, HEAVY, GRENADE
	}

	private WeaponType weaponType;
	private boolean isBroken;
	private boolean moveOrFire;
	private List<Ammunition> ammunitions;
	private Ammunition currentAmmunition;
	private boolean templateAttached;
	private boolean scattering;
	
	public RangeCombatWeapon() {
		ammunitions = new ArrayList<Ammunition>();		
		addAmmunitions();
		setAmmunitionProperties();
	}
	
	public abstract void addAmmunitions();
	
	public void setAmmunitionProperties() {	
		setCurrentAmmunition(getAmmunitions().get(0));
		
		for (Ammunition ammunition : getAmmunitions()) {
			ammunition.setWeapon(this);
		}
	}

	@Override
	public String getProfileString() {
		return currentAmmunition.getProfileString();
	}
	
	public void targetAdded() {
		setNumberOfShots(getNumberOfShots() - 1);

		if (getNumberOfShots() > 0) {
			Necromunda.setStatusMessage(String.format("%s sustained fire shots remaining.", getNumberOfShots()));
		}
		else {
			Necromunda.setStatusMessage("");
		}
	}

	@Override
	public void trigger() {
		currentAmmunition.trigger();
		getOwner().setHasShot(true);
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

	public void dealDamageTo(Fighter... fighters) {
		currentAmmunition.dealDamageTo(fighters);
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
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
	
	public int getNumberOfShots() {
		return currentAmmunition.getNumberOfShots();
	}
	
	public void setNumberOfShots(int numberOfShots) {
		currentAmmunition.setNumberOfShots(numberOfShots);
	}

	public void resetNumberOfShots() {
		for (Ammunition ammunition : getAmmunitions()) {
			ammunition.resetNumberOfShots();
		}
	}

	public boolean isMoveOrFire() {
		return moveOrFire;
	}

	public void setMoveOrFire(boolean moveOrFire) {
		this.moveOrFire = moveOrFire;
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
}
