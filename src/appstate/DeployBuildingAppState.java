package appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.*;
import com.jme3.input.controls.*;

import necromunda.Necromunda3dProvider.KeyboardListener;
import necromunda.Necromunda3dProvider.MouseListener;
import necromunda.*;

public class DeployBuildingAppState extends NecromundaAppState {

	public DeployBuildingAppState(MouseListener mouseListener, KeyboardListener keyboardListener) {
		super(mouseListener, keyboardListener);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		InputManager inputManager = getApplication().getInputManager();
		
		if (enabled) {
			inputManager.addMapping("leftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
			inputManager.addMapping("rightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
			inputManager.addMapping("Move_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
			inputManager.addMapping("Move_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
			inputManager.addMapping("SkipBuilding", new KeyTrigger(KeyInput.KEY_K));
			inputManager.addMapping("EndBuildingDeployment", new KeyTrigger(KeyInput.KEY_E));
			
			inputManager.addListener(mouseListener, "leftClick");
			inputManager.addListener(mouseListener, "rightClick");
			inputManager.addListener(mouseListener, "Move_Left", "Move_Right", "Move_Up", "Move_Down");
			inputManager.addListener(keyboardListener, "SkipBuilding");
			inputManager.addListener(keyboardListener, "EndBuildingDeployment");
		}
		else {
			inputManager.deleteMapping("leftClick");
			inputManager.deleteMapping("rightClick");
			inputManager.deleteMapping("Move_Left");
			inputManager.deleteMapping("Move_Right");
			inputManager.deleteMapping("SkipBuilding");
			inputManager.deleteMapping("EndBuildingDeployment");
		}
	}
}
