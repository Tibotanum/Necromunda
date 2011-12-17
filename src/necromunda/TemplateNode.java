package necromunda;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import weapons.Ammunition;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

public class TemplateNode extends Node {
	private static float TRAIL_STEPWIDTH = 1.0f;
	
	private Ammunition ammunition;
	private List<BoundingSphere> boundingSpheres;
	private List<Geometry> spheres;
	private ColorRGBA color;
	
	private TemplateNode(String name, Ammunition ammunition) {
		super(name);
		this.ammunition = ammunition;
		boundingSpheres = new ArrayList<BoundingSphere>();
		spheres = new ArrayList<Geometry>();
	}
	
	public static TemplateNode createTemplateNode(AssetManager assetManager, Ammunition ammunition) {
		TemplateNode templateNode = new TemplateNode("currentTemplateNode", ammunition);
		
		List<BoundingSphere> boundingSpheres = new ArrayList<BoundingSphere>();
		List<Geometry> spheres = new ArrayList<Geometry>();

		if (ammunition.getTemplateLength() == 0) {
			BoundingSphere boundingSphere = new BoundingSphere(ammunition.getTemplateRadius(), Vector3f.ZERO);
			boundingSpheres.add(boundingSphere);
			Sphere sphere = new Sphere(10, 10, ammunition.getTemplateRadius());
			Geometry geometry = new Geometry("sphere", sphere);
			spheres.add(geometry);
		}
		else {
			float radiusToLengthRatio = ammunition.getTemplateRadius() / ammunition.getTemplateLength();
			
			for (int i = 1; i <= ammunition.getTemplateLength(); i++) {
				Vector3f vector = Vector3f.UNIT_Y.mult(i);
				BoundingSphere boundingSphere = new BoundingSphere(radiusToLengthRatio * i, vector);
				boundingSpheres.add(boundingSphere);
				Sphere sphere = new Sphere(10, 10, radiusToLengthRatio * i);
				Geometry geometry = new Geometry("sphere", sphere);
				spheres.add(geometry);
				geometry.setLocalTranslation(vector);
			}
		}
		
		Material material = createTemplateMaterial(assetManager, ammunition.getTemplateColor());
		
		for (Geometry geometry : spheres) {
			geometry.setMaterial(material);
			geometry.setQueueBucket(Bucket.Transparent);
			templateNode.attachChild(geometry);
		}
		
		templateNode.setBoundingSpheres(boundingSpheres);
		templateNode.setSpheres(spheres);
		
		return templateNode;
	}
	
	private static Material createTemplateMaterial(AssetManager assetManager, Color color) {
		ColorRGBA jmeColor = new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
		
		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		material.setFloat("Shininess", 5f);
		material.setColor("Diffuse", jmeColor);
		material.setBoolean("UseMaterialColors", true);
		material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		return material;
	}
	
	public void dealDamageTo(List<FighterNode> fighterNodes) {
		for (FighterNode fighterNode : fighterNodes) {
			Fighter fighter = fighterNode.getFighter();
			ammunition.dealDamageTo(fighter);
		}
	}
	
	public boolean scatter(float shotDistance, List<Collidable> collidables) {
		boolean hasEffect = true;
		int scatterDistance = Utils.rollArtilleryDice();
		float angle = Utils.rollScatterDice();

		if ((scatterDistance == 0) && (angle < 0)) {
			ammunition.explode();
		}
		else if (scatterDistance == 0) {
			Necromunda.appendToStatusMessage("Shot has no effect.");
			hasEffect = false;
		}
		else {
			scatterDistance = ammunition.getEffectiveScatterDistance(shotDistance, scatterDistance);
			
			Necromunda.appendToStatusMessage(String.format("Shot scatters by %s.", scatterDistance));

			moveAndCollide(scatterDistance, angle, collidables);
		}
		
		return hasEffect;
	}
	
	public void moveAndCollide(float distance, float angle, List<Collidable> collidables) {
		Quaternion q = new Quaternion();
		q.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
		Vector3f scatterDirection = q.mult(Vector3f.UNIT_X);

		CollisionResult collisionResult = Utils.getNearestCollisionFrom(getLocalTranslation(), scatterDirection, collidables);

		if (collisionResult != null) {
			float rayDistance = collisionResult.getContactPoint().distance(getLocalTranslation());

			if (rayDistance < distance) {
				scatterDirection.multLocal(rayDistance - Necromunda3dProvider.NOT_TOUCH_DISTANCE);
			}
			else {
				scatterDirection.multLocal(distance);
			}
		}
		else {
			scatterDirection.multLocal(distance);
		}

		move(scatterDirection);
	}
	
	public void attachTrail(Vector3f end) {
		removeTrail();

		float radius = ammunition.getTemplateRadius();
		
		Vector3f nodeToEndVector = end.subtract(getLocalTranslation());
		float distance = nodeToEndVector.length();
		Vector3f vector = nodeToEndVector.normalize();
		
		for (float i = distance; i > 0; i -= TRAIL_STEPWIDTH) {
			Vector3f tempVector = vector.mult(i);
			BoundingSphere boundingSphere = new BoundingSphere(radius, tempVector);
			boundingSpheres.add(boundingSphere);
			Sphere sphere = new Sphere(10, 10, radius);
			Geometry geometry = new Geometry("sphere", sphere);
			spheres.add(geometry);
			geometry.setLocalTranslation(tempVector);
		}
		
		updateGeometry();
	}
	
	public void removeTrail() {
		BoundingSphere firstBoundingSphere = boundingSpheres.get(0);
		boundingSpheres.clear();
		boundingSpheres.add(firstBoundingSphere);
		
		Geometry firstSphere = spheres.get(0);
		spheres.clear();
		spheres.add(firstSphere);
		
		updateGeometry();
	}
	
	public void updateGeometry() {
		Geometry firstChild = (Geometry)getChild(0);
		Material material = firstChild.getMaterial();
		
		detachAllChildren();
		
		for (Geometry geometry : spheres) {
			geometry.setMaterial(material);
			geometry.setQueueBucket(Bucket.Transparent);
			attachChild(geometry);
		}
		
		setTransformRefresh();
	}

	@Override
	protected void setTransformRefresh() {
		super.setTransformRefresh();
		
		for (int i = 0; i < spheres.size(); i++) {
			boundingSpheres.get(i).setCenter(spheres.get(i).getWorldTranslation());
		}
	}

	public List<BoundingSphere> getBoundingSpheres() {
		return boundingSpheres;
	}

	public void setBoundingSpheres(List<BoundingSphere> boundingSpheres) {
		this.boundingSpheres = boundingSpheres;
	}

	public List<Geometry> getSpheres() {
		return spheres;
	}

	public void setSpheres(List<Geometry> spheres) {
		this.spheres = spheres;
	}

	public ColorRGBA getColor() {
		return color;
	}

	public void setColor(ColorRGBA color) {
		this.color = color;
	}

	public Ammunition getAmmunition() {
		return ammunition;
	}
	
	public boolean isTemplatePersistent() {
		return ammunition.isTemplatePersistent();
	}
	
	public float getDriftAngle() {
		return ammunition.getDriftAngle();
	}
	
	public float getDriftDistance() {
		return ammunition.getDriftDistance();
	}
	
	public boolean isTemplateToBeRemoved() {
		return ammunition.isTemplateToBeRemoved();
	}
	
	public boolean isTemplateMoving() {
		return ammunition.isTemplateMoving();
	}
}
