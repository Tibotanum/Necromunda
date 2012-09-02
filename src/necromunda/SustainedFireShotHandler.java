package necromunda;

import java.util.List;

public class SustainedFireShotHandler extends ShotHandler {
	private int remainingShots;
	private List<FighterNode> validSustainedFireTargetNodes;
	private ShotInfo shotInfo;

	@Override
	public void handle(ShotInfo shotInfo) {
		if (shotInfo == null) {
			this.shotInfo = shotInfo;
		}
		
		Necromunda3dProvider provider = getProvider();
		FighterNode selectedFighterNode = getProvider().getSelectedFighterNode();
		FighterNode fighterNodeUnderCursor = getProvider().getFighterNodeUnderCursor();
		List<FighterNode> targetedFighterNodes = shotInfo.getTargetedFighterNodes();
		
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
			}
			else {
				if (validSustainedFireTargetNodes.contains(fighterNodeUnderCursor)) {
					targetedFighterNodes.add(fighterNodeUnderCursor);
					remainingShots--;
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

	public int getRemainingShots() {
		return remainingShots;
	}

	public void setRemainingShots(int remainingShots) {
		this.remainingShots = remainingShots;
	}
}
