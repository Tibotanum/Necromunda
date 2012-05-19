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
import com.jme3.scene.shape.*;

public class TemplateNode extends Node {
	private static float TRAIL_STEPWIDTH = 1.0f;
	
	private Ammunition ammunition;
	private List<Geometry> spheres;
	private List<Geometry> boundingVolumes;
	private ColorRGBA color;
	
	private TemplateNode(String name, Ammunition ammunition) {
		super(name);
		this.ammunition = ammunition;
		spheres = new ArrayList<Geometry>();
		boundingVolumes = new ArrayList<Geometry>();
	}
	
	public static TemplateNode createTemplateNode(AssetManager assetManager, Ammunition ammunition) {
		TemplateNode templateNode = new TemplateNode("currentTemplateNode", ammunition);
		
		List<Geometry> spheres = new ArrayList<Geometry>();
		List<Geometry> boundingBoxes = new ArrayList<Geometry>();

		if (ammunition.getTemplateLength() == 0) {
			Sphere sphere = new Sphere(10, 10, ammunition.getTemplateRadius());
			Geometry geometry = new Geometry("sphere", sphere);
			spheres.add(geometry);
			
			Box boundingBox = new Box(ammunition.getTemplateRadius(), ammunition.getTemplateRadius(), ammunition.getTemplateRadius());
			Geometry boundingBoxGeometry = new Geometry("boundingBox", boundingBox);
			boundingBoxes.add(boundingBoxGeometry);
		}
		else {
			float radiusToLengthRatio = ammunition.getTemplateRadius() / ammunition.getTemplateLength();
			
			for (int i = 1; i <= ammunition.getTemplateLength(); i++) {
				float currentRadiusToLengthRatio = radiusToLengthRatio * i;
				
				Vector3f vector = Vector3f.UNIT_X.mult(i);
				
				Sphere sphere = new Sphere(10, 10, currentRadiusToLengthRatio);
				Geometry geometry = new Geometry("sphere", sphere);
				spheres.add(geometry);
				geometry.setLocalTranslation(vector);
				
				MyBox boundingBox = new MyBox(currentRadiusToLengthRatio, currentRadiusToLengthRatio, currentRadiusToLengthRatio);
				Geometry boundingBoxGeometry = new Geometry("boundingBox", boundingBox);
				boundingBoxes.add(boundingBoxGeometry);
				boundingBoxGeometry.setLocalTranslation(vector);
			}
		}
		
		Material material = createTemplateMaterial(assetManager, ammunition.getTemplateColor());
		
		for (Geometry geometry : spheres) {
			geometry.setMaterial(material);
			geometry.setQueueBucket(Bucket.Transparent);
			templateNode.attachChild(geometry);
		}
		
		for (Geometry geometry : boundingBoxes) {
			geometry.setMaterial(material);
			geometry.setQueueBucket(Bucket.Transparent);
			//geometry.setCullHint(CullHint.Always);
			templateNode.attachChild(geometry);
		}
		
		templateNode.setSpheres(spheres);
		templateNode.setBoundingVolumes(boundingBoxes);
		
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
		ScatterDiceRollResult scatterDiceRollResult = Utils.rollScatterDice();

		if ((scatterDistance == 0) && (scatterDiceRollResult.isHit())) {
			ammunition.explode();
		}
		else if (scatterDistance == 0) {
			Necromunda.appendToStatusMessage("Shot has no effect.");
			hasEffect = false;
		}
		else {
			scatterDistance = ammunition.getEffectiveScatterDistance(shotDistance, scatterDistance);
			
			Necromunda.appendToStatusMessage(String.format("Shot scatters by %s.", scatterDistance));

			moveAndCollide(scatterDistance, scatterDiceRollResult.getAngle(), collidables);
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
			
			Sphere sphere = new Sphere(10, 10, radius);
			Geometry geometry = new Geometry("sphere", sphere);
			spheres.add(geometry);
			geometry.setLocalTranslation(tempVector);
			
			MyBox boundingBox = new MyBox(radius, radius, radius);
			Geometry boundingBoxGeometry = new Geometry("boundingBox", boundingBox);
			boundingVolumes.add(boundingBoxGeometry);
			boundingBoxGeometry.setLocalTranslation(tempVector);
		}
		
		updateGeometry();
	}
	
	public void removeTrail() {
		Geometry firstSphere = spheres.get(0);
		spheres.clear();
		spheres.add(firstSphere);
		
		Geometry firstBoundingBox = boundingVolumes.get(0);
		boundingVolumes.clear();
		boundingVolumes.add(firstBoundingBox);
		
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
		
		for (Geometry geometry : boundingVolumes) {
			geometry.setMaterial(material);
			geometry.setQueueBucket(Bucket.Transparent);
			//geometry.setCullHint(CullHint.Always);
			attachChild(geometry);
		}
		
		setTransformRefresh();
	}

	public List<Geometry> getSpheres() {
		return spheres;
	}

	public void setSpheres(List<Geometry> spheres) {
		this.spheres = spheres;
	}

	public List<Geometry> getBoundingVolumes() {
		return boundingVolumes;
	}

	public void setBoundingVolumes(List<Geometry> boundingVolumes) {
		this.boundingVolumes = boundingVolumes;
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
