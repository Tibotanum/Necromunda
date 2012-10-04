package necromunda;

import java.util.*;

import necromunda.Necromunda3dProvider.SelectionMode;
import weapons.RangeCombatWeapon;

import com.jme3.collision.Collidable;

public class StandardShotHandler extends ShotHandler {
	public StandardShotHandler(ShotHandler nextHandler) {
		super(nextHandler);
	}

	@Override
	public void handle(ShotInfo shotInfo) {
		Necromunda3dProvider provider = getProvider();
		TemplateNode currentTemplateNode = provider.getCurrentTemplateNode();
		FighterNode selectedFighterNode = provider.getSelectedFighterNode();
		FighterNode fighterNodeUnderCursor = provider.getFighterNodeUnderCursor();
		
		List<Collidable> collidables = provider.getTemplateBoundingVolumes();

		if (currentTemplateNode != null) {
			collidables.removeAll(currentTemplateNode.getBoundingVolumes());
		}

		collidables.add(provider.getBuildingsBoundsNode());

		List<FighterNode> otherFighterNodes = new ArrayList<FighterNode>(provider.getFighterNodes());
		otherFighterNodes.remove(selectedFighterNode);

		for (FighterNode otherFighterNode : otherFighterNodes) {
			collidables.add(otherFighterNode.getBoundingVolume());
		}

		RangeCombatWeapon weapon = provider.getSelectedRangeCombatWeapon();

		for (FighterNode fighterNode : provider.getTargetedFighterNodes()) {
			List<Collidable> tempCollidables = new ArrayList<Collidable>(collidables);
			tempCollidables.remove(fighterNode.getBoundingVolume());
			
			VisibilityInfo visibilityInfo = provider.getVisibilityInfo(selectedFighterNode, fighterNode.getCollisionShapePointCloud(),
					tempCollidables);

			float visiblePercentage = visibilityInfo.getVisiblePercentage();

			Necromunda.appendToStatusMessage("Visible percentage: " + (visiblePercentage * 100));

			float distance = fighterNode.getLocalTranslation().distance(selectedFighterNode.getLocalTranslation());

			if (distance > weapon.getMaximumRange()) {
				Necromunda.appendToStatusMessage("Object out of range.");
				continue;
			}

			int targetHitRoll = provider.getTargetHitRoll(selectedFighterNode.getFighter(), weapon, distance, provider.getHitModifier(visiblePercentage));

			if (targetHitRoll >= 10) {
				Necromunda.appendToStatusMessage(String.format("You need a %s to hit - impossible!", targetHitRoll));
				continue;
			}

			Necromunda.appendToStatusMessage(String.format("Target hit roll is %s.", targetHitRoll));

			int hitRoll = Utils.rollD6();

			Necromunda.appendToStatusMessage(String.format("Rolled a %s.", hitRoll));

			weapon.hitRoll(hitRoll);

			if ((targetHitRoll > 6) && (hitRoll == 6)) {
				targetHitRoll -= 3;
				hitRoll = Utils.rollD6();
			}

			if ((hitRoll < targetHitRoll) || (hitRoll <= 1)) {
				Necromunda.appendToStatusMessage("Shot missed...");

				if ((hitRoll == 1) && (Utils.rollD6() == 1)) {
					FighterNode strayShotFighterNode = provider.getStrayShotFighterNode(selectedFighterNode, fighterNode);

					if (strayShotFighterNode != null) {
						fighterNode = strayShotFighterNode;
						Necromunda.appendToStatusMessage(String.format("Stray shot hits %s.", fighterNode.getFighter().getName()));
					}
					else {
						continue;
					}
				}

				if (currentTemplateNode != null) {
					boolean hasEffect = true;

					if (weapon.isScattering()) {
						List<Collidable> scatterCollidables = new ArrayList<Collidable>();
						scatterCollidables.add(provider.getBuildingsNode());
						Line lineOfSight = provider.getLineOfSight(selectedFighterNode, fighterNodeUnderCursor);
						hasEffect = currentTemplateNode.scatter(provider.getLineLength(lineOfSight), scatterCollidables);
					}

					if (hasEffect) {
						provider.fireTemplate(currentTemplateNode);
					}

					provider.queueNodeForRemoval(currentTemplateNode);

					continue;
				}
			}
			else {
				Necromunda.appendToStatusMessage("Shot hit!");
				
				if (currentTemplateNode == null) {
					List<FighterNode> affectedFighterNodes = new ArrayList<FighterNode>();

					affectedFighterNodes.add(fighterNode);

					if (weapon.getAdditionalTargetRange() > 0) {
						List<FighterNode> fighterNodesWithinRange = provider.getFighterNodesWithinDistance(fighterNode, provider.getFighterNodes(),
								weapon.getAdditionalTargetRange());
						List<FighterNode> visibleFighterNodes = provider.getVisibleFighterNodes(selectedFighterNode, fighterNodesWithinRange);

						affectedFighterNodes.addAll(visibleFighterNodes);
					}

					provider.pinNormalFighters(affectedFighterNodes);

					for (FighterNode affectedFighterNode : affectedFighterNodes) {
						Fighter fighter = affectedFighterNode.getFighter();

						if (!weapon.dealDamageTo(fighter)) {
							provider.setCurrentWeapon(weapon);
							provider.setCurrentTarget(fighter);
							provider.setSelectionMode(SelectionMode.REROLL);
							Necromunda.appendToStatusMessage("Re-roll wound roll?");
						}
					}
				}
				else {
					provider.fireTemplate(currentTemplateNode);
					provider.queueNodeForRemoval(currentTemplateNode);
				}
			}
		}

		provider.getSelectedRangeCombatWeapon().trigger();

		provider.tearDownTargeting();
		
		provider.getTargetedFighterNodes().clear();

		if (provider.getSelectionMode() != SelectionMode.REROLL) {
			provider.setSelectionMode(SelectionMode.SELECT);
		}
	}

	@Override
	public void reset() {
	}
}
