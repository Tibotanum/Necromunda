package necromunda;

import java.util.*;

public class SustainedFireShotHandler extends ShotHandler {
	private int maximumNumberOfSustainedFireDice;
	private int numberOfSustainedFireDice = 1;
	private int remainingShots;
	private List<FighterNode> validSustainedFireTargetNodes;
	
	public SustainedFireShotHandler(int maximumNumberOfSustainedFireDice, ShotHandler nextHandler) {
		super(nextHandler);
		validSustainedFireTargetNodes = new ArrayList<FighterNode>();
		this.maximumNumberOfSustainedFireDice = maximumNumberOfSustainedFireDice;
	}

	@Override
	public void handle(ShotInfo shotInfo) {
		Necromunda3dProvider provider = getProvider();
		FighterNode selectedFighterNode = getProvider().getSelectedFighterNode();
		FighterNode fighterNodeUnderCursor = getProvider().getFighterNodeUnderCursor();
		List<FighterNode> targetedFighterNodes = provider.getTargetedFighterNodes();
		
		if (fighterNodeUnderCursor != null) {
			if (validSustainedFireTargetNodes.isEmpty()
					&& (!provider.getValidTargetFighterNodes(selectedFighterNode).contains(fighterNodeUnderCursor))) {
				Necromunda.setStatusMessage("This fighter is not a valid target.");
				return;
			}
	
			if (validSustainedFireTargetNodes.isEmpty()) {
				targetedFighterNodes.add(fighterNodeUnderCursor);
	
				List<FighterNode> sustainedFireNeighbours = provider.getFighterNodesWithinDistance(fighterNodeUnderCursor,
						provider.getFighterNodes(), Necromunda.SUSTAINED_FIRE_RADIUS);
				sustainedFireNeighbours = provider.getVisibleFighterNodes(selectedFighterNode, sustainedFireNeighbours);
	
				validSustainedFireTargetNodes.add(fighterNodeUnderCursor);
				validSustainedFireTargetNodes.addAll(sustainedFireNeighbours);
				remainingShots--;
				targetAdded();
			}
			else {
				if (validSustainedFireTargetNodes.contains(fighterNodeUnderCursor)) {
					targetedFighterNodes.add(fighterNodeUnderCursor);
					remainingShots--;
					targetAdded();
				}
				else {
					Necromunda.setStatusMessage("This target is not a valid target for sustained fire.");
				}
			}
			
			if (remainingShots == 0) {
				super.handle(shotInfo);
			}
		}
	}
	
	private void targetAdded() {
		if (remainingShots > 0) {
			Necromunda.setStatusMessage(String.format("%s sustained fire shots remaining.", remainingShots));
		}
		else {
			Necromunda.setStatusMessage("");
		}
	}
	
	public void setNumberOfSustainedFireDice(int number) {
		numberOfSustainedFireDice = number;
		remainingShots = 0;
		
		for (int i = 0; i < number; i++) {
			remainingShots += Utils.rollD(3);
		}
	}

	public int getNumberOfSustainedFireDice() {
		return numberOfSustainedFireDice;
	}

	public int getRemainingShots() {
		return remainingShots;
	}

	public void setRemainingShots(int remainingShots) {
		this.remainingShots = remainingShots;
	}
	
	public void reset() {
		validSustainedFireTargetNodes.clear();
		setNumberOfSustainedFireDice(getNumberOfSustainedFireDice());
	}

	public int getMaximumNumberOfSustainedFireDice() {
		return maximumNumberOfSustainedFireDice;
	}

	public void setMaximumNumberOfSustainedFireDice(int maximumNumberOfSustainedFireDice) {
		this.maximumNumberOfSustainedFireDice = maximumNumberOfSustainedFireDice;
	}
}
