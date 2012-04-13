package necromunda;

import necromunda.Necromunda3dProvider.*;

import com.jme3.input.*;
import com.jme3.input.controls.*;

public class GeneralAppState extends NecromundaAppState {

	public GeneralAppState(InputManager inputManager, MouseListener mouseListener, KeyboardListener keyboardListener) {
		super(inputManager, mouseListener, keyboardListener);
	}
	
	protected void addMappings() {
		inputManager.addMapping("leftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping("rightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addListener(mouseListener, "leftClick");
		inputManager.addListener(mouseListener, "rightClick");

		inputManager.addMapping("Move_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("Move_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("Move_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping("Move_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addListener(mouseListener, "Move_Left", "Move_Right", "Move_Up", "Move_Down");

		inputManager.addMapping("Break", new KeyTrigger(KeyInput.KEY_B));
		inputManager.addListener(keyboardListener, "Break");

		inputManager.addMapping("Move", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(keyboardListener, "Move");

		inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_R));
		inputManager.addListener(keyboardListener, "Run");
		
		inputManager.addMapping("Hide", new KeyTrigger(KeyInput.KEY_I));
		inputManager.addListener(keyboardListener, "Hide");

		inputManager.addMapping("Climb", new KeyTrigger(KeyInput.KEY_C));
		inputManager.addListener(keyboardListener, "Climb");

		inputManager.addMapping("Cycle", new KeyTrigger(KeyInput.KEY_Y));
		inputManager.addListener(keyboardListener, "Cycle");

		inputManager.addMapping("Yes", new KeyTrigger(KeyInput.KEY_Z));
		inputManager.addListener(keyboardListener, "Yes");

		inputManager.addMapping("Mode", new KeyTrigger(KeyInput.KEY_O));
		inputManager.addListener(keyboardListener, "Mode");

		inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_H));
		inputManager.addListener(keyboardListener, "Shoot");

		inputManager.addMapping("NextPhase", new KeyTrigger(KeyInput.KEY_N));
		inputManager.addListener(keyboardListener, "NextPhase");

		inputManager.addMapping("No", new KeyTrigger(KeyInput.KEY_N));
		inputManager.addListener(keyboardListener, "No");

		inputManager.addMapping("EndTurn", new KeyTrigger(KeyInput.KEY_E));
		inputManager.addListener(keyboardListener, "EndTurn");
		
		inputManager.addMapping("SkipBuilding", new KeyTrigger(KeyInput.KEY_K));
		inputManager.addListener(keyboardListener, "SkipBuilding");
	}
	
	protected void removeMappings() {
		inputManager.removeListener(mouseListener);
		inputManager.removeListener(keyboardListener);
	}
}
