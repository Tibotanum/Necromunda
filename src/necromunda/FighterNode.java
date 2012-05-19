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
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
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

public class FighterNode extends Node {
	public static final float BASE_HEIGHT = 17f / 132f;
	public static final float TOP_BOTTOM_RADIUS_DIFFERENCE = 10f / 132f / 2f;
	private final static float SYMBOL_HEIGHT = 2;
	private Fighter fighter;
	private boolean positionValid;
	
	public FighterNode(String name, Fighter fighter, MaterialFactory materialFactory) {
		super(name);
		this.fighter = fighter;

		float upperBaseRadius = fighter.getBaseRadius() - TOP_BOTTOM_RADIUS_DIFFERENCE;
		int radialSamples = 20;

		Cylinder cylinder = new Cylinder(4, radialSamples, fighter.getBaseRadius(), upperBaseRadius, BASE_HEIGHT, true, false);

		FloatBuffer texBuffer = cylinder.getFloatBuffer(VertexBuffer.Type.TexCoord);
		texBuffer.rewind();

		float[] array = new float[texBuffer.capacity()];
		Arrays.fill(array, -10f);
		texBuffer.put(array);
		texBuffer.position(texBuffer.capacity() - 4);
		texBuffer.put(0.0f + 0.5f).put(0.0f + 0.5f);

		FloatBuffer posBuffer = cylinder.getFloatBuffer(VertexBuffer.Type.Position);

		posBuffer.rewind();
		texBuffer.rewind();

		for (int i = 0; i < radialSamples + 1; i++) {
			texBuffer.put(posBuffer.get() + 0.5f).put(posBuffer.get() + 0.5f);
			posBuffer.get();
		}

		Geometry base = new Geometry("base", cylinder);
		base.setMaterial(materialFactory.createMaterial(MaterialIdentifier.NORMAL));
		base.rotate(FastMath.HALF_PI, 0, 0);
		base.move(0, BASE_HEIGHT / 2, 0);

		Material figureMaterial = materialFactory.createFigureMaterial(fighter);

		Image modelImage = figureMaterial.getTextureParam("ColorMap").getTextureValue().getImage();

		BasedModelImage basedModelImage = fighter.getFighterImage();
		
		float pixelsPerInch = basedModelImage.getBaseWidth() / (upperBaseRadius * 2);
		float imageHeightInInch = modelImage.getHeight() / pixelsPerInch;
		float xOffsetLeft = -upperBaseRadius - (basedModelImage.getOffset() / pixelsPerInch);
		float xOffsetRight = upperBaseRadius
				+ ((modelImage.getWidth() - basedModelImage.getOffset() - basedModelImage.getBaseWidth()) / pixelsPerInch);

		Quad quad = new Quad(xOffsetRight - xOffsetLeft, imageHeightInInch);
		Geometry figure = new Geometry("figure", quad);
		figure.setMaterial(figureMaterial);
		figure.move(xOffsetLeft, BASE_HEIGHT, 0);
		figure.setQueueBucket(Bucket.Translucent);
		figure.setShadowMode(ShadowMode.Off);

		CustomBillboardControl billboardControl = new CustomBillboardControl();
		billboardControl.setAlignment(BillboardControl.Alignment.AxialY);

		Node figureNode = new Node("figureNode");
		figureNode.attachChild(figure);
		figureNode.addControl(billboardControl);

		attachChild(base);
		attachChild(figureNode);

		MyBox boundingBox = new MyBox(fighter.getBaseRadius(), fighter.getBaseRadius() * 1.5f, fighter.getBaseRadius());
		Geometry boundingVolumeGeometry = new Geometry("boundingVolume", boundingBox);
		boundingVolumeGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.SELECTED));
		boundingVolumeGeometry.setCullHint(CullHint.Always);
		boundingVolumeGeometry.setLocalTranslation(0, fighter.getBaseRadius() * 1.5f, 0);
		attachChild(boundingVolumeGeometry);
	}
	
	public void attachSymbol(Material material) {
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

	@Override
	public void setLocalTranslation(Vector3f localTranslation) {
		super.setLocalTranslation(localTranslation);
		positionValid = true;
	}

	public Fighter getFighter() {
		return fighter;
	}

	public void setFighter(Fighter fighter) {
		this.fighter = fighter;
	}

	public boolean isPositionValid() {
		return positionValid;
	}
	
	public void setPositionValid(boolean positionValid) {
		this.positionValid = positionValid;
	}
}
