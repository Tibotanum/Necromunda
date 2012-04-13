package necromunda;

import necromunda.Necromunda3dProvider.*;

import com.jme3.input.*;
import com.jme3.input.controls.*;

public class RerollAppState extends NecromundaAppState {
	public RerollAppState(InputManager inputManager, MouseListener mouseListener, KeyboardListener keyboardListener) {
		super(inputManager, mouseListener, keyboardListener);
	}

	@Override
	protected void addMappings() {
		inputManager.addMapping("Yes", new KeyTrigger(KeyInput.KEY_Y));
		inputManager.addListener(keyboardListener, "Yes");

		inputManager.addMapping("No", new KeyTrigger(KeyInput.KEY_N));
		inputManager.addListener(keyboardListener, "No");
	}

	@Override
	protected void removeMappings() {
		inputManager.removeListener(mouseListener);
		inputManager.removeListener(keyboardListener);
	}
}
