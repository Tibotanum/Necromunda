package necromunda;

import java.io.Serializable;

public abstract class FighterProfile implements Serializable {
	private int movement;
	private int weaponSkill;
	private int ballisticSkill;
	private int strength;
	private int toughness;
	private int wounds;
	private int initiative;
	private int attacks;
	private int leadership;
	
	private float currentMovement;
	private int currentWeaponSkill;
	private int currentBallisticSkill;
	private int currentStrength;
	private int currentToughness;
	private int currentWounds;
	private int currentInitiative;
	private int currentAttacks;
	private int currentLeadership;
	
	public FighterProfile(int movement, int weaponSkill, int ballisticSkill, int strength, int toughness, int wounds, int initiative, int attacks, int leadership) {
		this.movement = movement;
		this.weaponSkill = weaponSkill;
		this.ballisticSkill = ballisticSkill;
		this.strength = strength;
		this.toughness = toughness;
		this.wounds = wounds;
		this.initiative = initiative;
		this.attacks = attacks;
		this.leadership = leadership;
		
		this.currentMovement = movement;
		this.currentWeaponSkill = weaponSkill;
		this.currentBallisticSkill = ballisticSkill;
		this.currentStrength = strength;
		this.currentToughness = toughness;
		this.currentWounds = wounds;
		this.currentInitiative = initiative;
		this.currentAttacks = attacks;
		this.currentLeadership = leadership;
	}

	public String toString() {
		String string = String.format("M: %s, WS: %s, BS: %s, S: %s, T: %s, W: %s, I: %s, A: %s, Ld: %s", movement, weaponSkill, ballisticSkill, strength,
				toughness, wounds, initiative, attacks, leadership);
		return string;
	}

	public int getMovement() {
		return movement;
	}

	public void setMovement(int movement) {
		this.movement = movement;
	}

	public int getWeaponSkill() {
		return weaponSkill;
	}

	public void setWeaponSkill(int weaponSkill) {
		this.weaponSkill = weaponSkill;
	}

	public int getBallisticSkill() {
		return ballisticSkill;
	}

	public void setBallisticSkill(int ballisticSkill) {
		this.ballisticSkill = ballisticSkill;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getToughness() {
		return toughness;
	}

	public void setToughness(int toughness) {
		this.toughness = toughness;
	}

	public int getWounds() {
		return wounds;
	}

	public void setWounds(int wounds) {
		this.wounds = wounds;
	}

	public int getInitiative() {
		return initiative;
	}

	public void setInitiative(int initiative) {
		this.initiative = initiative;
	}

	public int getAttacks() {
		return attacks;
	}

	public void setAttacks(int attacks) {
		this.attacks = attacks;
	}

	public int getLeadership() {
		return leadership;
	}

	public void setLeadership(int leadership) {
		this.leadership = leadership;
	}

	public float getCurrentMovement() {
		return currentMovement;
	}

	public void setCurrentMovement(float currentMovement) {
		this.currentMovement = currentMovement;
	}

	public int getCurrentWeaponSkill() {
		return currentWeaponSkill;
	}

	public void setCurrentWeaponSkill(int currentWeaponSkill) {
		this.currentWeaponSkill = currentWeaponSkill;
	}

	public int getCurrentBallisticSkill() {
		return currentBallisticSkill;
	}

	public void setCurrentBallisticSkill(int currentBallisticSkill) {
		this.currentBallisticSkill = currentBallisticSkill;
	}

	public int getCurrentStrength() {
		return currentStrength;
	}

	public void setCurrentStrength(int currentStrength) {
		this.currentStrength = currentStrength;
	}

	public int getCurrentToughness() {
		return currentToughness;
	}

	public void setCurrentToughness(int currentToughness) {
		this.currentToughness = currentToughness;
	}

	public int getCurrentWounds() {
		return currentWounds;
	}

	public void setCurrentWounds(int currentWounds) {
		this.currentWounds = currentWounds;
	}

	public int getCurrentInitiative() {
		return currentInitiative;
	}

	public void setCurrentInitiative(int currentInitiative) {
		this.currentInitiative = currentInitiative;
	}

	public int getCurrentAttacks() {
		return currentAttacks;
	}

	public void setCurrentAttacks(int currentAttacks) {
		this.currentAttacks = currentAttacks;
	}

	public int getCurrentLeadership() {
		return currentLeadership;
	}

	public void setCurrentLeadership(int currentLeadership) {
		this.currentLeadership = currentLeadership;
	}
}
