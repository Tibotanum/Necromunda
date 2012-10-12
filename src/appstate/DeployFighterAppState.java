package appstate;

import com.jme3.input.*;
import com.jme3.input.controls.*;

import necromunda.Necromunda3dProvider.*;

public class DeployFighterAppState extends NecromundaAppState {
	
	public DeployFighterAppState(MouseListener mouseListener, KeyboardListener keyboardListener) {
		super(mouseListener, keyboardListener);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		InputManager inputManager = getApplication().getInputManager();
		
		if (enabled) {
			inputManager.addMapping("leftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
			inputManager.addMapping("SkipFighter", new KeyTrigger(KeyInput.KEY_K));
			
			inputManager.addListener(mouseListener, "leftClick");
			inputManager.addListener(keyboardListener, "SkipFighter");
		}
		else {
			inputManager.deleteMapping("leftClick");
			inputManager.deleteMapping("SkipFighter");
		}
	}
}
