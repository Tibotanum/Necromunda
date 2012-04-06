package necromunda;

import necromunda.Necromunda3dProvider.SelectionMode;

public class TemplateTargetHandler extends TargetHandler {

	@Override
	public void target(Necromunda3dProvider p) {
		p.getSelectedRangeCombatWeapon().trigger();

		p.fireTemplate(p.getCurrentTemplateNode());

		p.tearDownTargeting();

		if (p.getSelectionMode() != SelectionMode.REROLL) {
			p.setSelectionMode(SelectionMode.SELECT);
		}
	}
	
}
