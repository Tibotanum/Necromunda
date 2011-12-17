package necromunda;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;

public class Ladder {
	private Ladder peer;
	private Node lineNode;
	private Geometry lineGeometry;
	private Line lineShape;
	
	public Ladder(Vector3f origin, Material material) {
		lineNode = new Node("lineNode");
		lineNode.setLocalTranslation(origin);
		lineShape = new Line(Vector3f.ZERO, Vector3f.UNIT_Y);
		lineGeometry = new Geometry("line", lineShape);
		lineGeometry.setMaterial(material);
		//lineNode.attachChild(lineGeometry);
	}

	public Ladder getPeer() {
		return peer;
	}

	public void setPeer(Ladder peer) {
		this.peer = peer;
	}
	
	public Vector3f getWorldStart() {
		return lineNode.getWorldTranslation().add(lineShape.getStart());
	}
	
	public Vector3f getWorldEnd() {
		return lineNode.getWorldTranslation().add(lineShape.getEnd());
	}
	
	public float distance(Vector3f point) {
		necromunda.Line line = new necromunda.Line(getWorldStart(), getWorldEnd());
		return line.distance(point);
	}

	public Node getLineNode() {
		return lineNode;
	}
}