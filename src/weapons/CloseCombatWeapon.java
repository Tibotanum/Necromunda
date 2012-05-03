package weapons;

public class CloseCombatWeapon extends Weapon {
	private boolean addingToOwnersStrength;
	
	@Override
	public String getProfileString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getStrength() {
		if (addingToOwnersStrength) {
			return owner.getStrength() + strength;
		}
		else {
			return super.getStrength();
		}
	}

	public boolean isAddingToOwnersStrength() {
		return addingToOwnersStrength;
	}

	public void setAddingToOwnersStrength(boolean addingToOwnersStrength) {
		this.addingToOwnersStrength = addingToOwnersStrength;
	}
}
