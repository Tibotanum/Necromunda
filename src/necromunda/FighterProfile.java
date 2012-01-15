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
}
