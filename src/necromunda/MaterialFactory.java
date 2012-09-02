package necromunda;

import java.util.HashMap;
import java.util.Map;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.plugins.NeoTextureMaterialKey;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class MaterialFactory {
	public enum MaterialIdentifier {
		TARGET_LINE,
	}
	
	private Map<String, Material> materialCache;
	
	private AssetManager assetManager;
	private Map<Enum<MaterialIdentifier>, Material> materialMap = new HashMap<Enum<MaterialIdentifier>, Material>();
	
	public MaterialFactory(AssetManager assetManager, Necromunda3dProvider necromunda3dProvider) {
		materialCache = new HashMap<String, Material>();
		
		this.assetManager = assetManager;
		
		Material targetLineMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		targetLineMaterial.setColor("Color", new ColorRGBA(ColorRGBA.Red));
		targetLineMaterial.setColor("GlowColor", new ColorRGBA(ColorRGBA.Red));

		materialMap.put(MaterialIdentifier.TARGET_LINE, targetLineMaterial);
	}
	
	public Material createMaterial(Enum<MaterialIdentifier> identifier) {
		return materialMap.get(identifier);
	}
	
	public Material createBuildingMaterial(String identifier) {
		Material buildingMaterial = materialCache.get(identifier);
		
		if (buildingMaterial == null) {
			buildingMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
			buildingMaterial.setFloat("Shininess", 5f);
			TextureKey key = new TextureKey("Images/Textures/Buildings/" + identifier + ".png", false);
			Texture modelTexture = assetManager.loadTexture(key);
			modelTexture.setWrap(WrapMode.Repeat);
			buildingMaterial.setTexture("DiffuseMap", modelTexture);
			
			materialCache.put(identifier, buildingMaterial);
		}

		return buildingMaterial;
	}
	
	public Material createFigureMaterial(Fighter fighter) {
		BasedModelImage basedModelImage = fighter.getFighterImage();
		Material figureMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture modelTexture = assetManager.loadTexture(basedModelImage.getRelativeImageFileName());
		figureMaterial.setTexture("ColorMap", modelTexture);
		figureMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		return figureMaterial;
	}
	
	public Material createTextureMaterial(String filename, ColorRGBA colour) {
		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		Texture modelTexture = assetManager.loadTexture(filename);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("Diffuse", colour);
		material.setTexture("DiffuseMap", modelTexture);
		material.setFloat("Shininess", 128f);
		
		return material;
	}
	
	public Material createSymbolMaterial(String filename) {
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture texture = assetManager.loadTexture(filename);
		material.setTexture("ColorMap", texture);
		
		return material;
	}
	
	public Material createColourMaterial(ColorRGBA colour) {
		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		material.setFloat("Shininess", 5f);
		material.setColor("Diffuse", colour);
		material.setBoolean("UseMaterialColors", true);
		
		return material;
	}
	
	public Material createTransparentColourMaterial(ColorRGBA colour, float alpha) {
		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		material.setFloat("Shininess", 5f);
		ColorRGBA transparentColour = new ColorRGBA(colour);
		transparentColour.a = alpha;
		material.setColor("Diffuse", transparentColour);
		material.setBoolean("UseMaterialColors", true);
		material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		return material;
	}
	
	public Material createNeoTextureMaterial(String materialIdentifier) {
		NeoTextureMaterialKey key = new NeoTextureMaterialKey(materialIdentifier);
		Material material = assetManager.loadAsset(key);
		material.setFloat("Shininess", 12f);
		
		return material;
	}
}
