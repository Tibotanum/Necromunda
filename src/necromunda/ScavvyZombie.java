package necromunda;

import necromunda.Fighter.State;

public class ScavvyZombie extends Fighter {
	public ScavvyZombie(String name, Gang ownGang) {
		super(name, new ScavvyZombieProfile(), ownGang);
		setCost(0);
	}

    @Override
    public void turnStarted() {
        float movement = (float)(Utils.rollD6() + Utils.rollD6());
        getProfile().setCurrentMovement(movement);
        super.turnStarted();
    }
	
}
