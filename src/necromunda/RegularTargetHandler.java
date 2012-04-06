package necromunda;

import java.util.*;

import necromunda.Necromunda3dProvider.SelectionMode;

import com.jme3.collision.Collidable;

public class RegularTargetHandler extends TargetHandler {

	@Override
	public void target(Necromunda3dProvider p) {
		FighterNode fighterNodeUnderCursor = p.getFighterNodeUnderCursor();

		if (fighterNodeUnderCursor == null) {
			return;
		}

		if (!p.getHostileFighterNodesFrom(p.getFighterNodes()).contains(fighterNodeUnderCursor)) {
			Necromunda.setStatusMessage("This fighter is not hostile.");
			return;
		}

		if (p.getValidSustainedFireTargetFighterNodes().isEmpty() && (!p.getValidTargetFighterNodes().contains(fighterNodeUnderCursor))) {
			Necromunda.setStatusMessage("This fighter is not a valid target.");
			return;
		}

		boolean targetAdded = p.addTarget(fighterNodeUnderCursor);

		if (targetAdded) {
			p.getSelectedRangeCombatWeapon().targetAdded();
		}

		if (p.getSelectedRangeCombatWeapon().getNumberOfShots() > 0) {
			return;
		}
		
		p.fireTargetedWeapon(p.getSelectedRangeCombatWeapon());
		p.getSelectedRangeCombatWeapon().trigger();

		p.tearDownTargeting();

		p.getTargetedFighterNodes().clear();
		p.getValidSustainedFireTargetFighterNodes().clear();
		
		if (p.getSelectionMode() != SelectionMode.REROLL) {
			p.setSelectionMode(SelectionMode.SELECT);
		}
	}
}
