package necromunda;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.renderer.Camera;

public class InvertedFlyByCamera extends FlyByCamera {
	private static String[] mappings = new String[]{
        "FLYCAM_Left",
        "FLYCAM_Right",
        "FLYCAM_Up",
        "FLYCAM_Down",

        "FLYCAM_StrafeLeft",
        "FLYCAM_StrafeRight",
        "FLYCAM_Forward",
        "FLYCAM_Backward",

        "FLYCAM_ZoomIn",
        "FLYCAM_ZoomOut",
        "FLYCAM_RotateDrag",

        "FLYCAM_Rise",
        "FLYCAM_Lower"
    };
	
	public InvertedFlyByCamera(Camera camera) {
		super(camera);
	}

	@Override
	public void registerWithInput(InputManager inputManager) {
		super.registerWithInput(inputManager);

		inputManager.deleteMapping("FLYCAM_Up");
		inputManager.deleteMapping("FLYCAM_Down");

		inputManager.addMapping("FLYCAM_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true), new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addMapping("FLYCAM_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false), new KeyTrigger(KeyInput.KEY_UP));
		
		inputManager.removeListener(this);
		inputManager.addListener(this, mappings);

		Joystick[] joysticks = inputManager.getJoysticks();

        if (joysticks != null && joysticks.length > 0){
            Joystick joystick = joysticks[0];
            joystick.assignAxis("FLYCAM_Up", "FLYCAM_Down", joystick.getYAxisIndex());
        }
	}
}
