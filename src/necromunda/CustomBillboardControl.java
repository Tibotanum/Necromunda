package necromunda;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.BillboardControl;

public class CustomBillboardControl extends BillboardControl {
	private Matrix3f orient;
	private Vector3f left;

	public CustomBillboardControl() {
		super();
		orient = new Matrix3f();
		left = new Vector3f();
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		Camera cam = vp.getCamera();
		rotateBillboard(cam);
	}

	private void rotateBillboard(Camera cam) {
		rotateAxial(cam, Vector3f.UNIT_Y);
	}

	private void rotateAxial(Camera camera, Vector3f axis) {
		// Compute the additional rotation required for the billboard to face
		// the camera. To do this, the camera must be inverse-transformed into
		// the model space of the billboard.

		Vector3f cameraLocation = camera.getLocation().clone();
		left.set(cameraLocation.subtractLocal(spatial.getWorldTranslation()));

		// squared length of the camera projection in the xz-plane
		float lengthSquared = left.x * left.x + left.z * left.z;
		if (lengthSquared < FastMath.FLT_EPSILON) {
			// camera on the billboard axis, rotation not defined
			return;
		}

		if (axis.y == 1) {
			left.y = 0;
			left = left.normalize();

			// compute the local orientation matrix for the billboard
			orient.set(0, 0, left.z);
			orient.set(0, 1, 0);
			orient.set(0, 2, left.x);
			orient.set(1, 0, 0);
			orient.set(1, 1, 1);
			orient.set(1, 2, 0);
			orient.set(2, 0, -left.x);
			orient.set(2, 1, 0);
			orient.set(2, 2, left.z);
		}
		else if (axis.z == 1) {
			left.z = 0;
			left = left.normalize();

			// compute the local orientation matrix for the billboard
			orient.set(0, 0, left.y);
			orient.set(0, 1, left.x);
			orient.set(0, 2, 0);
			orient.set(1, 0, -left.y);
			orient.set(1, 1, left.x);
			orient.set(1, 2, 0);
			orient.set(2, 0, 0);
			orient.set(2, 1, 0);
			orient.set(2, 2, 1);
		}

		// The billboard must be oriented to face the camera before it is
		// transformed into the world.

		spatial.setLocalRotation(orient);
		spatial.updateGeometricState();
	}

}
