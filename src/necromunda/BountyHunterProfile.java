package necromunda;

public class BountyHunterProfile extends FighterProfile {
	public BountyHunterProfile() {
		super(4, 4, 4, 3, 3, 2, 4, 1, 8);
	}

	public void doAdvanceRolls() {
		switch (Utils.rollD6()) {
			case 1:
				setWeaponSkill(getWeaponSkill() + 1);
				break;
			case 2:
				setBallisticSkill(getBallisticSkill() + 1);
				break;
			case 3:
				setInitiative(getInitiative() + 1);
				break;
			case 4:
				setWeaponSkill(getLeadership() + 1);
				break;
			case 5:
				switch (Utils.rollD(2)) {
					case 1:
						setStrength(getStrength() + 1);
						break;
					case 2:
						setToughness(getToughness() + 1);
						break;
				}
				break;
			case 6:
				switch (Utils.rollD(2)) {
					case 1:
						if (getWounds() == 3) {
							setWounds(3);
						}
						else {
							setWounds(getWounds() + 1);
						}
						break;
					case 2:
						setAttacks(getAttacks() + 1);
						break;
				}
				break;
		}
	}
}
