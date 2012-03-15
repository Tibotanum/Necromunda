package necromunda;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.FlyByCamera;

public class InvertedFlyCamAppState extends AbstractAppState {
	private FlyByCamera flyCam;

	void setCamera(FlyByCamera cam) {
		this.flyCam = cam;
	}

	public FlyByCamera getCamera() {
		return flyCam;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		if (app.getInputManager() != null) {

			if (flyCam == null) {
				flyCam = new InvertedFlyByCamera(app.getCamera());
			}

			flyCam.registerWithInput(app.getInputManager());
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		flyCam.setEnabled(enabled);
	}

	@Override
	public void cleanup() {
		super.cleanup();

		flyCam.unregisterInput();
	}
}
