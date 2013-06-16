package necromunda;

import java.awt.Color;
import java.util.*;


import ammunitions.Ammunition;

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
import com.jme3.scene.*;
import com.jme3.scene.shape.*;

public class TemplateNode extends NecromundaNode {
	private Ammunition ammunition;
	public List<Geometry> spheres = new ArrayList<Geometry>();
	public List<Geometry> boundingVolumes = new ArrayList<Geometry>();
	private ColorRGBA color;
	
	private TemplateNode(String name, Ammunition ammunition) {
		super(name);
		this.ammunition = ammunition;
	}
	
	public static TemplateNode createTemplateNode(AssetManager assetManager, FighterNode fighterNode, Ammunition ammunition) {
		TemplateNode templateNode = new TemplateNode("templateNode", ammunition);
		Material material = createTemplateMaterial(assetManager, ammunition.getTemplateColor());

		if (ammunition.getTemplateLength() == 0) {
			Sphere sphere = new Sphere(10, 10, ammunition.getTemplateRadius());
			Geometry geometry = new Geometry("sphere", sphere);
			geometry.setMaterial(material);
			//geometry.setCullHint(CullHint.Always);
			geometry.setQueueBucket(Bucket.Transparent);
			templateNode.attachChild(geometry);
			templateNode.spheres.add(geometry);
			
			Sphere boundingVolume = new Sphere(4, 10, ammunition.getTemplateRadius());
			Geometry boundingVolumeGeometry = new Geometry("boundingVolume", boundingVolume);			
			boundingVolumeGeometry.setMaterial(material);
			boundingVolumeGeometry.setQueueBucket(Bucket.Transparent);
			boundingVolumeGeometry.setCullHint(CullHint.Always);
			templateNode.attachChild(boundingVolumeGeometry);
			templateNode.boundingVolumes.add(boundingVolumeGeometry);
		}
		else {
			float offset = ammunition.getTemplateLength();
			
			if (fighterNode != null) {
				offset += fighterNode.getFighter().getBaseRadius();
			}
			
			Sphere sphere = new Sphere(10, 10, ammunition.getTemplateRadius());
			List<Vector3f> vectors = Utils.getPoints(sphere);
			vectors.add(Vector3f.UNIT_X.mult(-ammunition.getTemplateLength()));
			
			Mesh mesh = Utils.convexHull(vectors);
			
			Geometry geometry = new Geometry("mesh", mesh);
			geometry.setLocalTranslation(Vector3f.UNIT_X.mult(offset));
			geometry.setMaterial(material);
			//geometry.setCullHint(CullHint.Always);
			geometry.setQueueBucket(Bucket.Transparent);
			templateNode.attachChild(geometry);
			
			Sphere sphere2 = new Sphere(4, 10, ammunition.getTemplateRadius());
			List<Vector3f> vectors2 = Utils.getPoints(sphere2);			
			vectors2.add(Vector3f.UNIT_X.mult(-ammunition.getTemplateLength()));
			
			Mesh boundingVolume = Utils.convexHull(vectors2);
			
			Geometry boundingVolumeGeometry = new Geometry("boundingVolume", boundingVolume);
			boundingVolumeGeometry.setLocalTranslation(Vector3f.UNIT_X.mult(offset));
			boundingVolumeGeometry.setMaterial(material);
			boundingVolumeGeometry.setQueueBucket(Bucket.Transparent);
			boundingVolumeGeometry.setCullHint(CullHint.Always);
			templateNode.attachChild(boundingVolumeGeometry);
		}
		
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
			Necromunda.setStatusMessage("Shot has no effect.");
			hasEffect = false;
		}
		else {
			scatterDistance = ammunition.getEffectiveScatterDistance(shotDistance, scatterDistance);
			
			Necromunda.setStatusMessage(String.format("Shot scatters by %s.", scatterDistance));

			moveAndCollide(scatterDistance, scatterDiceRollResult.getAngle(), collidables);
		}
		
		return hasEffect;
	}
	
	public void moveAndCollide(float distance, float angle, List<Collidable> collidables) {
		Quaternion q = new Quaternion();
		q.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
		Vector3f scatterDirection = q.mult(Vector3f.UNIT_X);

		CollisionResult collisionResult = Utils.getClosestCollision(getLocalTranslation(), scatterDirection, collidables);

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
			
		Sphere sphere = new Sphere(10, 10, radius);
		Geometry geometry = new Geometry("sphere", sphere);
		spheres.add(geometry);
		geometry.setLocalTranslation(nodeToEndVector);
		
		Sphere boundingVolume = new Sphere(4, 10, radius);
		Geometry boundingVolumeGeometry = new Geometry("boundingVolume", boundingVolume);
		boundingVolumes.add(boundingVolumeGeometry);
		boundingVolumeGeometry.setLocalTranslation(nodeToEndVector);
		
		updateGeometry();
	}
	
	public void removeTrail() {
		Geometry firstSphere = spheres.get(0);
		spheres.clear();
		spheres.add(firstSphere);
		
		Geometry firstBoundingVolume = boundingVolumes.get(0);
		boundingVolumes.clear();
		boundingVolumes.add(firstBoundingVolume);
		
		updateGeometry();
	}
	
	public void updateGeometry() {
		Geometry firstChild = (Geometry)getChild(0);
		Material material = firstChild.getMaterial();
		
		detachAllChildren();
		
		List<Vector3f> points = new ArrayList<Vector3f>();
		
		for (Geometry geometry : spheres) {
			points.addAll(Utils.getPoints(geometry));
		}
		
		List<Vector3f> boundingVolumePoints = new ArrayList<Vector3f>();
		
		for (Geometry geometry : boundingVolumes) {
			boundingVolumePoints.addAll(Utils.getPoints(geometry));		
		}
		
		Mesh trail = Utils.convexHull(points);
		Geometry trailGeometry = new Geometry("trail", trail);
		trailGeometry.setMaterial(material);
		trailGeometry.setQueueBucket(Bucket.Transparent);
		attachChild(trailGeometry);
		
		Mesh boundingVolumeTrail = Utils.convexHull(boundingVolumePoints);
		Geometry boundingVolumeTrailGeometry = new Geometry("boundingVolume", boundingVolumeTrail);
		boundingVolumeTrailGeometry.setMaterial(material);
		boundingVolumeTrailGeometry.setQueueBucket(Bucket.Transparent);
		boundingVolumeTrailGeometry.setCullHint(CullHint.Always);
		attachChild(boundingVolumeTrailGeometry);
		
		setTransformRefresh();
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

	@Override
	public List<Spatial> getVisualSpatials() {
		List<Spatial> spatials = new ArrayList<Spatial>();
		
		Spatial sphere = getChild("sphere");
		Spatial mesh = getChild("mesh");
		
		if (sphere != null) {
			spatials.add(sphere);
		}
		
		if (mesh != null) {
			spatials.add(mesh);
		}
		
		return spatials;
	}

	public List<Geometry> getBoundingVolumes() {
		List<Geometry> boundingVolumes = new ArrayList<Geometry>();
		
		for (Spatial spatial : getChildren()) {
			if ((spatial instanceof Geometry) && (spatial.getName().equals("boundingVolume"))) {
				Geometry boundingVolume = (Geometry)spatial;
				boundingVolumes.add(boundingVolume);
			}
		}
		
		return boundingVolumes;
	}
}
