package weapons;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import necromunda.Fighter;
import necromunda.Necromunda;

public abstract class Weapon implements Serializable {
	private String name;
	protected Fighter owner;	
	private boolean enabled;
	protected int strength;
	private int damage;
	private int armorSaveModification;
	private int cost;
	private List<String> availableModes;
	private String currentMode;
	
	public Weapon() {
		availableModes = new ArrayList<String>();
		enabled = true;
	}
	
	public void trigger() {
	}
	
	public void turnStarted() {
		reset();
	}
	
	public void reset() {
	}
	
	@Override
	public String toString() {
		return name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getAvailableModes() {
		return availableModes;
	}

	public String getMode() {
		return currentMode;
	}

	public void setMode(String mode) {
		this.currentMode = mode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public abstract String getProfileString();

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Fighter getOwner() {
		return owner;
	}

	public void setOwner(Fighter owner) {
		this.owner = owner;
	}
	
	public String getDamageText() {
		return String.valueOf(getDamage());
	}
}
