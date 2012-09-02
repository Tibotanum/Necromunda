package appstate;

import necromunda.*;
import necromunda.Necromunda3dProvider.*;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.*;
import com.jme3.input.controls.*;

public class RerollAppState extends NecromundaAppState {
	public RerollAppState(MouseListener mouseListener, KeyboardListener keyboardListener) {
		super(mouseListener, keyboardListener);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		InputManager inputManager = getApplication().getInputManager();
		
		if (enabled) {
			inputManager.addMapping("Yes", new KeyTrigger(KeyInput.KEY_Y));
			inputManager.addMapping("No", new KeyTrigger(KeyInput.KEY_N));
			
			inputManager.addListener(keyboardListener, "Yes");
			inputManager.addListener(keyboardListener, "No");
		}
		else {
			inputManager.deleteMapping("Yes");
			inputManager.deleteMapping("No");
		}
	}
}
