package necromunda;

import necromunda.Fighter.State;

public class ScavvyZombie extends Fighter {
	public static FighterProfile getTemplateProfile() {
		return new ScavvyZombieProfile();
	}
	
	public ScavvyZombie(String name, FighterProfile profile, Gang ownGang) {
		super(name, profile, ownGang);
		setCost(0);
	}

    @Override
    public void turnStarted() {
        float movement = (float)(Utils.rollD6() + Utils.rollD6());
        getProfile().setCurrentMovement(movement);
        super.turnStarted();
    }
	
}
