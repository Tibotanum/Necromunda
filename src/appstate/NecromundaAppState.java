package appstate;

import necromunda.Necromunda3dProvider;
import necromunda.Necromunda3dProvider.*;

import com.jme3.app.Application;
import com.jme3.app.state.*;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.renderer.RenderManager;

public abstract class NecromundaAppState implements AppState {
	private boolean initialised;
	private boolean enabled;
	
	protected MouseListener mouseListener;
	protected KeyboardListener keyboardListener;
	private Application application;
	
	public void initialize(AppStateManager stateManager, Application app) {
		this.application = app;
	}

	public NecromundaAppState(MouseListener mouseListener, KeyboardListener keyboardListener) {
		this.mouseListener = mouseListener;
		this.keyboardListener = keyboardListener;
	}

	public Application getApplication() {
		return application;
	}

	public boolean isInitialized() {
		return initialised;
	}

	public void setEnabled(boolean active) {
		this.enabled = active;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void stateAttached(AppStateManager stateManager) {
	}

	public void stateDetached(AppStateManager stateManager) {
	}

	public void update(float tpf) {
	}

	public void render(RenderManager rm) {
	}

	public void postRender() {
	}

	public void cleanup() {	
	}
}
