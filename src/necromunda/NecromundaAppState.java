package necromunda;

import necromunda.Necromunda3dProvider.*;

import com.jme3.app.state.AbstractAppState;
import com.jme3.input.*;
import com.jme3.input.controls.*;

public abstract class NecromundaAppState extends AbstractAppState {
	protected InputManager inputManager;
	protected MouseListener mouseListener;
	protected KeyboardListener keyboardListener;
	
	public NecromundaAppState(InputManager inputManager, MouseListener mouseListener, KeyboardListener keyboardListener) {
		this.inputManager = inputManager;
		this.mouseListener = mouseListener;
		this.keyboardListener = keyboardListener;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);

		if (enabled) {
			addMappings();
		}
		else {
			removeMappings();
		}
	}
	
	protected abstract void addMappings();
	
	protected abstract void removeMappings();
}
