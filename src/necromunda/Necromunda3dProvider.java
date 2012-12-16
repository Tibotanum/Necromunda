package necromunda;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.*;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import org.lwjgl.Sys;

import necromunda.Fighter.State;
import necromunda.MaterialFactory.MaterialIdentifier;
import weapons.*;

import ammunitions.Ammunition;
import appstate.*;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.bounding.*;
import com.jme3.bullet.*;
import com.jme3.bullet.collision.*;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.control.*;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.*;
import com.jme3.font.BitmapText;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.renderer.queue.RenderQueue.*;
import com.jme3.scene.*;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.*;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Necromunda3dProvider extends SimpleApplication {
	public enum SelectionMode {
		DEPLOY_FIGHTER, DEPLOY_BUILDING, SELECT, MOVE, CLIMB, TARGET, REROLL
	}

	public static final float MAX_COLLISION_NORMAL_ANGLE = 0.05f;
	public static final float MAX_SLOPE = 0.05f;
	public static final float NOT_TOUCH_DISTANCE = 0.01f;
	public static final boolean PHYSICS_DEBUG_ENABLED = false;
	public static final Vector3f GROUND_BUFFER = new Vector3f(0, NOT_TOUCH_DISTANCE, 0);
	private Necromunda game;

	private boolean invertMouse;

	private FighterNode selectedFighterNode;

	private SelectionMode selectionMode;

	private Node buildingsNode;
	private BuildingNode selectedBuildingNode;

	private Line currentPath;
	private ClimbPath currentClimbPath;
	private PathNode currentPathNode;
	
	private TemplateNode currentTemplateNode;
	private List<NodeRemover> nodeRemovers;
	private List<Node> nodesToBeRemoved;
	private List<FighterNode> targetedFighterNodes;

	private RangeCombatWeapon currentWeapon;
	private Fighter currentTarget;

	private List<FighterNode> validSustainedFireTargetFighterNodes;

	private boolean rightButtonDown;

	private CyclicList<BuildingNode> templateBuildingNodes;
	private LadderNode currentLadder;
	private List<LadderNode> currentLadders;

	private BitmapText statusMessage;

	private MaterialFactory materialFactory;
	private BaseFactory baseFactory;

	private String terrainType;

	public Necromunda3dProvider(Necromunda game) {
		super(new StatsAppState(), new DebugKeysAppState());

		this.game = game;

		selectionMode = SelectionMode.DEPLOY_BUILDING;

		buildingsNode = new Node("buildingsNode");

		templateBuildingNodes = new CyclicList<BuildingNode>();

		nodeRemovers = new ArrayList<NodeRemover>();
		
		nodesToBeRemoved = new ArrayList<Node>();

		targetedFighterNodes = new ArrayList<FighterNode>();

		validSustainedFireTargetFighterNodes = new ArrayList<FighterNode>();

		AppSettings settings = new AppSettings(false);
		settings.setTitle("Necromunda");
		settings.setSettingsDialogImage("/Images/Application/Splashscreen.png");
		settings.setFrameRate(59);
		// settings.setSamples(4);
		// settings.setVSync(true);
		settings.setIcons(createFrameIcons());
		setSettings(settings);
	}

	private BufferedImage[] createFrameIcons() {
		List<BufferedImage> iconImages = new ArrayList<BufferedImage>();

		try {
			iconImages.add(ImageIO.read(getClass().getResource("/Images/Application/OrlockLogoTiny.png")));
			iconImages.add(ImageIO.read(getClass().getResource("/Images/Application/OrlockLogoSmall.png")));
			iconImages.add(ImageIO.read(getClass().getResource("/Images/Application/OrlockLogoMedium.png")));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return iconImages.toArray(new BufferedImage[0]);
	}

	@Override
	public void simpleInitApp() {
		Logger.getLogger("").setLevel(Level.SEVERE);

		Vector3f lightDirection = new Vector3f(-0.5f, -1.5f, -1).normalize();

		PssmShadowRenderer shadowRenderer = new PssmShadowRenderer(assetManager, 4096, 3);
		shadowRenderer.setDirection(lightDirection);
		// shadowRenderer.setCompareMode(CompareMode.Hardware);
		viewPort.addProcessor(shadowRenderer);

		/*
		 * FilterPostProcessor filterPostProcessor = new
		 * FilterPostProcessor(assetManager); SSAOFilter ssaoFilter = new
		 * SSAOFilter(); filterPostProcessor.addFilter(ssaoFilter);
		 * viewPort.addProcessor(filterPostProcessor);
		 */

		// rootNode.setShadowMode(ShadowMode.Off);

		if (invertMouse) {
			InvertedFlyByCamera camera = new InvertedFlyByCamera(cam);
			InvertedFlyCamAppState appState = new InvertedFlyCamAppState();
			appState.setCamera(camera);
			stateManager.attach(appState);
			appState.getCamera().setMoveSpeed(20f);
		}
		else {
			FlyCamAppState appState = new FlyCamAppState();
			appState.initialize(stateManager, this);
			appState.cleanup();
			stateManager.attach(appState);
			appState.getCamera().setMoveSpeed(20f);
		}

		cam.setLocation(new Vector3f(0, 20, 50));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

		ScreenshotAppState screenshotAppState = new ScreenshotAppState();
		stateManager.attach(screenshotAppState);

		assetManager.registerLocator("", ClasspathLocator.class.getName());
		assetManager.registerLoader("com.jme3.material.plugins.NeoTextureMaterialLoader", "tgr");

		materialFactory = new MaterialFactory(assetManager, this);
		baseFactory = new BaseFactory(materialFactory);

		Node tableNode = createTableNode();
		rootNode.attachChild(tableNode);

		templateBuildingNodes = createBuildings(game.getBuildings());

		Node objectsNode = new Node("objectsNode");
		objectsNode.setShadowMode(ShadowMode.CastAndReceive);

		rootNode.attachChild(objectsNode);
		buildingsNode.setShadowMode(ShadowMode.CastAndReceive);
		rootNode.attachChild(buildingsNode);

		if (game.isDebug()) {
			setDisplayFps(true);
			setDisplayStatView(true);
		}
		else {
			setDisplayFps(false);
			setDisplayStatView(false);
		}

		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(lightDirection);
		sun.setColor(ColorRGBA.White);
		rootNode.addLight(sun);

		AmbientLight ambientLight = new AmbientLight();
		ambientLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
		rootNode.addLight(ambientLight);

		initCrossHairs();
		initStatusMessage();

		TextureKey key0 = new TextureKey("Images/Textures/Sky/SkyTopBottom.png", true);
		key0.setGenerateMips(true);
		key0.setAsCube(true);
		Texture tex0 = assetManager.loadTexture(key0);

		TextureKey key1 = new TextureKey("Images/Textures/Sky/SkyLeft.png", true);
		key1.setGenerateMips(true);
		key1.setAsCube(true);
		Texture tex1 = assetManager.loadTexture(key1);

		TextureKey key2 = new TextureKey("Images/Textures/Sky/SkyRight.png", true);
		key2.setGenerateMips(true);
		key2.setAsCube(true);
		Texture tex2 = assetManager.loadTexture(key2);

		TextureKey key3 = new TextureKey("Images/Textures/Sky/SkyFront.png", true);
		key3.setGenerateMips(true);
		key3.setAsCube(true);
		Texture tex3 = assetManager.loadTexture(key3);

		TextureKey key4 = new TextureKey("Images/Textures/Sky/SkyBack.png", true);
		key4.setGenerateMips(true);
		key4.setAsCube(true);
		Texture tex4 = assetManager.loadTexture(key4);

		Geometry sky = (Geometry) SkyFactory.createSky(assetManager, tex1, tex2, tex3, tex4, tex0, tex0);
		// Work around bug which sometimes culls the skybox
		sky.setLocalScale(100);
		rootNode.attachChild(sky);

		MouseListener mouseListener = new MouseListener();
		KeyboardListener keyboardListener = new KeyboardListener();

		stateManager.attach(new DeployBuildingAppState(mouseListener, keyboardListener));
		stateManager.attach(new DeployFighterAppState(mouseListener, keyboardListener));
		stateManager.attach(new GeneralAppState(mouseListener, keyboardListener));
		stateManager.attach(new RerollAppState(mouseListener, keyboardListener));
	}

	private Node createTableNode() {
		Box box = new Box(new Vector3f(24, -0.5f, 24), 24, 0.5f, 24);
		Geometry tableGeometry = new Geometry("tableGeometry", box);
		tableGeometry.setMaterial(materialFactory.createNeoTextureMaterial("Images/Textures/Table/" + getTerrainMaterialIdentifier()));
		tableGeometry.setShadowMode(ShadowMode.Receive);

		Node tableNode = new Node("tableNode");
		tableNode.attachChild(tableGeometry);

		return tableNode;
	}

	private CyclicList<BuildingNode> createBuildings(List<Building> buildings) {
		CyclicList<BuildingNode> buildingNodes = new CyclicList<BuildingNode>();

		for (Building building : buildings) {
			BuildingNode buildingNode = createBuildingNode(building);
			
			if (game.isDebug()) {
				buildingNode.displayBoundingVolumes(true);
				buildingNode.displayVisualSpatials(false);
			}
			
			buildingNodes.add(buildingNode);
		}

		return buildingNodes;
	}

	private BuildingNode createBuildingNode(Building building) {
		BuildingNode buildingNode = new BuildingNode("buildingNode");

		for (Entry<String, String> entry : building.getEntrySet()) {
			Spatial section = createBuildingSection(entry.getKey(), entry.getValue());

			buildingNode.attachChild(section);

			List<LadderNode> ladders = LadderNode.createLadders("/Ladders/" + entry.getKey() + ".ladder");

			for (LadderNode ladder : ladders) {
				buildingNode.attachChild(ladder);
			}
		}

		Node boundsNode = new Node("bounds");

		for (String identifier : building.getBounds()) {
			Node bound = (Node) assetManager.loadModel("Models/" + identifier + ".mesh.xml");

			for (Spatial spatial : bound.getChildren()) {
				boundsNode.attachChild((Geometry) spatial);
				spatial.setMaterial(materialFactory.createTransparentColourMaterial(ColorRGBA.Blue, 1));
			}
		}
		
		buildingNode.attachChild(boundsNode);

		return buildingNode;
	}

	private Spatial createBuildingSection(String modelIdentifier, String materialIdentifier) {
		Material buildingMaterial = materialFactory.createBuildingMaterial(materialIdentifier);

		Spatial section = assetManager.loadModel("Models/" + modelIdentifier + ".mesh.xml");

		section.setMaterial(buildingMaterial);

		return section;
	}

	private void createLadderLines() {
		for (LadderNode ladder : getLadders(buildingsNode)) {
			com.jme3.scene.shape.Line lineShape = new com.jme3.scene.shape.Line(Vector3f.ZERO, Vector3f.UNIT_Y);
			Geometry lineGeometry = new Geometry("line", lineShape);
			lineGeometry.setMaterial(materialFactory.createColourMaterial(ColorRGBA.Red));
			ladder.attachChild(lineGeometry);
		}
	}

	private List<LadderNode> getLadders(Node buildingsNode) {
		List<LadderNode> ladders = new ArrayList<LadderNode>();

		for (BuildingNode buildingNode : getBuildingNodes()) {
			ladders.addAll(buildingNode.getLadderNodes());
		}

		return ladders;
	}

	@Override
	public void simpleUpdate(float tpf) {
		int millis = (int) (tpf * 1000);

		Iterator<NodeRemover> it = nodeRemovers.iterator();

		while (it.hasNext()) {
			NodeRemover nodeRemover = it.next();
			nodeRemover.reduce(millis);

			if (nodeRemover.getTimeToRemove() < 0) {
				nodeRemover.remove();
				it.remove();
			}
		}

		updateModels();
		
		if (selectionMode == SelectionMode.DEPLOY_BUILDING) {
			enableAppStateExclusively(DeployBuildingAppState.class);
		}
		else if (selectionMode == SelectionMode.DEPLOY_FIGHTER) {
			enableAppStateExclusively(DeployFighterAppState.class);
		}
		else if (selectionMode == SelectionMode.REROLL) {
			enableAppStateExclusively(RerollAppState.class);
		}
		else {
			enableAppStateExclusively(GeneralAppState.class);
		}

		if (!(selectionMode.equals(SelectionMode.DEPLOY_FIGHTER) || (selectionMode.equals(SelectionMode.DEPLOY_BUILDING)))) {
			statusMessage.setText(buildStatusText(game));
		}
		
		if (selectionMode == SelectionMode.MOVE) {
			setUpMovement();
		}
		else if (selectionMode == SelectionMode.DEPLOY_FIGHTER) {
			Vector3f nearestIntersection = getSceneryCollisionPoint();

			if (nearestIntersection == null) {
				return;
			}

			updateModelPosition(nearestIntersection);
		}
		else if ((selectionMode == SelectionMode.DEPLOY_BUILDING) && !rightButtonDown) {
			Vector3f nearestIntersection = getTableCollisionPoint();

			if (nearestIntersection == null) {
				return;
			}

			if (selectedBuildingNode == null) {
				selectedBuildingNode = templateBuildingNodes.get(0);
				buildingsNode.attachChild(selectedBuildingNode);
			}

			selectedBuildingNode.setLocalTranslation(nearestIntersection);
		}
		
		if (currentPath != null) {
			List<NecromundaNode> otherNodes = new ArrayList<NecromundaNode>(); 
			otherNodes.addAll(getFighterNodes());
			otherNodes.addAll(getBuildingNodes());
			otherNodes.remove(selectedFighterNode);
			otherNodes.remove(currentPathNode);
			
			if (!intersect(currentPathNode, otherNodes) && !intersect(selectedFighterNode, otherNodes)) {
				currentPathNode.setMaterial(materialFactory.createTransparentColourMaterial(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f), 0.5f));
			}
			else {
				currentPathNode.setMaterial(materialFactory.createTransparentColourMaterial(ColorRGBA.Red, 0.5f));
			}
		}
	}

	private void updateModels() {
		for (FighterNode fighterNode : getFighterNodes()) {
			Fighter fighter = fighterNode.getFighter();

			if (fighter.isOutOfAction()) {
				getObjectsNode().detachChild(fighterNode);
			}
			else {
				if (fighterNode == selectedFighterNode) {
					setBaseSelected(fighterNode);
				}
				else if (targetedFighterNodes.contains(fighterNode)) {
					setBaseTargeted(fighterNode);
				}
				else {
					setBaseNormal(fighterNode);
				}

				Node figureNode = (Node) fighterNode.getChild("figureNode");

				List<Spatial> children = figureNode.getChildren();

				for (Spatial spatial : children) {
					if (spatial.getName().equals("symbol")) {
						figureNode.detachChild(spatial);
					}
				}
				
				String basePath = "Images/Textures/OverheadSymbols/";

				if (fighter.isPinned()) {
					fighterNode.addSymbol(materialFactory.createSymbolMaterial(basePath + "Pinned.PNG"));
				}

				if (fighter.isDown()) {
					fighterNode.addSymbol(materialFactory.createSymbolMaterial(basePath + "Down.PNG"));
				}

				if (fighter.isSedated()) {
					fighterNode.addSymbol(materialFactory.createSymbolMaterial(basePath + "Sedated.PNG"));
				}

				if (fighter.isComatose()) {
					fighterNode.addSymbol(materialFactory.createSymbolMaterial(basePath + "Comatose.PNG"));
				}

				if (fighter.isWebbed()) {
					fighterNode.addSymbol(materialFactory.createSymbolMaterial(basePath + "Webbed.PNG"));
				}

				if (fighter.isHidden()) {
					fighterNode.addSymbol(materialFactory.createSymbolMaterial(basePath + "Hidden.PNG"));
				}

				List<LadderNode> laddersInReach = getLaddersInReach(fighterNode.getLocalTranslation(), fighter.getBaseRadius());

				if (!laddersInReach.isEmpty()) {
					fighterNode.addSymbol(materialFactory.createSymbolMaterial(basePath + "Ladder.PNG"));
				}
			}
		}

		if (currentTemplateNode != null) {
			colouriseBasesUnderTemplate(currentTemplateNode);
		}

		for (TemplateNode templateNode : getTemplateNodes()) {
			colouriseBasesUnderTemplate(templateNode);
		}
	}

	public List<FighterNode> getFighterNodes() {
		List<FighterNode> fighterNodes = new ArrayList<FighterNode>();

		for (Spatial child : getObjectsNode().getChildren()) {
			if (child instanceof FighterNode) {
				fighterNodes.add((FighterNode) child);
			}
		}

		return fighterNodes;
	}

	public List<TemplateNode> getTemplateNodes() {
		List<TemplateNode> templateNodes = new ArrayList<TemplateNode>();

		for (Spatial child : getObjectsNode().getChildren()) {
			if (child instanceof TemplateNode) {
				templateNodes.add((TemplateNode) child);
			}
		}

		return templateNodes;
	}

	private Node getObjectsNode() {
		return (Node) rootNode.getChild("objectsNode");
	}

	public Node getBuildingsNode() {
		return (Node) rootNode.getChild("buildingsNode");
	}

	private Node getTableNode() {
		return (Node) rootNode.getChild("tableNode");
	}

	public void setInvertMouse(boolean invertMouse) {
		this.invertMouse = invertMouse;
	}

	private void colouriseBasesUnderTemplate(TemplateNode templateNode) {
		List<FighterNode> fighterNodesUnderTemplate = getFighterNodesUnderTemplate(templateNode, getFighterNodes());

		for (FighterNode fighterNodeUnderTemplate : fighterNodesUnderTemplate) {
			setBaseTargeted(fighterNodeUnderTemplate);
		}
	}

	private boolean isMemberOfCurrentGang(FighterNode fighterNode) {
		return game.getCurrentGang().getGangMembers().contains(selectedFighterNode.getFighter());
	}

	private void climbLadder(LadderNode ladder) {
		Vector3f currentPathOrigin;

		if (currentPath != null) {
			currentPathOrigin = currentPath.getOrigin();
			tearDownMovement();
		}
		else {
			currentPathOrigin = currentClimbPath.getStart();
		}

		currentClimbPath = new ClimbPath(currentPathOrigin);
		Vector3f nearestLadderCollisionPoint = getLadderCollisionPoint(ladder);
		currentClimbPath.addToLength(nearestLadderCollisionPoint.distance(currentPathOrigin));
		selectedFighterNode.setLocalTranslation(getLadderCollisionPoint(ladder.getPeer()));
		currentClimbPath.addToLength(nearestLadderCollisionPoint.distance(selectedFighterNode.getLocalTranslation()));
	}

	private void executeMouseAction(String name, boolean isPressed) {
		if (name.equals("leftClick")) {
			onLeftClick(isPressed);
		}
		else if (name.equals("rightClick")) {
			onRightClick(isPressed);
		}
	}

	private void onLeftClick(boolean isPressed) {
		if (isPressed) {
			Necromunda.setStatusMessage("");

			switch (selectionMode) {
			case SELECT:
				select();
				break;
			case MOVE:
				move();
				break;
			case CLIMB:
				climb();
				break;
			case TARGET:
				target();
				break;
			case DEPLOY_BUILDING:
				deployBuilding();
				break;
			case DEPLOY_FIGHTER:
				deployFighter();
				break;
			}
		}
	}

	private void target() {
		if (getSelectedRangeCombatWeapon().getCurrentAmmunition().isTargeted()) {
			targetRegularWeapon();
		}
		else {
			targetTemplateWeapon();
		}
	}

	private void targetRegularWeapon() {
		getSelectedRangeCombatWeapon().handleShot(new ShotInfo());
	}

	public int getHitModifier(float visiblePercentage) {
		int hitModifier = 0;

		if ((visiblePercentage < 1.0) && (visiblePercentage >= 0.5)) {
			hitModifier = -1;
		}
		else if (visiblePercentage < 0.5) {
			hitModifier = -2;
		}

		return hitModifier;
	}

	public int getTargetHitRoll(Fighter fighter, RangeCombatWeapon weapon, float distance, int hitModifier) {
		int targetHitRoll = 7 - fighter.getProfile().getCurrentBallisticSkill() - weapon.getRangeModifier(distance) - hitModifier;
		return targetHitRoll;
	}

	public FighterNode getStrayShotFighterNode(FighterNode source, FighterNode target) {
		FighterNode strayShotFighterNode = null;
		List<FighterNode> strayShotCandidates = new ArrayList<FighterNode>();

		LineSegment lineSegment = new LineSegment(source.getLocalTranslation(), target.getLocalTranslation());

		for (FighterNode fighterNode : getFighterNodes()) {
			if (source.getFighter().isGangMate(fighterNode.getFighter()) && (fighterNode != source)) {
				Vector3f point = fighterNode.getLocalTranslation();
				Vector3f projectedPoint = point.project(
						lineSegment.getPositiveEnd(null).subtract(lineSegment.getNegativeEnd(null))).add(
						source.getLocalTranslation());

				if ((lineSegment.distance(projectedPoint) < 0.01f)
						&& ((lineSegment.distance(point) - fighterNode.getFighter().getBaseRadius()) <= 0.5f)) {
					strayShotCandidates.add(fighterNode);
				}
			}
		}

		if (!strayShotCandidates.isEmpty()) {
			int candidateIndex = FastMath.nextRandomInt(0, strayShotCandidates.size() - 1);
			strayShotFighterNode = strayShotCandidates.get(candidateIndex);
		}

		return strayShotFighterNode;
	}

	private void targetTemplateWeapon() {
		getSelectedRangeCombatWeapon().trigger();

		List<FighterNode> affectedFighterNodes = getFighterNodesUnderTemplate(currentTemplateNode, getFighterNodes());
		pinNormalFighters(affectedFighterNodes);
		currentTemplateNode.dealDamageTo(affectedFighterNodes);

		tearDownTargeting();

		if (getSelectionMode() != SelectionMode.REROLL) {
			setSelectionMode(SelectionMode.SELECT);
		}
	}

	private void deployBuilding() {
		Vector3f nearestIntersection = getTableCollisionPoint();

		if (nearestIntersection == null) {
			return;
		}

		BuildingNode buildingNode = selectedBuildingNode.clone(false);

		buildingNode.setLocalTranslation(nearestIntersection);

		buildingsNode.attachChild(buildingNode);
	}

	private void skipBuilding() {
		Vector3f nearestIntersection = getTableCollisionPoint();

		if (nearestIntersection == null) {
			nearestIntersection = selectedBuildingNode.getLocalTranslation();
		}

		buildingsNode.detachChild(selectedBuildingNode);

		if (!templateBuildingNodes.isEmpty()) {
			selectedBuildingNode = templateBuildingNodes.next();
		}
		
		selectedBuildingNode.setLocalTranslation(nearestIntersection);
		buildingsNode.attachChild(selectedBuildingNode);
	}
	
	private void endBuildingDeployment() {
		Vector3f nearestIntersection = getTableCollisionPoint();
		
		if (nearestIntersection == null) {
			nearestIntersection = selectedBuildingNode.getLocalTranslation();
		}
		
		buildingsNode.detachChild(selectedBuildingNode);
		
		selectedBuildingNode = null;
		
		if (game.isDebug()) {
			createLadderLines();
		}

		selectionMode = SelectionMode.DEPLOY_FIGHTER;
		createFighterNode(nearestIntersection);
	}
	
	private void skipFighter() {
		Vector3f nearestIntersection = getTableCollisionPoint();

		if (nearestIntersection == null) {
			nearestIntersection = selectedFighterNode.getLocalTranslation();
		}

		getObjectsNode().detachChild(selectedFighterNode);

		game.getDeployQueue().next();
		createFighterNode(nearestIntersection);
	}
	
	private void enableAppStateExclusively(Class<? extends AppState> appStateClass) {
		List<Class<? extends AppState>> appStateClasses = new ArrayList<Class<? extends AppState>>();
		appStateClasses.add(GeneralAppState.class);
		appStateClasses.add(RerollAppState.class);
		appStateClasses.add(DeployBuildingAppState.class);
		appStateClasses.add(DeployFighterAppState.class);
		
		AppState appStateToBeEnabled = null;
		
		for (Class<? extends AppState> currentAppStateClass : appStateClasses) {
			AppState currentAppState = stateManager.getState(currentAppStateClass);
			
			if (currentAppState != null) {
				if (currentAppState.getClass().equals(appStateClass)) {
					appStateToBeEnabled = currentAppState;
				}
				else if (currentAppState.isEnabled()) {
					currentAppState.setEnabled(false);
				}
			}
		}
		
		if ((appStateToBeEnabled != null) && !appStateToBeEnabled.isEnabled()) {
			appStateToBeEnabled.setEnabled(true);
		}
	}
	
	private void createFighterNode(Vector3f position) {
		Fighter fighter = game.getDeployQueue().current();
		
		selectedFighterNode = new FighterNode("fighterNode", fighter, materialFactory, baseFactory);
		
		if (game.isDebug()) {
			selectedFighterNode.displayVisualSpatials(false);
			selectedFighterNode.displayBoundingVolumes(true);
		}
		
		for (Weapon weapon : selectedFighterNode.getFighter().getWeapons()) {
			RangeCombatWeapon rangeCombatWeapon = (RangeCombatWeapon)weapon;
			
			for (Ammunition ammunition : rangeCombatWeapon.getAmmunitions()) {
				ammunition.getShotHandler().setProvider(this);
			}
		}
		
		getObjectsNode().attachChild(selectedFighterNode);
		selectedFighterNode.setLocalTranslation(position);
	}

	private void updateModelPosition(Vector3f position) {
		if (selectedFighterNode != null) {
			selectedFighterNode.setLocalTranslation(position);
		}
	}

	private void deployFighter() {
		Vector3f contactPoint = getSceneryCollisionPoint();

		/*
		 * List<Vector3f> pointCloud =
		 * selectedFighterNode.getCollisionShapePointCloud();
		 * 
		 * Material material =
		 * materialFactory.createMaterial(MaterialIdentifier.SELECTED);
		 * 
		 * for (Vector3f vector : pointCloud) { Quad quad = new Quad(0.01f,
		 * 0.01f); Geometry geometry = new Geometry("cloudpoint", quad);
		 * geometry.setMaterial(material); geometry.setLocalTranslation(vector);
		 * rootNode.attachChild(geometry); }
		 */

		if ((contactPoint != null) && (selectedFighterNode != null) && hasValidPosition(selectedFighterNode)) {
			game.getDeployQueue().remove(selectedFighterNode.getFighter());
			
			if (!game.getDeployQueue().isEmpty()) {
				createFighterNode(contactPoint);
			}
			else {
				game.deploymentFinished();
				selectionMode = SelectionMode.SELECT;
			}
		}
	}

	private void select() {
		selectedFighterNode = getFighterNodeUnderCursor();
	}

	private void move() {
		List<NecromundaNode> otherNodes = new ArrayList<NecromundaNode>();
		otherNodes.addAll(getFighterNodes());
		otherNodes.addAll(getBuildingNodes());
		otherNodes.remove(selectedFighterNode);
		otherNodes.remove(currentPathNode);
		
		if (!intersect(selectedFighterNode, otherNodes) && !intersect(currentPathNode, otherNodes)) {
			if (selectedFighterNode.getFighter().isGoingToRun()) {
				List<FighterNode> fighterNodes = getHostileFighterNodes(getFighterNodes());
				fighterNodes = getFighterNodesWithinDistance(selectedFighterNode, fighterNodes, Necromunda.RUN_SPOT_DISTANCE);
				fighterNodes = getVisibleFighterNodes(selectedFighterNode, fighterNodes);

				if (!fighterNodes.isEmpty()) {
					Necromunda.setStatusMessage("You cannot run so close to an enemy fighter.");
				}
				else {
					commitMovement();
				}
			}
			else {
				commitMovement();
			}
		}
	}

	private void climb() {
		if (hasValidPosition(selectedFighterNode)) {
			if (currentClimbPath.getLength() <= selectedFighterNode.getFighter().getRemainingMovement()) {
				commitClimb();
			}
			else {
				Necromunda.setStatusMessage("This ganger cannot climb that far.");
			}
		}
		else {
			System.out.println("No valid position");
		}
	}

	private void hide() {
		if (selectedFighterNode.getFighter().hasRun()) {
			Necromunda.setStatusMessage("This ganger cannot hide as he has run this turn.");
			return;
		}

		boolean hideable = isHideable(selectedFighterNode);

		if (hideable) {
			selectedFighterNode.getFighter().setHidden(true);
			selectedFighterNode.getFighter().setRemainingMovement(0);
			selectionMode = SelectionMode.SELECT;
		}
		else {
			Necromunda.setStatusMessage("This fighter cannot hide here.");
		}
	}

	private boolean isHideable(FighterNode fighterNode) {
		List<Collidable> collidables = getTemplateBoundingVolumes();
		collidables.addAll(getBuildingBoundingVolumes());

		boolean hideable = true;

		List<FighterNode> hostileFighterNodes = getHostileFighterNodes(getFighterNodes());

		for (FighterNode hostileFighterNode : hostileFighterNodes) {
			VisibilityInfo fighterVisibilityInfo = getVisibilityInfo(hostileFighterNode,
					fighterNode.getCollisionShapePointCloud(), collidables);

			if (fighterVisibilityInfo.getNumberOfPoints() == fighterVisibilityInfo.getNumberOfVisiblePoints()) {
				hideable = false;
				break;
			}

			// VisibilityInfo pathVisibilityInfo =
			// getVisibilityInfo(fighterNode,
			// getPathBoxNodePointCloud(currentPathBoxNode), collidables);
			// TODO: Seems to be impossible to check if a fighter was completely
			// visible at a certain point on his path

			if (isTargetWithinDistance(hostileFighterNode, fighterNode, hostileFighterNode.getFighter().getInitiative())) {
				hideable = false;
				break;
			}
		}

		return hideable;
	}

	private void onRightClick(boolean isPressed) {
		if (isPressed) {
			rightButtonDown = true;

			setFlyCamEnabled(false);

			if (selectionMode == SelectionMode.MOVE) {
				tearDownMovement();
				selectionMode = SelectionMode.SELECT;
				selectedFighterNode = null;
			}
			else if (selectionMode == SelectionMode.CLIMB) {
				abortClimbing();
				selectionMode = SelectionMode.SELECT;
				selectedFighterNode = null;
			}
			else if (selectionMode == SelectionMode.TARGET) {
				tearDownTargeting();
				selectionMode = SelectionMode.SELECT;
				selectedFighterNode = null;
			}
		}
		else {
			rightButtonDown = false;
			setFlyCamEnabled(true);
		}
	}

	private void setFlyCamEnabled(boolean enabled) {
		FlyCamAppState flyCamAppState = stateManager.getState(FlyCamAppState.class);
		InvertedFlyCamAppState invertedFlyCamAppState = stateManager.getState(InvertedFlyCamAppState.class);

		if (flyCamAppState != null) {
			flyCamAppState.getCamera().setDragToRotate(!enabled);

			if (enabled) {
				flyCamAppState.getCamera().registerWithInput(inputManager);
			}
			else {
				flyCamAppState.getCamera().unregisterInput();
			}
		}

		if (invertedFlyCamAppState != null) {
			invertedFlyCamAppState.getCamera().setDragToRotate(!enabled);

			if (enabled) {
				invertedFlyCamAppState.getCamera().registerWithInput(inputManager);
			}
			else {
				invertedFlyCamAppState.getCamera().unregisterInput();
			}
		}
	}

	public FighterNode getFighterNodeUnderCursor() {
		List<Collidable> collidables = new ArrayList<Collidable>();

		for (FighterNode fighterNode : getFighterNodes()) {
			collidables.add(fighterNode.getBoundingVolume());
		}

		CollisionResult closestCollision = Utils.getClosestCollision(cam.getLocation(), cam.getDirection(), collidables);

		FighterNode fighterNodeUnderCursor = null;

		if (closestCollision != null) {
			Geometry geometry = closestCollision.getGeometry();
			fighterNodeUnderCursor = (FighterNode) getParent(geometry, "fighterNode");
		}

		return fighterNodeUnderCursor;
	}

	public boolean addTarget(FighterNode fighterNode) {
		if (validSustainedFireTargetFighterNodes.isEmpty()) {
			addFirstTarget(fighterNode);
			return true;
		}
		else {
			return addSubsequentTarget(fighterNode);
		}
	}

	private void addFirstTarget(FighterNode fighterNode) {
		targetedFighterNodes.add(fighterNode);

		List<FighterNode> sustainedFireNeighbours = getFighterNodesWithinDistance(getFighterNodeUnderCursor(), getFighterNodes(),
				Necromunda.SUSTAINED_FIRE_RADIUS);
		sustainedFireNeighbours = getVisibleFighterNodes(selectedFighterNode, sustainedFireNeighbours);

		validSustainedFireTargetFighterNodes.add(fighterNode);
		validSustainedFireTargetFighterNodes.addAll(sustainedFireNeighbours);
	}

	private boolean addSubsequentTarget(FighterNode fighterNode) {
		if (validSustainedFireTargetFighterNodes.contains(fighterNode)) {
			targetedFighterNodes.add(fighterNode);
			return true;
		}
		else {
			Necromunda.setStatusMessage("This target is not a valid target for sustained fire.");
			return false;
		}
	}

	private void tearDownMovement() {
		if (currentPath != null) {
			selectedFighterNode.setLocalTranslation(currentPath.getOrigin());
		}

		if (currentClimbPath != null) {
			selectedFighterNode.setLocalTranslation(currentClimbPath.getStart());
		}

		if (currentPathNode != null) {
			rootNode.detachChild(currentPathNode);
		}

		currentPath = null;
		currentPathNode = null;
	}

	private void abortClimbing() {
		if (currentClimbPath != null) {
			selectedFighterNode.setLocalTranslation(currentClimbPath.getStart());
		}

		currentClimbPath = null;
	}

	public void tearDownTargeting() {
		rootNode.detachChildNamed("currentLineOfSightLine");
		removeCurrentWeaponTemplate();
	}

	private void removeCurrentWeaponTemplate() {
		if ((currentTemplateNode != null) && !nodesToBeRemoved.contains(currentTemplateNode)) {
			rootNode.detachChild(currentTemplateNode);
		}
		currentTemplateNode = null;
	}

	private void commitMovement() {
		Fighter selectedFighter = selectedFighterNode.getFighter();

		Vector3f movementVector = currentPath.getVector();
		float distance = movementVector.length();

		float remainingMovementDistance = selectedFighter.getRemainingMovement() - distance;
		selectedFighter.setRemainingMovement(remainingMovementDistance);

		currentPath.getOrigin().set(selectedFighterNode.getLocalTranslation());

		for (Fighter fighter : selectedFighter.getGang().getGangMembers()) {
			if (fighter != selectedFighter) {
				if (fighter.hasMoved() || fighter.hasRun()) {
					fighter.setRemainingMovement(0);
				}
			}
		}

		if (selectedFighter.isGoingToRun()) {
			selectedFighter.setHasRun(true);
		}
		else {
			selectedFighter.setHasMoved(true);
		}

		if (selectedFighter.isSpotted() && selectedFighter.hasRun()) {
			selectedFighter.setRemainingMovement(0);
			selectionMode = SelectionMode.SELECT;
		}

		if (selectedFighter.getRemainingMovement() < 0.01f) {
			selectedFighter.setRemainingMovement(0);
			tearDownMovement();
			selectionMode = SelectionMode.SELECT;
		}

		revealHiddenFighters();

		if (!isHideable(selectedFighterNode)) {
			selectedFighterNode.getFighter().setHidden(false);
		}
	}

	private void revealHiddenFighters() {
		List<Collidable> collidables = getTemplateBoundingVolumes();
		collidables.addAll(getBuildingBoundingVolumes());

		List<FighterNode> hostileFighterNodes = getHostileFighterNodes(getFighterNodes());

		for (FighterNode fighterNode : hostileFighterNodes) {
			VisibilityInfo visibilityInfo = getVisibilityInfo(selectedFighterNode, fighterNode.getCollisionShapePointCloud(),
					collidables);

			if (visibilityInfo.getNumberOfPoints() == visibilityInfo.getNumberOfVisiblePoints()) {
				fighterNode.getFighter().setHidden(false);
			}
		}

		List<FighterNode> fighterNodesWithinInitiativeRange = getFighterNodesWithinDistance(selectedFighterNode,
				hostileFighterNodes, selectedFighterNode.getFighter().getInitiative());

		for (FighterNode fighterNode : fighterNodesWithinInitiativeRange) {
			fighterNode.getFighter().setHidden(false);
		}
	}

	private void commitClimb() {
		Fighter selectedFighter = selectedFighterNode.getFighter();

		float distance = currentClimbPath.getLength();
		float remainingMovementDistance = selectedFighter.getRemainingMovement() - distance;
		selectedFighter.setRemainingMovement(remainingMovementDistance);

		currentClimbPath.getStart().set(selectedFighterNode.getLocalTranslation());

		for (Fighter fighter : selectedFighter.getGang().getGangMembers()) {
			if (fighter != selectedFighter) {
				if (fighter.hasMoved() || fighter.hasRun()) {
					fighter.setRemainingMovement(0);
				}
			}
		}

		if (selectedFighter.isGoingToRun()) {
			selectedFighter.setHasRun(true);
		}
		else {
			selectedFighter.setHasMoved(true);
		}

		if (selectedFighter.getRemainingMovement() < 0.01f) {
			selectedFighter.setRemainingMovement(0);
		}

		abortClimbing();

		selectionMode = SelectionMode.SELECT;

		revealHiddenFighters();

		if (!isHideable(selectedFighterNode)) {
			selectedFighterNode.getFighter().setHidden(false);
		}
	}

	private Geometry getPathBoxGeometry(Vector3f halfExtents) {
		Box box = new Box(halfExtents.getX(), halfExtents.getY(), halfExtents.getZ());
		Geometry boxGeometry = new Geometry("pathBoxGeometry", box);
		boxGeometry.setMaterial(materialFactory.createTransparentColourMaterial(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f), 0.5f));
		boxGeometry.setQueueBucket(Bucket.Translucent);

		return boxGeometry;
	}

	private Vector3f getPathHalfExtents(FighterNode fighterNode, Line path) {
		Vector3f halfExtents = getHalfExtentsOf(fighterNode);
		float pathLength = path.length();

		Vector3f vector = new Vector3f(halfExtents.getX(), halfExtents.getY(), pathLength / 2);

		return vector;
	}

	private Vector3f getHalfExtentsOf(FighterNode fighterNode) {
		Cylinder boundingVolume = (Cylinder) fighterNode.getBoundingVolume().getMesh();
		Vector3f halfExtents = new Vector3f(boundingVolume.getRadius(), boundingVolume.getHeight() / 2, boundingVolume.getRadius());

		return halfExtents;
	}

	private void setBaseSelected(FighterNode fighterNode) {
		Material baseMaterial = materialFactory.createColourMaterial(ColorRGBA.Red);
		fighterNode.setBaseMaterial(baseMaterial);
	}

	private void setBaseTargeted(FighterNode fighterNode) {
		Material baseMaterial = materialFactory.createColourMaterial(ColorRGBA.Yellow);
		fighterNode.setBaseMaterial(baseMaterial);
	}

	private void setBaseNormal(FighterNode fighterNode) {
		Material baseMaterial = materialFactory.createTextureMaterial("Images/Textures/Base/BaseGrass.png", new ColorRGBA(64 / 255f, 147 / 255f, 91 / 255f, 1.0f));
		fighterNode.setBaseMaterial(baseMaterial);
	}

	private boolean hasValidPosition(NecromundaNode node) {
		List<NecromundaNode> otherNodes = new ArrayList<NecromundaNode>();
		otherNodes.add(node);
		otherNodes.addAll(getBuildingNodes());
		otherNodes.add(node);
		
		return intersect(node, otherNodes);
	}
	
	private boolean intersect(NecromundaNode node, List<NecromundaNode> otherNodes) {
		for (NecromundaNode otherNode : otherNodes) {
			if (Utils.intersect(node, otherNode)) {
				System.out.println("Position invalid...");
				return true;
			}
		}
		
		return false;
	}
	
	public List<Geometry> getBuildingBoundingVolumes() {
		List<Geometry> boundingVolumes = new ArrayList<Geometry>();
		
		for (Spatial spatial : getBuildingsNode().getChildren()) {
			NecromundaNode buildingNode = (NecromundaNode) spatial;
			boundingVolumes.addAll(buildingNode.getBoundingVolumes());
		}
		
		return boundingVolumes;
	}
	
	private List<BuildingNode> getBuildingNodes() {
		List<BuildingNode> buildingNodes = new ArrayList<BuildingNode>();
		
		for (Spatial child : getBuildingsNode().getChildren()) {
			if (child instanceof BuildingNode) {
				BuildingNode buildingNode = (BuildingNode)child;
				buildingNodes.add(buildingNode);
			}
		}
		
		return buildingNodes;
	}

	private void moveAlongPath(FighterNode fighterNode, Vector3f endPosition) {

	}

	private Node getParent(Spatial spatial, String name) {
		Node parent = spatial.getParent();

		if (parent == null) {
			return null;
		}
		else if (parent.getName().equals(name)) {
			return parent;
		}
		else {
			return getParent(parent, name);
		}
	}

	private void initCrossHairs() {
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText crosshair = new BitmapText(guiFont, false);
		crosshair.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		// crosshairs
		crosshair.setText("+");
		// center
		crosshair.setLocalTranslation(settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
				settings.getHeight() / 2 + crosshair.getLineHeight() / 2, 0);
		guiNode.attachChild(crosshair);
	}

	private void initStatusMessage() {
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		statusMessage = new BitmapText(guiFont, false);
		statusMessage.setSize(guiFont.getCharSet().getRenderedSize());
		statusMessage.setLocalTranslation(10, 120, 0);
		guiNode.attachChild(statusMessage);
	}

	public VisibilityInfo getVisibilityInfo(FighterNode source, List<Vector3f> pointCloud, List<Collidable> collidables) {
		Vector3f sourceUpTranslation = new Vector3f(0, source.getFighter().getBaseRadius() * 1.5f, 0);

		Vector3f sourceLocation = source.getLocalTranslation().add(sourceUpTranslation);

		int numberOfVisiblePoints = 0;

		for (Vector3f vector : pointCloud) {
			Vector3f direction = vector.subtract(sourceLocation);

			CollisionResult closestCollision = Utils.getClosestCollision(sourceLocation, direction, collidables);

			if (closestCollision != null) {
				float distanceToTarget = direction.length();
				float distanceToCollisionPoint = closestCollision.getContactPoint().subtract(sourceLocation).length();

				if (distanceToCollisionPoint >= distanceToTarget) {
					numberOfVisiblePoints++;
				}
			}
			else {
				numberOfVisiblePoints++;
			}
		}

		VisibilityInfo visibilityInfo = new VisibilityInfo(numberOfVisiblePoints, pointCloud.size());

		return visibilityInfo;
	}

	public List<Collidable> getTemplateBoundingVolumes() {
		List<Collidable> boundingVolumes = new ArrayList<Collidable>();

		for (TemplateNode templateNode : getTemplateNodes()) {
			boundingVolumes.addAll(templateNode.getBoundingVolumes());
		}

		return boundingVolumes;
	}

	private Vector3f getTableCollisionPoint() {
		List<Collidable> collidables = new ArrayList<Collidable>();
		collidables.add(getTableNode());
		CollisionResult closestCollision = Utils.getClosestCollision(cam.getLocation(), cam.getDirection(), collidables);

		if (closestCollision != null) {
			return closestCollision.getContactPoint().add(GROUND_BUFFER);
		}
		else {
			return null;
		}
	}

	private Vector3f getSceneryCollisionPoint() {
		List<Collidable> collidables = new ArrayList<Collidable>();
		collidables.add(getTableNode());
		collidables.addAll(getBuildingBoundingVolumes());
		CollisionResult closestCollision = Utils.getClosestCollision(cam.getLocation(), cam.getDirection(), collidables);

		if ((closestCollision != null)
				&& closestCollision.getContactNormal().angleBetween(Vector3f.UNIT_Y) <= MAX_COLLISION_NORMAL_ANGLE) {
			return closestCollision.getContactPoint().add(GROUND_BUFFER);
		}
		else {
			return null;
		}
	}

	private Vector3f getLadderCollisionPoint(LadderNode ladder) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(ladder.getWorldEnd(), Vector3f.UNIT_Y.mult(-1));
		getTableNode().collideWith(ray, results);
		getBuildingsNode().collideWith(ray, results);
		CollisionResult closestCollision = results.getClosestCollision();

		if (closestCollision != null
				&& closestCollision.getContactNormal().angleBetween(Vector3f.UNIT_Y) <= MAX_COLLISION_NORMAL_ANGLE) {
			return closestCollision.getContactPoint().add(GROUND_BUFFER);
		}
		else {
			return null;
		}
	}

	public class MouseListener implements ActionListener, AnalogListener {
		public void onAction(String name, boolean isPressed, float tpf) {
			executeMouseAction(name, isPressed);
		}

		public void onAnalog(String name, float value, float tpf) {
			if (selectionMode == SelectionMode.TARGET) {
				setUpTargeting();
			}
			else if (selectionMode == SelectionMode.DEPLOY_BUILDING) {
				if (rightButtonDown) {
					if ((selectedBuildingNode != null) && isMouseMovement(name)) {
						float direction = 1;

						if (name.equals("Move_Left")) {
							direction = -1;
						}

						selectedBuildingNode.rotate(0, value * 3 * direction, 0);
					}
				}
			}
		}
	}

	private boolean isMouseMovement(String name) {
		if (name.equals("Move_Left") || name.equals("Move_Right") || name.equals("Move_Up") || name.equals("Move_Down")) {
			return true;
		}
		else {
			return false;
		}
	}

	public class KeyboardListener implements ActionListener {

		public void onAction(String name, boolean isPressed, float tpf) {
			if (isPressed) {
				executeKeyboardAction(name);
			}
		}
	}

	private void executeKeyboardAction(String name) {
		Necromunda.setStatusMessage("");

		Fighter selectedFighter = null;

		if (selectedFighterNode != null) {
			selectedFighter = selectedFighterNode.getFighter();
		}

		if (name.equals("NextPhase")) {
			tearDownMovement();
			selectionMode = SelectionMode.SELECT;
			game.nextPhase();
		}
		else if (name.equals("EndTurn")) {
			tearDownMovement();
			tearDownTargeting();
			selectionMode = SelectionMode.SELECT;
			game.endTurn();
			turnStarted();
		}
		else if (name.equals("SkipBuilding")) {
			skipBuilding();
		}
		else if (name.equals("SkipFighter")) {
			skipFighter();
		}
		else if (name.equals("EndBuildingDeployment")) {
			endBuildingDeployment();
		}
		else if (selectionMode == SelectionMode.REROLL) {
			if (name.equals("Yes")) {
				currentWeapon.dealDamageTo(currentTarget);
				selectionMode = SelectionMode.SELECT;
			}
			else if (name.equals("No")) {
				selectionMode = SelectionMode.SELECT;
			}
		}
		else if (selectedFighterNode != null) {
			if ((selectionMode == SelectionMode.SELECT) && isMemberOfCurrentGang(selectedFighterNode)) {
				if (name.equals("Break") && game.getPhase().equals(Phase.MOVEMENT)) {
					if (selectedFighter.isPinned()) {
						Necromunda.setStatusMessage("This ganger cannot break the web.");
					}
					else {
						selectedFighter.breakWeb();
					}
				}
				else if (name.equals("Move") && game.getPhase().equals(Phase.MOVEMENT)) {
					if (selectedFighter.canMove() && !selectedFighter.hasRun()) {
						if (!selectedFighter.hasMoved()) {
							selectedFighter.setGoingToRun(false);
						}

						selectionMode = SelectionMode.MOVE;
						setUpMovement();
					}
					else {
						Necromunda.setStatusMessage("This ganger cannot move.");
					}
				}
				else if (name.equals("Run") && game.getPhase().equals(Phase.MOVEMENT)) {
					if (selectedFighter.canRun() && !selectedFighter.hasMoved()) {
						if (!selectedFighter.hasRun()) {
							selectedFighter.setGoingToRun(true);
						}

						selectionMode = SelectionMode.MOVE;
						setUpMovement();
					}
					else {
						Necromunda.setStatusMessage("This ganger cannot run.");
					}
				}
				else if (name.equals("Hide") && game.getPhase().equals(Phase.MOVEMENT)) {
					hide();
				}
				else if (name.equals("Shoot") && game.getPhase().equals(Phase.SHOOTING)) {
					if (selectedFighter.canShoot()) {
						if (!selectedFighter.getWeapons().isEmpty()) {
							RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();

							if (weapon == null) {
								weapon = (RangeCombatWeapon) selectedFighter.getWeapons().get(0);
								selectedFighter.setSelectedRangeCombatWeapon(weapon);
							}

							if (weapon.isBroken()) {
								Necromunda.setStatusMessage("The selected weapon is broken.");
							}
							else if (!weapon.isEnabled()) {
								Necromunda.setStatusMessage("The selected weapon is disabled.");
							}
							else if (weapon.isMoveOrFire() && selectedFighter.hasMoved()) {
								Necromunda.setStatusMessage("The selected weapon cannot be fired after moving.");
							}
							else {
								weapon.reset();

								selectionMode = SelectionMode.TARGET;

								setUpTargeting();
							}
						}
						else {
							Necromunda.setStatusMessage("This ganger has no weapons.");
						}
					}
					else {
						Necromunda.setStatusMessage("This ganger cannot shoot.");
					}
				}
				else if (name.equals("Cycle")) {
					CyclicList<Weapon> weapons = selectedFighter.getWeapons();

					if (!weapons.isEmpty()) {
						selectedFighter.setSelectedRangeCombatWeapon((RangeCombatWeapon)weapons.next());
					}
					else {
						Necromunda.setStatusMessage("This ganger has no weapons.");
					}
				}
				else if (name.equals("Mode")) {
					RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();

					if (weapon != null) {
						List<Ammunition> ammunitions = weapon.getAmmunitions();

						int index = ammunitions.indexOf(weapon.getCurrentAmmunition());

						if (index == -1) {
							weapon.setCurrentAmmunition(ammunitions.get(0));
						}
						else {
							if (index < ammunitions.size() - 1) {
								weapon.setCurrentAmmunition(ammunitions.get(index + 1));
							}
							else {
								weapon.setCurrentAmmunition(ammunitions.get(0));
							}
						}
					}
					else {
						Necromunda.setStatusMessage("No weapon selected.");
					}
				}
				else if (name.equals("SustainedFireDice")) {
					RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();

					if (weapon != null) {
						Ammunition ammunition = weapon.getCurrentAmmunition();
						
						if (ammunition != null) {
							ShotHandler shotHandler = ammunition.getShotHandler();
							
							if (shotHandler instanceof SustainedFireShotHandler) {
								SustainedFireShotHandler sustainedFireShotHandler = (SustainedFireShotHandler)shotHandler;
								int number = sustainedFireShotHandler.getNumberOfSustainedFireDice();
								
								if (number < sustainedFireShotHandler.getMaximumNumberOfSustainedFireDice()) {
									sustainedFireShotHandler.setNumberOfSustainedFireDice(++number);
								}
								else {
									sustainedFireShotHandler.setNumberOfSustainedFireDice(1);
								}
							}
						}
					}
					else {
						Necromunda.setStatusMessage("No weapon selected.");
					}
				}
			}
			else if ((selectionMode == SelectionMode.MOVE) && name.equals("Climb") && isMemberOfCurrentGang(selectedFighterNode)) {
				currentLadders = getLaddersInReach(currentPath.getOrigin(), selectedFighter.getBaseRadius());

				if (currentLadders.isEmpty()) {
					Necromunda.setStatusMessage("There is no ladder in reach.");
				}
				else {
					currentLadder = currentLadders.get(0);
					selectionMode = SelectionMode.CLIMB;
					climbLadder(currentLadder);
				}
			}
			else if ((selectionMode == SelectionMode.CLIMB) && name.equals("Climb") && isMemberOfCurrentGang(selectedFighterNode)) {
				int ladderIndex = currentLadders.indexOf(currentLadder);

				if (ladderIndex < currentLadders.size() - 1) {
					ladderIndex += 1;
				}
				else {
					ladderIndex = 0;
				}

				currentLadder = currentLadders.get(ladderIndex);
				climbLadder(currentLadder);
			}
		}
		else {
			Necromunda.setStatusMessage("You must select a fighter first.");
		}
	}

	private class NodeRemover {
		private Node node;
		private int timeToRemove;

		public NodeRemover(Node temporaryWeaponTemplate) {
			this.node = temporaryWeaponTemplate;
			this.timeToRemove = 2000;
		}

		public void remove() {
			rootNode.detachChild(node);
		}
		
		public int getTimeToRemove() {
			return timeToRemove;
		}
		
		public void reduce(int time) {
			timeToRemove -= time;
		}
	}

	private List<LadderNode> getLaddersInReach(Vector3f origin, float baseRadius) {
		List<LadderNode> laddersInReach = new ArrayList<LadderNode>();

		for (LadderNode ladder : (List<LadderNode>) getLadders(buildingsNode)) {
			float distance = ladder.getWorldStart().distance(origin);

			if ((distance - baseRadius) <= LadderNode.MAX_LADDER_DISTANCE) {
				laddersInReach.add(ladder);
			}
		}

		return laddersInReach;
	}

	private void unpinFighters() {
		for (FighterNode fighterNode : getFighterNodes()) {
			Fighter fighter = fighterNode.getFighter();

			if (game.getCurrentGang().getGangMembers().contains(fighter) && fighter.isPinned()) {
				List<FighterNode> nearbyFighterNodes = getFighterNodesWithinDistance(fighterNode, getFighterNodes(),
						Necromunda.UNPIN_BY_INITIATIVE_DISTANCE);
				List<Fighter> reliableMates = new ArrayList<Fighter>();

				for (FighterNode nearbyFighterNode : nearbyFighterNodes) {
					Fighter nearbyFighter = nearbyFighterNode.getFighter();

					if (fighter.isGangMate(nearbyFighter) && nearbyFighter.isReliable()) {
						reliableMates.add(nearbyFighter);
					}
				}

				if (!reliableMates.isEmpty() || fighter instanceof Leader) {
					fighter.unpinByInitiative();
				}
				else {
					Necromunda.appendToStatusMessage(String.format("%s has no reliable mates around.", fighter));
				}
			}
		}
	}

	private void turnStarted() {
		unpinFighters();
		removeTemplates();
		moveTemplates();
		applyTemplateEffects();
		//removeTemplateTrails();
	}

	private void removeTemplates() {
		for (TemplateNode templateNode : getTemplateNodes()) {
			if (templateNode.isTemplateToBeRemoved()) {
				getObjectsNode().detachChild(templateNode);
			}
		}
	}

	private void moveTemplates() {
		for (TemplateNode templateNode : getTemplateNodes()) {
			if (templateNode.isTemplateMoving()) {
				float distance = templateNode.getDriftDistance();
				float angle = templateNode.getDriftAngle();

				Vector3f start = templateNode.getLocalTranslation().clone();

				List<Collidable> collidables = new ArrayList<Collidable>();
				collidables.add(getBuildingsNode());
				templateNode.moveAndCollide(distance, angle, collidables);
				templateNode.attachTrail(start);
			}
		}
	}

	private void applyTemplateEffects() {
		for (TemplateNode templateNode : getTemplateNodes()) {
			List<FighterNode> affectedFighterNodes = getFighterNodesUnderTemplate(templateNode, getFighterNodes());
			templateNode.dealDamageTo(affectedFighterNodes);
		}
	}

	private void removeTemplateTrails() {
		for (TemplateNode templateNode : getTemplateNodes()) {
			templateNode.removeTrail();
		}
	}

	private void setUpMovement() {
		updateCurrentPath();

		if (currentPath != null) {
			updateCurrentPathBox();
		}
	}

	private void updateCurrentPath() {
		Vector3f nearestIntersection = getSceneryCollisionPoint();
		Vector3f objectPosition = null;

		if (currentPath == null) {
			objectPosition = selectedFighterNode.getLocalTranslation();
			
			if (nearestIntersection == null) {
				nearestIntersection = objectPosition;
			}
		}
		else {
			objectPosition = currentPath.getOrigin();
			
			if (nearestIntersection == null) {
				return;
			}
		}

		float slope = FastMath.abs(nearestIntersection.getY() - objectPosition.getY());

		if (currentPath == null) {
			if (slope > MAX_SLOPE) {
				nearestIntersection = objectPosition;
			}
			
			currentPath = new Line(objectPosition.clone(), nearestIntersection);
		}
		else {
			if (slope > MAX_SLOPE) {
				return;
			}
			
			currentPath = new Line(objectPosition.clone(), nearestIntersection);
		}

		Vector3f movementVector = nearestIntersection.subtract(currentPath.getOrigin());
		float distance = movementVector.length();
		float remainingMovementDistance = selectedFighterNode.getFighter().getRemainingMovement();

		if (distance > remainingMovementDistance) {
			movementVector.normalizeLocal().multLocal(remainingMovementDistance);
		}

		currentPath.getDirection().set(currentPath.getOrigin().add(movementVector));
	}

	private void updateCurrentPathBox() {
		rootNode.detachChildNamed("currentPathBoxNode");

		currentPathNode = getPathBoxNode(currentPath.getOrigin(), currentPath.getDirection(), selectedFighterNode);
		currentPathNode.setName("currentPathBoxNode");
		rootNode.attachChild(currentPathNode);

		selectedFighterNode.setLocalTranslation(currentPath.getDirection());
	}

	private PathNode getPathBoxNode(Vector3f start, Vector3f end, FighterNode fighterNode) {
		Line path = new Line(start, end);
		Vector3f halfExtents = getPathHalfExtents(fighterNode, path);
		Geometry pathBoxGeometry = getPathBoxGeometry(halfExtents);

		PathNode pathBoxNode = new PathNode("pathBoxNode");
		pathBoxNode.attachChild(pathBoxGeometry);

		Vector3f translation = path.getOrigin().add(path.getVector().mult(0.5f)).addLocal(selectedFighterNode.getLocalCenter());
		pathBoxNode.setLocalTranslation(translation);
		pathBoxNode.lookAt(currentPath.getDirection().add(selectedFighterNode.getLocalCenter()),
				selectedFighterNode.getLocalCenter());

		return pathBoxNode;
	}

	private List<Vector3f> getPathBoxNodePointCloud(Node pathBoxNode) {
		List<Vector3f> pointCloud = new ArrayList<Vector3f>();

		GhostControl ghostControl = pathBoxNode.getControl(GhostControl.class);
		BoxCollisionShape collisionShape = (BoxCollisionShape) ghostControl.getCollisionShape();

		Vector3f halfExtents = collisionShape.getHalfExtents();

		for (float x = 0; x <= halfExtents.getX(); x += 0.1f) {
			for (float y = 0; y < halfExtents.getY(); y += 0.1f) {
				for (float z = 0; z < halfExtents.getZ(); z += 0.1f) {
					addVectors(x, y, z, pointCloud);
				}

				addVectors(x, y, halfExtents.getZ(), pointCloud);
			}

			addVectors(x, halfExtents.getY(), halfExtents.getZ(), pointCloud);
		}

		addVectors(halfExtents.getX(), halfExtents.getY(), halfExtents.getZ(), pointCloud);

		for (Vector3f vector : pointCloud) {
			vector.addLocal(pathBoxNode.getWorldTranslation());
		}

		return pointCloud;
	}

	private void addVectors(float x, float y, float z, List<Vector3f> pointCloud) {
		pointCloud.add(new Vector3f(x, y, z));
		pointCloud.add(new Vector3f(x, y, -z));
		pointCloud.add(new Vector3f(x, -y, z));
		pointCloud.add(new Vector3f(x, -y, -z));
		pointCloud.add(new Vector3f(-x, y, z));
		pointCloud.add(new Vector3f(-x, y, -z));
		pointCloud.add(new Vector3f(-x, -y, z));
		pointCloud.add(new Vector3f(-x, -y, -z));
	}

	private void setUpTargeting() {
		Line line = null;

		if (getSelectedRangeCombatWeapon().isTargeted()) {
			if (isTargetUnderCursor()) {
				updateCurrentLineOfSightLine();

				if (getSelectedRangeCombatWeapon().isTemplated()) {
					line = getLineOfSight(selectedFighterNode, getFighterNodeUnderCursor());
				}
			}
			else {
				tearDownTargeting();
			}
		}
		else if (getSelectedRangeCombatWeapon().isTemplated()) {
			if (getSceneryCollisionPoint() != null) {
				line = new Line(selectedFighterNode.getLocalTranslation(), getSceneryCollisionPoint());
			}
		}

		if (line != null) {
			if (currentTemplateNode == null) {
				currentTemplateNode = TemplateNode.createTemplateNode(assetManager, selectedFighterNode, getSelectedRangeCombatWeapon()
						.getCurrentAmmunition());
				
				if (game.isDebug()) {
					currentTemplateNode.displayVisualSpatials(false);
					currentTemplateNode.displayBoundingVolumes(true);
				}
			}
			else if (!nodesToBeRemoved.contains(currentTemplateNode)) {
				rootNode.detachChild(currentTemplateNode);
			}

			if (getSelectedRangeCombatWeapon().isTemplateAttached()) {
				Vector3f lineOfSightVector = line.getDirection().subtract(line.getOrigin());
				lineOfSightVector = lineOfSightVector.normalize();

				float angle = FastMath.acos(lineOfSightVector.x);

				if (lineOfSightVector.z < 0) {
					angle = -angle;
				}

				Matrix3f rotationMatrix = new Matrix3f(FastMath.cos(angle), 0, -FastMath.sin(angle), 0, 1, 0,
						FastMath.sin(angle), 0, FastMath.cos(angle));
				currentTemplateNode.setLocalRotation(rotationMatrix);
				currentTemplateNode.setLocalTranslation(selectedFighterNode.getLocalCenter().add(
						selectedFighterNode.getLocalTranslation()));
			}
			else {
				currentTemplateNode.setLocalTranslation(line.getDirection());
			}
			
			rootNode.attachChild(currentTemplateNode);
		}
	}

	private boolean isTargetUnderCursor() {
		return (getFighterNodeUnderCursor() != null) && (selectedFighterNode != getFighterNodeUnderCursor());
	}

	public Line getLineOfSight(FighterNode source, FighterNode target) {
		Vector3f sourceHeadPosition = source.getLocalHeadPosition();
		source.localToWorld(sourceHeadPosition, sourceHeadPosition);
		Vector3f targetCenter = target.getLocalCenter();
		target.localToWorld(targetCenter, targetCenter);

		return new Line(sourceHeadPosition, targetCenter);
	}

	private void updateCurrentLineOfSightLine() {
		rootNode.detachChildNamed("currentLineOfSightLine");

		Line lineOfSight = getLineOfSight(selectedFighterNode, getFighterNodeUnderCursor());
		Vector3f start = lineOfSight.getOrigin();
		Vector3f end = lineOfSight.getDirection();

		com.jme3.scene.shape.Line line = new com.jme3.scene.shape.Line(start, end);
		Geometry lineGeometry = new Geometry("currentLineOfSightLine", line);
		lineGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.TARGET_LINE));

		rootNode.attachChild(lineGeometry);
	}

	public float getLineLength(Line line) {
		return line.getOrigin().distance(line.getDirection());
	}

	public List<FighterNode> getHostileFighterNodes(List<FighterNode> fighterNodes) {
		List<FighterNode> hostileFighterNodes = new ArrayList<FighterNode>();

		for (FighterNode fighterNode : fighterNodes) {
			Fighter fighter = fighterNode.getFighter();

			if (game.getHostileGangers().contains(fighter)) {
				hostileFighterNodes.add(fighterNode);
			}
		}

		return hostileFighterNodes;
	}

	public List<FighterNode> getVisibleFighterNodes(FighterNode source, List<FighterNode> fighterNodes) {
		List<FighterNode> visibleFighterNodes = new ArrayList<FighterNode>();

		List<Collidable> collidables = getTemplateBoundingVolumes();
		collidables.addAll(getBuildingBoundingVolumes());

		for (FighterNode fighterNode : fighterNodes) {
			VisibilityInfo visibilityInfo = getVisibilityInfo(source, fighterNode.getCollisionShapePointCloud(), collidables);

			if ((visibilityInfo.getNumberOfVisiblePoints() > 0) && !fighterNode.getFighter().isHidden()) {
				visibleFighterNodes.add(fighterNode);
			}
		}

		return visibleFighterNodes;
	}

	private FighterNode getNearestFighterNode(FighterNode source, List<FighterNode> fighterNodes) {
		float nearestDistance = Float.MAX_VALUE;
		FighterNode nearestFighterNode = null;

		for (FighterNode fighterNode : fighterNodes) {
			float distance = source.getLocalTranslation().distance(fighterNode.getLocalTranslation());

			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearestFighterNode = fighterNode;
			}
		}

		return nearestFighterNode;
	}

	public List<FighterNode> getFighterNodesWithinDistance(FighterNode fighterNode, List<FighterNode> fighterNodes,
			float maxDistance) {
		List<FighterNode> otherFighterNodes = new ArrayList<FighterNode>();

		for (FighterNode otherFighterNode : fighterNodes) {
			if (otherFighterNode == fighterNode) {
				continue;
			}

			if (isTargetWithinDistance(fighterNode, otherFighterNode, maxDistance)) {
				otherFighterNodes.add(otherFighterNode);
			}
		}

		return otherFighterNodes;
	}

	private boolean isTargetWithinDistance(FighterNode source, FighterNode target, float maxDistance) {
		boolean isInRange = false;

		Fighter fighter = source.getFighter();
		Fighter otherFighter = target.getFighter();
		float distance = source.getLocalTranslation().distance(target.getLocalTranslation());
		distance -= fighter.getBaseRadius() + otherFighter.getBaseRadius();

		if (distance < maxDistance) {
			isInRange = true;
		}

		return isInRange;
	}

	private List<FighterNode> getFighterNodesUnderTemplate(TemplateNode templateNode, List<FighterNode> fighterNodes) {
		List<FighterNode> fighterNodesUnderTemplate = new ArrayList<FighterNode>();

		for (FighterNode fighterNode : fighterNodes) {
			if (Utils.intersect(fighterNode, templateNode)) {
				fighterNodesUnderTemplate.add(fighterNode);
			}
		}

		return fighterNodesUnderTemplate;
	}

	public void queueNodeForRemoval(Node node) {
		if (!currentTemplateNode.isTemplatePersistent()) {
			nodesToBeRemoved.add(node);
			NodeRemover nodeRemover = new NodeRemover(node);
			nodeRemovers.add(nodeRemover);
		}
		else {
			getObjectsNode().attachChild(node);
		}
	}

	public void fireTemplate(TemplateNode templateNode) {
		List<FighterNode> affectedFighterNodes = getFighterNodesUnderTemplate(templateNode, getFighterNodes());
		pinNormalFighters(affectedFighterNodes);
		templateNode.dealDamageTo(affectedFighterNodes);
	}

	public void pinNormalFighters(List<FighterNode> fighterNodes) {
		for (FighterNode fighterNode : fighterNodes) {
			Fighter fighter = fighterNode.getFighter();

			if (fighter.isNormal()) {
				fighter.setState(State.PINNED);
			}
		}
	}

	private String buildStatusText(Necromunda game) {
		StringBuilder statusText = new StringBuilder();

		String string = String.format("Turn %s, %s\n", game.getTurn(), game.getCurrentGang().toString());
		statusText.append(string);

		if (selectedFighterNode != null) {
			Fighter fighter = selectedFighterNode.getFighter();
			statusText.append(String.format("%s, %s, %sFlesh Wounds: %s\n", fighter.getName(), fighter.getState(),
					(fighter.isWebbed() ? "Webbed, " : ""), fighter.getFleshWounds()));

			RangeCombatWeapon weapon = fighter.getSelectedRangeCombatWeapon();

			if (weapon != null) {
				String broken = weapon.isBroken() ? " (Broken), " : ", ";
				String mode = (weapon.getAmmunitions().size() > 1) ? String.format("Ammunition: %s, ", weapon
						.getCurrentAmmunition().getName()) : "";
				String numberOfSustainedFireDice = "";
				
				if (weapon.getCurrentAmmunition() != null) {
					ShotHandler shotHandler = weapon.getCurrentAmmunition().getShotHandler();
					
					if (shotHandler instanceof SustainedFireShotHandler) {
						SustainedFireShotHandler sustainedFireShotHandler = (SustainedFireShotHandler)shotHandler;
						numberOfSustainedFireDice = "Sustained Fire Dice: " + sustainedFireShotHandler.getNumberOfSustainedFireDice() + ", ";
					}
				}
				
				statusText.append(String.format("%s%s%s%s%s\n", weapon, broken, mode, numberOfSustainedFireDice, weapon.getProfileString()));
			}
			else {
				statusText.append("No weapon selected\n");
			}
		}
		else {
			statusText.append("\n\n");
		}

		if (Necromunda.getStatusMessage() != null) {
			statusText.append(String.format("%s\n", Necromunda.getStatusMessage()));
		}
		else {
			statusText.append("\n");
		}

		if (game.getPhase() != null) {
			statusText.append(game.getPhase().toString());
		}
		else {
			statusText.append(" ");
		}

		return statusText.toString();
	}

	public String getTerrainMaterialIdentifier() {
		return terrainType;
	}

	public void setTerrainMaterialIdentifier(String terrainType) {
		this.terrainType = terrainType;
	}

	public FighterNode getSelectedFighterNode() {
		return selectedFighterNode;
	}

	public RangeCombatWeapon getSelectedRangeCombatWeapon() {
		RangeCombatWeapon selectedRangeCombatWeapon = null;

		if (selectedFighterNode != null) {
			selectedRangeCombatWeapon = selectedFighterNode.getFighter().getSelectedRangeCombatWeapon();
		}

		return selectedRangeCombatWeapon;
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public TemplateNode getCurrentTemplateNode() {
		return currentTemplateNode;
	}

	public void setCurrentTemplateNode(TemplateNode currentTemplateNode) {
		this.currentTemplateNode = currentTemplateNode;
	}

	public List<FighterNode> getValidTargetFighterNodes(FighterNode source) {
		List<FighterNode> validTargetFighterNodes = new ArrayList<FighterNode>();

		List<FighterNode> visibleHostileFighterNodes = getVisibleFighterNodes(source, getHostileFighterNodes(getFighterNodes()));

		boolean normalFighterNodeFound = false;

		while ((!normalFighterNodeFound) && (!visibleHostileFighterNodes.isEmpty())) {
			FighterNode fighterNode = getNearestFighterNode(source, visibleHostileFighterNodes);
			Fighter fighter = fighterNode.getFighter();

			if (fighter.isNormal() || fighter.isPinned()) {
				normalFighterNodeFound = true;
			}

			validTargetFighterNodes.add(fighterNode);
			visibleHostileFighterNodes.remove(fighterNode);
		}

		return validTargetFighterNodes;
	}

	public List<FighterNode> getValidSustainedFireTargetFighterNodes() {
		return validSustainedFireTargetFighterNodes;
	}

	public void setValidSustainedFireTargetFighterNodes(List<FighterNode> validSustainedFireTargetFighterNodes) {
		this.validSustainedFireTargetFighterNodes = validSustainedFireTargetFighterNodes;
	}

	public List<FighterNode> getTargetedFighterNodes() {
		return targetedFighterNodes;
	}

	public void setTargetedFighterNodes(List<FighterNode> targetedFighterNodes) {
		this.targetedFighterNodes = targetedFighterNodes;
	}

	public RangeCombatWeapon getCurrentWeapon() {
		return currentWeapon;
	}

	public void setCurrentWeapon(RangeCombatWeapon currentWeapon) {
		this.currentWeapon = currentWeapon;
	}

	public Fighter getCurrentTarget() {
		return currentTarget;
	}

	public void setCurrentTarget(Fighter currentTarget) {
		this.currentTarget = currentTarget;
	}
}
