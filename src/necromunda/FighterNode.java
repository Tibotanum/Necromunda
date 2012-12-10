package necromunda;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import necromunda.MaterialFactory.MaterialIdentifier;

import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.*;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

public class FighterNode extends NecromundaNode {
	
	private final static float SYMBOL_HEIGHT = 2;
	private Fighter fighter;
	
	public FighterNode(String name, Fighter fighter, MaterialFactory materialFactory, BaseFactory baseFactory) {
		super(name);
		this.fighter = fighter;

		Geometry roundBase = baseFactory.createRoundBase(fighter.getBaseRadius(), new ColorRGBA(64 / 255f, 147 / 255f, 91 / 255f, 1.0f), "Images/Textures/Base/BaseGrass.png");

		Material figureMaterial = materialFactory.createFigureMaterial(fighter);

		Image modelImage = figureMaterial.getTextureParam("ColorMap").getTextureValue().getImage();

		BasedModelImage basedModelImage = fighter.getFighterImage();
		
		float upperBaseRadius = baseFactory.calculateUpperBaseRadius(fighter.getBaseRadius());
		float pixelsPerInch = basedModelImage.getBaseWidth() / (upperBaseRadius * 2);
		float imageHeightInInch = modelImage.getHeight() / pixelsPerInch;
		float xOffsetLeft = -upperBaseRadius - (basedModelImage.getOffset() / pixelsPerInch);
		float xOffsetRight = upperBaseRadius
				+ ((modelImage.getWidth() - basedModelImage.getOffset() - basedModelImage.getBaseWidth()) / pixelsPerInch);

		Quad quad = new Quad(xOffsetRight - xOffsetLeft, imageHeightInInch);
		Geometry figure = new Geometry("figure", quad);
		figure.setMaterial(figureMaterial);
		figure.move(xOffsetLeft, BaseFactory.BASE_HEIGHT, 0);
		figure.setQueueBucket(Bucket.Translucent);
		figure.setShadowMode(ShadowMode.Off);

		CustomBillboardControl billboardControl = new CustomBillboardControl();
		billboardControl.setAlignment(BillboardControl.Alignment.AxialY);

		Node figureNode = new Node("figureNode");
		figureNode.attachChild(figure);
		figureNode.addControl(billboardControl);

		attachChild(roundBase);
		attachChild(figureNode);
		
		Cylinder boundingVolume = new Cylinder(4, 10, fighter.getBaseRadius(), fighter.getBaseRadius() * 1.5f * 2, true, false);
		Geometry boundingVolumeGeometry = new Geometry("boundingVolume", boundingVolume);
		boundingVolumeGeometry.setMaterial(materialFactory.createColourMaterial(ColorRGBA.Red));
		boundingVolumeGeometry.setCullHint(CullHint.Always);
		boundingVolumeGeometry.setLocalTranslation(0, fighter.getBaseRadius() * 1.5f, 0);
		boundingVolumeGeometry.rotate(FastMath.HALF_PI, 0, 0);
		attachChild(boundingVolumeGeometry);
	}
	
	public void addSymbol(Material material) {
		Quad quad = new Quad(0.3f, 0.3f);
		
		Geometry symbol = new Geometry("symbol", quad);
		symbol.setMaterial(material);
		symbol.setShadowMode(ShadowMode.Off);
		
		Node symbolNode = (Node)getChild("figureNode");
		symbolNode.attachChild(symbol);
		
		List<Geometry> symbolNodes = getSymbols();
		
		int numberOfSymbols = symbolNodes.size();
		
		for (int i = 0; i < numberOfSymbols; i++) {
			Geometry childSymbolNode = symbolNodes.get(i);
			childSymbolNode.setLocalTranslation(quad.getWidth() * i - (numberOfSymbols * quad.getWidth() / 2), SYMBOL_HEIGHT, 0);
		}
	}
	
	private List<Geometry> getSymbols() {
		List<Geometry> symbolNodes = new ArrayList<Geometry>();
		
		Node figureNode = (Node)getChild("figureNode");
		List<Spatial> children = figureNode.getChildren();
		
		for (Spatial child : children) {
			if (child.getName().equals("symbol")) {
				symbolNodes.add((Geometry)child);
			}
		}
		
		return symbolNodes;
	}
	
	public List<Vector3f> getCollisionShapePointCloud() {
		List<Vector3f> pointCloud = new ArrayList<Vector3f>();
		
		float angleAddition = FastMath.TWO_PI / 8;
		float[] fighterBaseRadiusFragments = new float[5];
		
		for (int i = 0; i <= 4; i++) {
			fighterBaseRadiusFragments[i] = fighter.getBaseRadius() / 4 * i;
		}
		
		List<Vector3f> templateSlice = new ArrayList<Vector3f>();
		
		for (float angle = 0; angle < FastMath.TWO_PI; angle += angleAddition) {
			for (int i = 0; i <= 4; i++) {
				float radius = fighterBaseRadiusFragments[i];
				
				float x = FastMath.cos(angle) * radius;
				float z = FastMath.sin(angle) * radius;
				
				templateSlice.add(new Vector3f(x, 0, z));
			}
		}
		
		float yOffset = fighter.getBaseRadius() * 1.5f;
		float yOffsetFragment = yOffset * 2 / 8;
		
		for (int i = 0; i <= 8; i++) {
			List<Vector3f> slice = new ArrayList<Vector3f>();
			
			float tempYoffset = yOffset - yOffsetFragment * i;
			
			for (Vector3f vector : templateSlice) {
				Vector3f clone = vector.clone();
				clone.setY(tempYoffset);
				slice.add(clone);
			}
			
			pointCloud.addAll(slice);
		}
		
		// Add Node world offset
		for (Vector3f vector : pointCloud) {
			vector.addLocal(getBoundingVolume().getWorldTranslation());
		}
		
		return pointCloud;
	}
	
	public Geometry getBoundingVolume() {
		return (Geometry)getChild("boundingVolume");
	}
	
	public void setBaseMaterial(Material baseMaterial) {
		Spatial base = getChild("base");
		base.setMaterial(baseMaterial);
	}
	
	public Vector3f getLocalHeadPosition() {
		Vector3f localHeadPosition = getLocalCenter().add(getCenterToHeadVector());
		return localHeadPosition;
	}
	
	public Vector3f getLocalCenter() {
		return getBoundingVolume().getLocalTranslation().clone();
	}
	
	public Vector3f getCenterToHeadVector() {
		return new Vector3f(0, fighter.getBaseRadius() * 1.0f, 0);
	}

	public Fighter getFighter() {
		return fighter;
	}

	public void setFighter(Fighter fighter) {
		this.fighter = fighter;
	}
	
	@Override
	public List<Spatial> getVisualSpatials() {
		List<Spatial> spatials = new ArrayList<Spatial>();
		spatials.add(getChild("base"));
		spatials.add(getChild("figureNode"));
		return spatials;
	}

	public List<Geometry> getBoundingVolumes() {
		List<Geometry> boundingVolumes = new ArrayList<Geometry>();
		boundingVolumes.add(getBoundingVolume());
		return boundingVolumes;
	}
}
