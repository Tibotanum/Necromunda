package necromunda;

import java.util.List;

public class SingleShotHandler extends ShotHandler {

	public SingleShotHandler(ShotHandler nextHandler) {
		super(nextHandler);
	}

	@Override
	public void handle(ShotInfo shotInfo) {
		FighterNode fighterNodeUnderCursor = getProvider().getFighterNodeUnderCursor();
		
		if ((fighterNodeUnderCursor != null) && !getProvider().getValidTargetFighterNodes(getProvider().getSelectedFighterNode()).contains(fighterNodeUnderCursor)) {
			Necromunda.setStatusMessage("This fighter is not a valid target.");
			return;
		}
	
		getProvider().getTargetedFighterNodes().add(fighterNodeUnderCursor);
		
		super.handle(shotInfo);
	}

	@Override
	public void reset() {
	}
}
