package necromunda;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import weapons.RangeCombatWeapon;
import weapons.Weapon;
import weapons.WebPistol;

public abstract class Fighter implements Serializable {
	public static FighterProfile getTemplateProfile() {
		return new GangerProfile();
	}

	public enum State {
		NORMAL("Normal"),
		PINNED("Pinned"),
		DOWN("Down"),
		SEDATED("Sedated"),
		COMATOSE("Comatose"),
		BROKEN("Broken"),
		OUT_OF_ACTION("Out of Action");

		private String literal;

		private State(String literal) {
			this.literal = literal;
		}

		@Override
		public String toString() {
			return literal;
		}
	}

	public enum Type {
		LEADER("Leader", Leader.class),
		GANGER("Ganger", Ganger.class),
		JUVE("Juve", Juve.class),
		HEAVY("Heavy", Heavy.class),
		BOUNTY_HUNTER("Bounty Hunter", BountyHunter.class),
		SCAVVY_LEADER("Scavvy Leader", ScavvyBoss.class),
		SCAVVY("Scavvy", Scavvy.class),
		SCALY("Scaly", ScavvyScaly.class),
		SCAVVY_ZOMBIE("Scavvy Zombie", ScavvyZombie.class),
		SCAVVY_GHOUL("Scavvy Ghoul", ScavvyGhoul.class),
		SCAVVY_DOG("Scavvy Dog", ScavvyDog.class),
		REDEMPTIONIST_PRIEST("Priest", RedemptionistPriest.class),
		REDEMPTIONIST_CRUSADER("Crusader", RedemptionistCrusader.class),
		REDEMPTIONIST_ZEALOT("Zealot", RedemptionistZealot.class),
		REDEMPTIONIST_DEACON("Deacon", RedemptionistDeacon.class),
		REDEMTIONIST_DEVOTEE("Devotee", RedemptionistDevotee.class),
		RATSKIN_CHIEF("Chief", RatskinChief.class),
		RATSKIN("Ratskin", Ratskin.class),
		RATSKIN_BRAVE("Brave", RatskinBrave.class),
		RATSKIN_WARRIOR("Totem Warrior", RatskinTotemWarrior.class);

		private String literal;
		private Class<? extends Fighter> associatedClass;

		private Type(String literal, Class<? extends Fighter> associatedClass) {
			this.literal = literal;
			this.associatedClass = associatedClass;
		}

		@Override
		public String toString() {
			return literal;
		}

		public Class<? extends Fighter> getAssociatedClass() {
			return associatedClass;
		}
	}

	private FighterProfile profile;
	private State state;
	private boolean canMove;
	private boolean hasMoved;
	private boolean isGoingToMove;
	private boolean canRun;
	private boolean hasRun;
	private boolean isGoingToRun;
	private boolean hasShot;
	private boolean canShoot;
	private boolean isWebbed;
	private boolean isHidden;
	private float remainingMovement;
	private float baseRadius;
	private int fleshWounds;
	private boolean isSpotted;
	private Gang gang;
	private RangeCombatWeapon selectedRangeCombatWeapon;
	private CyclicList<Weapon> weapons;
	private int cost;
	private BasedModelImage fighterImage;
	private String name;

	public static Fighter createInstance(Type type, String name, Gang ownGang) {
		Fighter fighter = null;

		switch (type) {
			case LEADER:
				fighter = new Leader(name, ownGang);
				break;
			case GANGER:
				fighter = new Ganger(name, ownGang);
				break;
			case JUVE:
				fighter = new Juve(name, ownGang);
				break;
			case HEAVY:
				fighter = new Heavy(name, ownGang);
				break;
			case BOUNTY_HUNTER:
				fighter = new BountyHunter(name, ownGang);
				break;
			case SCAVVY_LEADER:
				fighter = new ScavvyBoss(name, ownGang);
				break;
			case SCAVVY:
				fighter = new Scavvy(name, ownGang);
				break;
			case SCALY:
				fighter = new ScavvyScaly(name, ownGang);
				break;
			case SCAVVY_ZOMBIE:
				fighter = new ScavvyZombie(name, ownGang);
				break;
			case SCAVVY_GHOUL:
				fighter = new ScavvyGhoul(name, ownGang);
				break;
			case SCAVVY_DOG:
				fighter = new ScavvyDog(name, ownGang);
				break;
			case REDEMPTIONIST_PRIEST:
				fighter = new RedemptionistPriest(name, ownGang);
				break;
			case REDEMPTIONIST_CRUSADER:
				fighter = new RedemptionistCrusader(name, ownGang);
				break;
			case REDEMPTIONIST_ZEALOT:
				fighter = new RedemptionistZealot(name, ownGang);
				break;
			case REDEMPTIONIST_DEACON:
				fighter = new RedemptionistDeacon(name, ownGang);
				break;
			case REDEMTIONIST_DEVOTEE:
				fighter = new RedemptionistDevotee(name, ownGang);
				break;
			case RATSKIN_CHIEF:
				fighter = new RatskinChief(name, ownGang);
				break;
			case RATSKIN:
				fighter = new Ratskin(name, ownGang);
				break;
			case RATSKIN_BRAVE:
				fighter = new RatskinBrave(name, ownGang);
				break;
			case RATSKIN_WARRIOR:
				fighter = new RatskinTotemWarrior(name, ownGang);
				break;
		}

		return fighter;
	}

	public Fighter(String name, FighterProfile profile, Gang ownGang) {
		this.name = name;
		this.profile = profile;
		this.gang = ownGang;

		state = State.NORMAL;
		canMove = true;
		canRun = true;
		canShoot = true;
		isHidden = false;
		baseRadius = 0.5f;
		weapons = new CyclicList<Weapon>();
	}

	/*
	 * private void readObject(ObjectInputStream in) throws IOException,
	 * ClassNotFoundException { in.defaultReadObject(); List<Weapon> tempWeapons
	 * = new ArrayList<Weapon>(weapons); weapons.clear();
	 * 
	 * for (Weapon weapon : tempWeapons) { addWeapon(weapon); } }
	 */

	public void addWeapon(Weapon weapon) {
		weapons.add(weapon);
	}

	public void removeAllWeapons() {
		weapons.clear();
	}

	public void injure(boolean highImpact) {
		int injuryRoll = Utils.rollD6();
		State tempState = null;

		if (injuryRoll == 1) {
			addFleshWound();
			tempState = state;

			if ((profile.getCurrentWeaponSkill() <= 0) && (profile.getCurrentBallisticSkill() <= 0)) {
				tempState = State.OUT_OF_ACTION;
			}
		}
		else if ((injuryRoll > 1) && (injuryRoll < 5)) {
			tempState = State.DOWN;
		}
		else if (injuryRoll == 5) {
			if (highImpact) {
				tempState = State.OUT_OF_ACTION;
			}
			else {
				tempState = State.DOWN;
			}
		}
		else {
			tempState = State.OUT_OF_ACTION;
		}

		if (tempState.equals(State.DOWN)) {
			if (state.equals(State.NORMAL) || state.equals(State.PINNED)) {
				state = tempState;
			}
		}
		else if (tempState.equals(State.OUT_OF_ACTION)) {
			state = tempState;
		}
	}

	private void addFleshWound() {
		fleshWounds++;
		profile.setCurrentWeaponSkill(profile.getCurrentWeaponSkill() - 1);
		profile.setCurrentBallisticSkill(profile.getCurrentBallisticSkill() - 1);
	}

	public void setNextNormalState() {
		int injuryRoll = Utils.rollD6();

		if (injuryRoll == 1) {
			addFleshWound();

			if ((profile.getCurrentWeaponSkill() <= 0) && (profile.getCurrentBallisticSkill() <= 0)) {
				state = State.OUT_OF_ACTION;
			}
			else {
				state = State.PINNED;
			}
		}
		else if ((injuryRoll > 1) && (injuryRoll < 6)) {
			state = State.DOWN;
		}
		else {
			state = State.OUT_OF_ACTION;
		}
	}

	public void poison() {
		int injuryRoll = Utils.rollD6();
		State tempState = state;

		if ((injuryRoll == 1) || (injuryRoll == 2)) {
		}
		else if ((injuryRoll == 3) || (injuryRoll == 4)) {
			tempState = State.SEDATED;
		}
		else if (injuryRoll == 5) {
			tempState = State.COMATOSE;
		}
		else {
			tempState = State.OUT_OF_ACTION;
		}

		if (tempState.equals(State.PINNED)) {
			if (state.equals(State.NORMAL)) {
				state = tempState;
			}
		}
		else if (tempState.equals(State.SEDATED) || tempState.equals(State.COMATOSE)) {
			if (state.equals(State.NORMAL) || state.equals(State.PINNED)) {
				state = tempState;
			}
		}
		else if (tempState.equals(State.OUT_OF_ACTION)) {
			state = tempState;
		}
	}

	public void setNextPoisonedState() {
		int injuryRoll = Utils.rollD6();

		if ((injuryRoll == 1) || (injuryRoll == 2)) {
			setState(State.PINNED);
		}
		else if ((injuryRoll == 3) || (injuryRoll == 4)) {
			setState(State.SEDATED);
		}
		else if (injuryRoll == 5) {
			setState(State.COMATOSE);
		}
		else {
			setState(State.OUT_OF_ACTION);
		}
	}

	public void breakWeb() {
		if (isWebbed()) {
			int webRoll = Utils.rollD6();

			if ((webRoll + getStrength()) >= 9) {
				setWebbed(false);
				Necromunda.appendToStatusMessage("This ganger has broken the web.");
			}
			else {
				WebPistol.dealWebDamage(this);
			}
		}
		else {
			Necromunda.setStatusMessage("This ganger is not webbed.");
		}
	}

	public void resetRemainingMovement() {
		if (state.equals(State.DOWN) || state.equals(State.SEDATED)) {
			setRemainingMovement(2);
		}
		else if (state.equals(State.COMATOSE)) {
			setRemainingMovement(0);
		}
		else {
			setRemainingMovement(profile.getCurrentMovement());
		}
	}

	public boolean isGoingToMove() {
		return isGoingToMove;
	}

	public void setGoingToMove(boolean isGoingToMove) {
		this.isGoingToMove = isGoingToMove;
	}

	public boolean isGoingToRun() {
		return isGoingToRun;
	}

	public void setGoingToRun(boolean isGoingToRun) {
		this.isGoingToRun = isGoingToRun;
		resetRemainingMovement();

		if (isGoingToRun) {
			setRemainingMovement(getRemainingMovement() * 2);
		}
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	public void turnStarted() {
		setHasMoved(false);
		setHasRun(false);
		setHasShot(false);
		resetRemainingMovement();

		for (Weapon weapon : weapons) {
			weapon.turnStarted();
		}
	}

	public void turnEnded() {
		if (state.equals(State.PINNED)) {
			state = State.NORMAL;
		}
		else if (state.equals(State.DOWN)) {
			setNextNormalState();
		}
		else if (state.equals(State.SEDATED) || state.equals(State.COMATOSE)) {
			setNextPoisonedState();
		}
	}

	public void unpinByInitiative() {
		int initiativeRoll = Utils.rollD6();

		if (initiativeRoll <= profile.getInitiative()) {
			state = State.NORMAL;
			Necromunda.appendToStatusMessage(String.format("%s unpins by initiative.", this));
		}
		else {
			Necromunda.appendToStatusMessage(String.format("%s fails to unpin by initiative.", this));
		}
	}

	public int getGrenadeRange() {
		int range = profile.getStrength() * 2 + 2;

		if (range > 12) {
			range = 12;
		}

		return range;
	}

	public boolean isGangMate(Fighter fighter) {
		return gang.getGangMembers().contains(fighter);
	}

	public CyclicList<Weapon> getWeapons() {
		return weapons;
	}

	public float getBaseRadius() {
		return baseRadius;
	}

	public void setBaseRadius(float baseRadius) {
		this.baseRadius = baseRadius;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Gang getGang() {
		return gang;
	}

	public void setGang(Gang gang) {
		this.gang = gang;
	}

	public boolean isSpotted() {
		return isSpotted;
	}

	public void setSpotted(boolean isSpotted) {
		this.isSpotted = isSpotted;
	}

	public RangeCombatWeapon getSelectedRangeCombatWeapon() {
		return selectedRangeCombatWeapon;
	}

	public void setSelectedRangeCombatWeapon(RangeCombatWeapon selectedRangeCombatWeapon) {
		this.selectedRangeCombatWeapon = selectedRangeCombatWeapon;
	}

	public FighterProfile getProfile() {
		return profile;
	}

	public int getFleshWounds() {
		return fleshWounds;
	}

	public void setFleshWounds(int fleshWounds) {
		this.fleshWounds = fleshWounds;
	}

	public int getCost() {
		return cost;
	}

	protected void setCost(int cost) {
		this.cost = cost;
	}

	public int getValue() {
		int value = getCost();

		for (Weapon weapon : weapons) {
			value += weapon.getCost();
		}

		return value;
	}

	public BasedModelImage getFighterImage() {
		return fighterImage;
	}

	public void setFighterImage(BasedModelImage gangerPicture) {
		this.fighterImage = gangerPicture;
	}

	public boolean isReliable() {
		if (state.equals(State.NORMAL) || state.equals(State.PINNED)) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean canShoot() {
		if (!this.canShoot || hasShot || hasRun || !state.equals(State.NORMAL)) {
			return false;
		}
		else {
			return true;
		}
	}

	public void setCanShoot(boolean canShoot) {
		this.canShoot = canShoot;
	}

	public boolean canMove() {
		if (!canMove || getRemainingMovement() == 0 || state.equals(State.COMATOSE) || isWebbed
				|| state.equals(State.PINNED)) {
			return false;
		}
		else {
			return true;
		}
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public boolean hasRun() {
		return hasRun;
	}

	public void setHasRun(boolean hasRun) {
		this.hasRun = hasRun;
	}

	public boolean canRun() {
		/*
		 * List<? extends Fighter> hostileGangers =
		 * gang.getHostileGangers(game.getGangs()); List<Fighter>
		 * visibleHostileGangers = game.getVisibleObjects(position,
		 * hostileGangers);
		 * 
		 * for (Fighter object : visibleHostileGangers) { float
		 * runSpotDistanceBetweenPositions = Necromunda.RUN_SPOT_DISTANCE +
		 * this.getRadius() + object.getRadius();
		 * 
		 * if (position.distance(object.getPosition()) <=
		 * runSpotDistanceBetweenPositions) { return false; } }
		 */

		if (!this.canMove || getRemainingMovement() == 0 || !this.canRun || !state.equals(State.NORMAL)) {
			return false;
		}
		else {
			return true;
		}
	}

	public void setCanRun(boolean canRun) {
		this.canRun = canRun;
	}

	public boolean hasShot() {
		return hasShot;
	}

	public void setHasShot(boolean hasShot) {
		this.hasShot = hasShot;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isWebbed() {
		return isWebbed;
	}

	public void setWebbed(boolean isWebbed) {
		this.isWebbed = isWebbed;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public float getRemainingMovement() {
		return remainingMovement;
	}

	public void setRemainingMovement(float remainingMovement) {
		this.remainingMovement = remainingMovement;
	}

	public float getMovement() {
		return profile.getMovement();
	}

	public int getStrength() {
		return profile.getStrength();
	}

	public int getToughness() {
		return profile.getToughness();
	}

	public int getWounds() {
		return profile.getWounds();
	}

	public int getInitiative() {
		return profile.getInitiative();
	}

	public int getAttacks() {
		return profile.getAttacks();
	}

	public int getLeadership() {
		return profile.getLeadership();
	}

	public boolean isBroken() {
		return state.equals(State.BROKEN);
	}

	public boolean isComatose() {
		return state.equals(State.COMATOSE);
	}

	public boolean isDown() {
		return state.equals(State.DOWN);
	}

	public boolean isNormal() {
		return state.equals(State.NORMAL);
	}

	public boolean isOutOfAction() {
		return state.equals(State.OUT_OF_ACTION);
	}

	public boolean isPinned() {
		return state.equals(State.PINNED);
	}

	public boolean isSedated() {
		return state.equals(State.SEDATED);
	}
}
