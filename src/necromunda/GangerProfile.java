package necromunda;

public class GangerProfile extends FighterProfile {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6516704566486543854L;

	public GangerProfile() {
		setMovement(4);
		setWeaponSkill(3);
		setBallisticSkill(3);
		setStrength(3);
		setToughness(3);
		setWounds(1);
		setInitiative(3);
		setAttacks(1);
		setLeadership(7);
	}
}
