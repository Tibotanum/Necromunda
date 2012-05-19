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
		NORMAL,
		SELECTED,
		TARGETED,
		VALID_PATH,
		INVALID_PATH,
		TARGET_LINE,
		SYMBOL_PINNED,
		SYMBOL_DOWN,
		SYMBOL_SEDATED,
		SYMBOL_COMATOSE,
		SYMBOL_WEBBED,
		SYMBOL_HIDDEN,
		SYMBOL_LADDER,
		TABLE;
	}
	
	private Map<String, Material> materialCache;
	
	private AssetManager assetManager;
	private Map<Enum<MaterialIdentifier>, Material> materialMap = new HashMap<Enum<MaterialIdentifier>, Material>();
	
	public MaterialFactory(AssetManager assetManager, Necromunda3dProvider necromunda3dProvider) {
		materialCache = new HashMap<String, Material>();
		
		this.assetManager = assetManager;
		
		Material baseMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		baseMaterial.setColor("Specular", ColorRGBA.White);
		baseMaterial.setColor("Diffuse", new ColorRGBA(64 / 255f, 147 / 255f, 91 / 255f, 1.0f));
		baseMaterial.setBoolean("UseMaterialColors", true);
		
		baseMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		Texture modelTexture = assetManager.loadTexture("Images/Textures/Base/BaseGrass.png");
		baseMaterial.setColor("Specular", ColorRGBA.White);
		baseMaterial.setColor("Diffuse", new ColorRGBA(64 / 255f, 147 / 255f, 91 / 255f, 1.0f));
		baseMaterial.setTexture("DiffuseMap", modelTexture);
		baseMaterial.setFloat("Shininess", 128f);
		
		Material selectedBaseMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		selectedBaseMaterial.setFloat("Shininess", 5f);
		selectedBaseMaterial.setColor("Diffuse", new ColorRGBA(ColorRGBA.Red));
		selectedBaseMaterial.setBoolean("UseMaterialColors", true);

		Material targetedMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		targetedMaterial.setFloat("Shininess", 5f);
		targetedMaterial.setColor("Diffuse", new ColorRGBA(ColorRGBA.Yellow));
		targetedMaterial.setBoolean("UseMaterialColors", true);

		Material validPathMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		validPathMaterial.setFloat("Shininess", 5f);
		validPathMaterial.setColor("Diffuse", new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
		validPathMaterial.setBoolean("UseMaterialColors", true);
		validPathMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		Material invalidPathMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		invalidPathMaterial.setFloat("Shininess", 5f);
		ColorRGBA invalidPathColor = new ColorRGBA(ColorRGBA.Red);
		invalidPathColor.a = 0.5f;
		invalidPathMaterial.setColor("Diffuse", invalidPathColor);
		invalidPathMaterial.setBoolean("UseMaterialColors", true);
		invalidPathMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		Material targetLineMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		targetLineMaterial.setColor("Color", new ColorRGBA(ColorRGBA.Red));
		targetLineMaterial.setColor("GlowColor", new ColorRGBA(ColorRGBA.Red));
		
		String basePath = "Images/Textures/OverheadSymbols/";

		Material pinnedSymbolMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture pinnedSymbolTexture = assetManager.loadTexture(basePath + "Pinned.PNG");
		pinnedSymbolMaterial.setTexture("ColorMap", pinnedSymbolTexture);
		
		Material downSymbolMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture downSymbolTexture = assetManager.loadTexture(basePath + "Down.PNG");
		downSymbolMaterial.setTexture("ColorMap", downSymbolTexture);
		
		Material sedatedSymbolMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture sedatedSymbolTexture = assetManager.loadTexture(basePath + "Sedated.PNG");
		sedatedSymbolMaterial.setTexture("ColorMap", sedatedSymbolTexture);
		
		Material comatoseSymbolMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture comatoseSymbolTexture = assetManager.loadTexture(basePath + "Comatose.PNG");
		comatoseSymbolMaterial.setTexture("ColorMap", comatoseSymbolTexture);
		
		Material webbedSymbolMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture webbedSymbolTexture = assetManager.loadTexture(basePath + "Webbed.PNG");
		webbedSymbolMaterial.setTexture("ColorMap", webbedSymbolTexture);
		
		Material hiddenSymbolMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture hiddenSymbolTexture = assetManager.loadTexture(basePath + "Hidden.PNG");
		hiddenSymbolMaterial.setTexture("ColorMap", hiddenSymbolTexture);
		
		Material ladderSymbolMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture ladderSymbolTexture = assetManager.loadTexture(basePath + "Ladder.PNG");
		ladderSymbolMaterial.setTexture("ColorMap", ladderSymbolTexture);
		
		NeoTextureMaterialKey key = new NeoTextureMaterialKey("Images/Textures/Table/" + necromunda3dProvider.getTerrainType());
		Material tableMaterial = assetManager.loadAsset(key);
		tableMaterial.setFloat("Shininess", 12f);
		
		materialMap.put(MaterialIdentifier.NORMAL, baseMaterial);
		materialMap.put(MaterialIdentifier.SELECTED, selectedBaseMaterial);
		materialMap.put(MaterialIdentifier.TARGETED, targetedMaterial);
		materialMap.put(MaterialIdentifier.VALID_PATH, validPathMaterial);
		materialMap.put(MaterialIdentifier.INVALID_PATH, invalidPathMaterial);
		materialMap.put(MaterialIdentifier.TARGET_LINE, targetLineMaterial);
		materialMap.put(MaterialIdentifier.SYMBOL_PINNED, pinnedSymbolMaterial);
		materialMap.put(MaterialIdentifier.SYMBOL_DOWN, downSymbolMaterial);
		materialMap.put(MaterialIdentifier.SYMBOL_SEDATED, sedatedSymbolMaterial);
		materialMap.put(MaterialIdentifier.SYMBOL_COMATOSE, comatoseSymbolMaterial);
		materialMap.put(MaterialIdentifier.SYMBOL_WEBBED, webbedSymbolMaterial);
		materialMap.put(MaterialIdentifier.SYMBOL_HIDDEN, hiddenSymbolMaterial);
		materialMap.put(MaterialIdentifier.SYMBOL_LADDER, ladderSymbolMaterial);
		materialMap.put(MaterialIdentifier.TABLE, tableMaterial);
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
}
