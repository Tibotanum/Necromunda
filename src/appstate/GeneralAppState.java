package appstate;

import necromunda.*;
import necromunda.Necromunda3dProvider.*;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.*;
import com.jme3.input.controls.*;

public class GeneralAppState extends NecromundaAppState {

	public GeneralAppState(MouseListener mouseListener, KeyboardListener keyboardListener) {
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
			inputManager.addMapping("Move_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
			inputManager.addMapping("Move_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
			inputManager.addMapping("Break", new KeyTrigger(KeyInput.KEY_B));
			inputManager.addMapping("Move", new KeyTrigger(KeyInput.KEY_M));
			inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_R));
			inputManager.addMapping("Hide", new KeyTrigger(KeyInput.KEY_I));
			inputManager.addMapping("Climb", new KeyTrigger(KeyInput.KEY_C));
			inputManager.addMapping("Cycle", new KeyTrigger(KeyInput.KEY_Y));
			inputManager.addMapping("Mode", new KeyTrigger(KeyInput.KEY_O));
			inputManager.addMapping("SustainedFireDice", new KeyTrigger(KeyInput.KEY_P));
			inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_H));
			inputManager.addMapping("NextPhase", new KeyTrigger(KeyInput.KEY_N));
			inputManager.addMapping("EndTurn", new KeyTrigger(KeyInput.KEY_E));
			
			inputManager.addListener(mouseListener, "leftClick");
			inputManager.addListener(mouseListener, "rightClick");
			inputManager.addListener(mouseListener, "Move_Left", "Move_Right", "Move_Up", "Move_Down");
			inputManager.addListener(keyboardListener, "Break");
			inputManager.addListener(keyboardListener, "Move");
			inputManager.addListener(keyboardListener, "Run");
			inputManager.addListener(keyboardListener, "Hide");
			inputManager.addListener(keyboardListener, "Climb");
			inputManager.addListener(keyboardListener, "Cycle");
			inputManager.addListener(keyboardListener, "Mode");
			inputManager.addListener(keyboardListener, "SustainedFireDice");
			inputManager.addListener(keyboardListener, "Shoot");
			inputManager.addListener(keyboardListener, "NextPhase");
			inputManager.addListener(keyboardListener, "EndTurn");
		}
		else {
			inputManager.deleteMapping("leftClick");
			inputManager.deleteMapping("rightClick");
			inputManager.deleteMapping("Move_Left");
			inputManager.deleteMapping("Move_Right");
			inputManager.deleteMapping("Move_Up");
			inputManager.deleteMapping("Move_Down");
			inputManager.deleteMapping("Break");
			inputManager.deleteMapping("Move");
			inputManager.deleteMapping("Run");
			inputManager.deleteMapping("Hide");
			inputManager.deleteMapping("Climb");
			inputManager.deleteMapping("Cycle");
			inputManager.deleteMapping("Mode");
			inputManager.deleteMapping("SustainedFireDice");
			inputManager.deleteMapping("Shoot");
			inputManager.deleteMapping("NextPhase");
			inputManager.deleteMapping("EndTurn");
		}
	}
}
