package necromunda;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.*;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import necromunda.Fighter.State;
import necromunda.MaterialFactory.MaterialIdentifier;
import weapons.*;

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
		DEPLOY_FIGHTER,
		DEPLOY_BUILDING,
		SELECT,
		MOVE,
		CLIMB,
		TARGET,
		REROLL
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
	private Node buildingsBoundsNode;
	private BuildingNode selectedBuildingNode;

	private Line currentPath;
	private ClimbPath currentClimbPath;
	private TemplateNode currentTemplateNode;
	private List<TemplateRemover> templateRemovers;
	private List<FighterNode> targetedFighterNodes;
	
	private RangeCombatWeapon currentWeapon;
	private Fighter currentTarget;

	private List<FighterNode> validSustainedFireTargetFighterNodes;
	
	private boolean rightButtonDown;

	private LinkedList<BuildingNode> buildingNodes;
	private LadderNode currentLadder;
	private List<LadderNode> currentLadders;

	private BitmapText statusMessage;

	private MaterialFactory materialFactory;
	
	private String terrainType;

	public Necromunda3dProvider(Necromunda game) {
		super(new StatsAppState(), new DebugKeysAppState());
		
		this.game = game;
		
		selectionMode = SelectionMode.DEPLOY_BUILDING;

		buildingsNode = new Node("buildingsNode");
		
		buildingsBoundsNode = new Node("buildingsBoundsNode");
		
		buildingNodes = new LinkedList<BuildingNode>();

		templateRemovers = new ArrayList<TemplateRemover>();

		targetedFighterNodes = new ArrayList<FighterNode>();

		validSustainedFireTargetFighterNodes = new ArrayList<FighterNode>();

		AppSettings settings = new AppSettings(false);
		settings.setTitle("Necromunda");
		settings.setSettingsDialogImage("/Images/Application/Splashscreen.png");
		settings.setFrameRate(60);
		//settings.setSamples(4);
		//settings.setVSync(true);
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
			// TODO Auto-generated catch block
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
		//shadowRenderer.setCompareMode(CompareMode.Hardware);
		viewPort.addProcessor(shadowRenderer);
		
		/*FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
		SSAOFilter ssaoFilter = new SSAOFilter();
		filterPostProcessor.addFilter(ssaoFilter);
		viewPort.addProcessor(filterPostProcessor);*/
		
		//rootNode.setShadowMode(ShadowMode.Off);
		
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

		Node tableNode = createTableNode();
		rootNode.attachChild(tableNode);
		
		buildingNodes = createBuildings(game.getBuildings());

		Node objectsNode = new Node("objectsNode");
		objectsNode.setShadowMode(ShadowMode.CastAndReceive);

		rootNode.attachChild(objectsNode);
		buildingsNode.setShadowMode(ShadowMode.CastAndReceive);
		rootNode.attachChild(buildingsNode);
		rootNode.attachChild(buildingsBoundsNode);

		setDisplayFps(true);
		setDisplayStatView(false);

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
		// Fix bug which sometimes culls the skybox
		sky.setLocalScale(100);
		rootNode.attachChild(sky);

		MouseListener mouseListener = new MouseListener();
		KeyboardListener keyboardListener = new KeyboardListener();
		
		stateManager.attach(new GeneralAppState(inputManager, mouseListener, keyboardListener));
		stateManager.attach(new RerollAppState(inputManager, mouseListener, keyboardListener));
		stateManager.getState(GeneralAppState.class).setEnabled(true);
	}

	private Node createTableNode() {
		Box box = new Box(new Vector3f(24, -0.5f, 24), 24, 0.5f, 24);
		//box.scaleTextureCoordinates(new Vector2f(10, 10));
		Geometry tableGeometry = new Geometry("tableGeometry", box);
		tableGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.TABLE));
		tableGeometry.setShadowMode(ShadowMode.Receive);

		Node tableNode = new Node("tableNode");
		tableNode.attachChild(tableGeometry);

		return tableNode;
	}

	private LinkedList<BuildingNode> createBuildings(List<Building> buildings) {
		LinkedList<BuildingNode> buildingNodes = new LinkedList<BuildingNode>();
		
		for (Building building : buildings) {
			BuildingNode buildingNode = createBuildingNode(building);
			buildingNodes.add(buildingNode);
		}
		
		return buildingNodes;
	}
	
	private BuildingNode createBuildingNode(Building building) {
		BuildingNode buildingNode = new BuildingNode("buildingNode");
		
		for (Entry<String, String> entry : building.getEntrySet()) {
			Spatial section = createBuildingSection(entry.getKey(), entry.getValue());

			buildingNode.attachChild(section);
			
			//Create ladders
			List<LadderNode> ladders = LadderNode.createLadders("/Ladders/" + entry.getKey() + ".ladder");
			
			for (LadderNode ladder : ladders) {
				buildingNode.attachChild(ladder);
			}
		}
		
		Node boundsNode = new Node("bounds");
		
		for (String identifier : building.getBounds()) {
			Node bound = (Node)assetManager.loadModel("Models/" + identifier + ".mesh.xml");

			for (Spatial spatial : bound.getChildren()) {
				boundsNode.attachChild((Geometry)spatial);
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
		for (LadderNode ladder : getLaddersFrom(buildingsNode)) {
			com.jme3.scene.shape.Line lineShape = new com.jme3.scene.shape.Line(Vector3f.ZERO, Vector3f.UNIT_Y);
			Geometry lineGeometry = new Geometry("line", lineShape);
			lineGeometry.setMaterial(materialFactory.createMaterial(MaterialFactory.MaterialIdentifier.SELECTED));
			ladder.attachChild(lineGeometry);
		}
	}
	
	private List<LadderNode> getLaddersFrom(Node buildingsNode) {
		List<LadderNode> ladders = new ArrayList<LadderNode>();
		
		List<Spatial> buildingNodes = buildingsNode.getChildren();
		
		for (Spatial buildingNode : buildingNodes) {
			ladders.addAll(((BuildingNode)buildingNode).getLadderNodes());
		}
		
		return ladders;
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		int millis = (int) (tpf * 1000);

		Iterator<TemplateRemover> it = templateRemovers.iterator();

		while (it.hasNext()) {
			TemplateRemover templateRemover = it.next();
			templateRemover.setTimer(templateRemover.getTimer() - millis);

			if (templateRemover.getTimer() < 0) {
				templateRemover.remove();
				it.remove();
			}
		}
		
		if (currentPath != null) {
			if (hasValidPosition(getCurrentPathBoxGeometry()) && hasValidPosition(selectedFighterNode.getBoundingVolume())) {
				getCurrentPathBoxGeometry().setMaterial(materialFactory.createMaterial(MaterialIdentifier.VALID_PATH));
			}
			else {
				getCurrentPathBoxGeometry().setMaterial(materialFactory.createMaterial(MaterialIdentifier.INVALID_PATH));
			}
		}
		
		updateModels();
		
		if (!(selectionMode.equals(SelectionMode.DEPLOY_FIGHTER) || (selectionMode.equals(SelectionMode.DEPLOY_BUILDING)))) {
			statusMessage.setText(getStatusTextFrom(game));
		}
	}
	
	private Node getCurrentPathBoxNode() {
		return (Node)rootNode.getChild("currentPathBoxNode");
	}
	
	private Geometry getCurrentPathBoxGeometry() {
		return (Geometry)getCurrentPathBoxNode().getChild("currentPathBoxGeometry");
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
	
				Node figureNode = (Node)fighterNode.getChild("figureNode");
				
				List<Spatial> children = figureNode.getChildren();
				
				for (Spatial spatial : children) {
					if (spatial.getName().equals("symbol")) {
						figureNode.detachChild(spatial);
					}
				}
	
				if (fighter.isPinned()) {
					fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_PINNED));
				}
				
				if (fighter.isDown()) {
					fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_DOWN));
				}
				
				if (fighter.isSedated()) {
					fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_SEDATED));
				}
				
				if (fighter.isComatose()) {
					fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_COMATOSE));
				}
				
				if (fighter.isWebbed()) {
					fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_WEBBED));
				}
				
				if (fighter.isHidden()) {
					fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_HIDDEN));
				}
				
				List<LadderNode> laddersInReach = getLaddersInReach(fighterNode.getLocalTranslation(), fighter.getBaseRadius());
	
				if (!laddersInReach.isEmpty()) {
					fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_LADDER));
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
				fighterNodes.add((FighterNode)child);
			}
		}
		
		return fighterNodes;
	}
	
	public List<TemplateNode> getTemplateNodes() {
		List<TemplateNode> templateNodes = new ArrayList<TemplateNode>();
		
		for (Spatial child : getObjectsNode().getChildren()) {
			if (child instanceof TemplateNode) {
				templateNodes.add((TemplateNode)child);
			}
		}
		
		return templateNodes;
	}

	private Node getObjectsNode() {
		return (Node)rootNode.getChild("objectsNode");
	}

	public Node getBuildingsNode() {
		return (Node)rootNode.getChild("buildingsNode");
	}
	
	public Node getBuildingsBoundsNode() {
		return (Node)rootNode.getChild("buildingsBoundsNode");
	}

	private Node getTableNode() {
		return (Node)rootNode.getChild("tableNode");
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
		if (getFighterNodeUnderCursor() == null) {
			return;
		}

		if (getValidSustainedFireTargetFighterNodes().isEmpty() && (!getValidTargetFighterNodesFor(selectedFighterNode).contains(getFighterNodeUnderCursor()))) {
			Necromunda.setStatusMessage("This fighter is not a valid target.");
			return;
		}

		if (validSustainedFireTargetFighterNodes.isEmpty()) {
			targetedFighterNodes.add(getFighterNodeUnderCursor());

			List<FighterNode> sustainedFireNeighbours = getFighterNodesWithinDistance(getFighterNodeUnderCursor(), getFighterNodes(), Necromunda.SUSTAINED_FIRE_RADIUS);
			sustainedFireNeighbours = getVisibleFighterNodes(selectedFighterNode, sustainedFireNeighbours);

			validSustainedFireTargetFighterNodes.add(getFighterNodeUnderCursor());
			validSustainedFireTargetFighterNodes.addAll(sustainedFireNeighbours);
			
			getSelectedRangeCombatWeapon().targetAdded();
		}
		else {
			if (validSustainedFireTargetFighterNodes.contains(getFighterNodeUnderCursor())) {
				targetedFighterNodes.add(getFighterNodeUnderCursor());
				getSelectedRangeCombatWeapon().targetAdded();
			}
			else {
				Necromunda.setStatusMessage("This target is not a valid target for sustained fire.");
			}
		}

		if (getSelectedRangeCombatWeapon().getNumberOfShots() > 0) {
			return;
		}
		
		List<Collidable> collidables = getTemplateBoundingVolumes();

		if (currentTemplateNode != null) {
			collidables.removeAll(currentTemplateNode.getBoundingVolumes());
		}

		collidables.add(getBuildingsBoundsNode());
		
		List<FighterNode> otherFighterNodes = new ArrayList<FighterNode>(getFighterNodes());
		otherFighterNodes.remove(selectedFighterNode);
		otherFighterNodes.remove(getFighterNodeUnderCursor());
		
		for (FighterNode otherFighterNode : otherFighterNodes) {
			collidables.add(otherFighterNode.getBoundingVolume());
		}
		
		RangeCombatWeapon weapon = getSelectedRangeCombatWeapon();

		for (FighterNode fighterNode : targetedFighterNodes) {
			VisibilityInfo visibilityInfo = getVisibilityInfo(selectedFighterNode, fighterNode.getCollisionShapePointCloud(), collidables);
			
			float visiblePercentage = visibilityInfo.getVisiblePercentage();

			Necromunda.appendToStatusMessage("Visible percentage: " + (visiblePercentage * 100));

			float distance = fighterNode.getLocalTranslation().distance(selectedFighterNode.getLocalTranslation());

			if (distance > weapon.getMaximumRange()) {
				Necromunda.appendToStatusMessage("Object out of range.");
				continue;
			}

			Fighter selectedFighter = selectedFighterNode.getFighter();

			int targetHitRoll = getTargetHitRoll(selectedFighter, weapon, distance, getHitModifier(visiblePercentage));

			if (targetHitRoll >= 10) {
				Necromunda.appendToStatusMessage(String.format("You need a %s to hit - impossible!", targetHitRoll));
				continue;
			}

			Necromunda.appendToStatusMessage(String.format("Target hit roll is %s.", targetHitRoll));

			int hitRoll = Utils.rollD6();
			
			Necromunda.appendToStatusMessage(String.format("Rolled a %s.", hitRoll));
			
			weapon.hitRoll(hitRoll);

			if ((targetHitRoll > 6) && (hitRoll == 6)) {
				targetHitRoll -= 3;
				hitRoll = Utils.rollD6();
			}

			if ((hitRoll < targetHitRoll) || (hitRoll <= 1)) {
				Necromunda.appendToStatusMessage("Shot missed...");
				
				if ((hitRoll == 1) && (Utils.rollD6() == 1)) {
					FighterNode strayShotFighterNode = getStrayShotFighterNode(selectedFighterNode, fighterNode);
					
					if (strayShotFighterNode != null) {
						fighterNode = getStrayShotFighterNode(selectedFighterNode, fighterNode);
						Necromunda.appendToStatusMessage(String.format("Stray shot hits %s.", fighterNode.getFighter().getName()));
					}
					else {
						continue;
					}
				}
				
				if (currentTemplateNode != null) {
					boolean hasEffect = true;

					if (weapon.isScattering()) {
						List<Collidable> scatterCollidables = new ArrayList<Collidable>();
						scatterCollidables.add(getBuildingsNode());
						Line lineOfSight = getLineOfSight(selectedFighterNode, getFighterNodeUnderCursor());
						hasEffect = currentTemplateNode.scatter(getLineLength(lineOfSight), scatterCollidables);
					}

					if (hasEffect) {
						fireTemplate(currentTemplateNode);
					}

					queueTemplateNodeForRemoval(currentTemplateNode);
					
					continue;
				}
			}
			else {
				Necromunda.appendToStatusMessage("Shot hit!");
			}

			if (currentTemplateNode != null) {
				fireTemplate(currentTemplateNode);
				queueTemplateNodeForRemoval(currentTemplateNode);
			}
			else {
				List<FighterNode> affectedFighterNodes = new ArrayList<FighterNode>();

				affectedFighterNodes.add(fighterNode);

				if (weapon.getAdditionalTargetRange() > 0) {
					List<FighterNode> fighterNodesWithinRange = getFighterNodesWithinDistance(fighterNode, getFighterNodes(), weapon.getAdditionalTargetRange());
					List<FighterNode> visibleFighterNodes = getVisibleFighterNodes(selectedFighterNode, fighterNodesWithinRange);

					affectedFighterNodes.addAll(visibleFighterNodes);
				}

				pinNormalFighters(affectedFighterNodes);

				for (FighterNode affectedFighterNode : affectedFighterNodes) {
					Fighter fighter = affectedFighterNode.getFighter();
					
					if (!weapon.dealDamageTo(fighter)) {
						currentWeapon = weapon;
						currentTarget = fighter;
						selectionMode = SelectionMode.REROLL;
						Necromunda.appendToStatusMessage("Re-roll wound roll?");
					}
				}
			}
		}
		
		getSelectedRangeCombatWeapon().trigger();

		tearDownTargeting();

		getTargetedFighterNodes().clear();
		getValidSustainedFireTargetFighterNodes().clear();
		
		if (getSelectionMode() != SelectionMode.REROLL) {
			setSelectionMode(SelectionMode.SELECT);
		}
		else {
			stateManager.getState(GeneralAppState.class).setEnabled(false);
			stateManager.getState(RerollAppState.class).setEnabled(true);
		}
	}
	
	private int getHitModifier(float visiblePercentage) {
		int hitModifier = 0;
		
		if ((visiblePercentage < 1.0) && (visiblePercentage >= 0.5)) {
			hitModifier = -1;
		}
		else if (visiblePercentage < 0.5) {
			hitModifier = -2;
		}
		
		return hitModifier;
	}
	
	private int getTargetHitRoll(Fighter fighter, RangeCombatWeapon weapon, float distance, int hitModifier) {
		int targetHitRoll = 7 - fighter.getBallisticSkill() - weapon.getRangeModifier(distance) - hitModifier;
		return targetHitRoll;
	}
	
	private FighterNode getStrayShotFighterNode(FighterNode source, FighterNode target) {
		FighterNode strayShotFighterNode = null;
		List<FighterNode> strayShotCandidates = new ArrayList<FighterNode>();
		
		LineSegment lineSegment = new LineSegment(source.getLocalTranslation(), target.getLocalTranslation());
		
		for (FighterNode fighterNode : getFighterNodes()) {
			if (source.getFighter().getGang().getGangMembers().contains(fighterNode.getFighter()) && (fighterNode != source)) {
				Vector3f point = fighterNode.getLocalTranslation();
				Vector3f projectedPoint = point.project(lineSegment.getPositiveEnd(null).subtract(lineSegment.getNegativeEnd(null))).add(source.getLocalTranslation());
				
				if ((lineSegment.distance(projectedPoint) < 0.01f) && ((lineSegment.distance(point) - fighterNode.getFighter().getBaseRadius()) <= 0.5f)) {
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
		Node boundsNode = (Node)buildingNode.getChild("bounds");
		
		buildingNode.setLocalTranslation(nearestIntersection);
		boundsNode.setLocalTransform(buildingNode.getLocalTransform());
		
		buildingsNode.attachChild(buildingNode);
		buildingsBoundsNode.attachChild(boundsNode);
	}
	
	private void skipBuilding() {
		Vector3f nearestIntersection = getTableCollisionPoint();

		if (nearestIntersection == null) {
			nearestIntersection = selectedBuildingNode.getLocalTranslation();
		}
		
		buildingsNode.detachChild(selectedBuildingNode);

		selectedBuildingNode = buildingNodes.poll();

		if (selectedBuildingNode == null) {
			if (PHYSICS_DEBUG_ENABLED) {
				createLadderLines();
			}
			
			selectionMode = SelectionMode.DEPLOY_FIGHTER;
			updateModelPosition(nearestIntersection);
		}
		else {
			selectedBuildingNode.setLocalTranslation(nearestIntersection);
			buildingsNode.attachChild(selectedBuildingNode);
		}
	}
	
	private void updateModelPosition(Vector3f position) {
		if (selectedFighterNode == null) {
			Fighter fighter = game.getNextFighter();
			
			if (fighter != null) {
				selectedFighterNode = new FighterNode("fighterNode", fighter, materialFactory);
				getObjectsNode().attachChild(selectedFighterNode);
			}
			else {
				game.deploymentFinished();
				selectionMode = SelectionMode.SELECT;
			}
		}
		
		if (selectedFighterNode != null) {
			selectedFighterNode.setLocalTranslation(position);
		}
	}

	private void deployFighter() {
		Vector3f contactPoint = getSceneryCollisionPoint();
		
		/*List<Vector3f> pointCloud = selectedFighterNode.getCollisionShapePointCloud();
		
		Material material = materialFactory.createMaterial(MaterialIdentifier.SELECTED);
		
		for (Vector3f vector : pointCloud) {
			Quad quad = new Quad(0.01f, 0.01f);
			Geometry geometry = new Geometry("cloudpoint", quad);
			geometry.setMaterial(material);
			geometry.setLocalTranslation(vector);
			rootNode.attachChild(geometry);
		}*/
		
		if ((contactPoint != null) && (selectedFighterNode != null) && hasValidPosition(selectedFighterNode.getBoundingVolume())) {
			selectedFighterNode = null;
			updateModelPosition(contactPoint);
		}
	}

	private void select() {
		selectedFighterNode = getFighterNodeUnderCursor();
	}

	private void move() {
		if (hasValidPosition(selectedFighterNode.getBoundingVolume()) && hasValidPosition(getCurrentPathBoxGeometry())) {
			List<FighterNode> fighterNodes = getHostileFighterNodesFrom(getFighterNodes());
			fighterNodes = getFighterNodesWithinDistance(selectedFighterNode, fighterNodes, Necromunda.RUN_SPOT_DISTANCE);
			fighterNodes = getVisibleFighterNodes(selectedFighterNode, fighterNodes);

			if (selectedFighterNode.getFighter().isGoingToRun() && (!fighterNodes.isEmpty())) {
				Necromunda.setStatusMessage("You cannot run so close to an enemy fighter.");
			}
			else {
				commitMovement();
			}
		}
	}

	private void climb() {
		if (hasValidPosition(selectedFighterNode.getBoundingVolume())) {
			if (currentClimbPath.getLength() <= selectedFighterNode.getFighter().getRemainingMovementDistance()) {
				commitClimb();
			}
			else {
				Necromunda.setStatusMessage("This ganger cannot climb that far.");
			}
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
			selectedFighterNode.getFighter().setRemainingMovementDistance(0);
			selectionMode = SelectionMode.SELECT;
		}
		else {
			Necromunda.setStatusMessage("This fighter cannot hide here.");
		}
	}
	
	private boolean isHideable(FighterNode fighterNode) {
		List<Collidable> collidables = getTemplateBoundingVolumes();
		collidables.add(getBuildingsBoundsNode());
		
		boolean hideable = true;
		
		List<FighterNode> hostileFighterNodes = getHostileFighterNodesFrom(getFighterNodes());
		
		for (FighterNode hostileFighterNode : hostileFighterNodes) {
			VisibilityInfo fighterVisibilityInfo = getVisibilityInfo(hostileFighterNode, fighterNode.getCollisionShapePointCloud(), collidables);
			
			if (fighterVisibilityInfo.getNumberOfPoints() == fighterVisibilityInfo.getNumberOfVisiblePoints()) {
				hideable = false;
				break;
			}
			
			//VisibilityInfo pathVisibilityInfo = getVisibilityInfo(fighterNode, getPathBoxNodePointCloud(currentPathBoxNode), collidables);
			// TODO: Seems to be impossible to check if a fighter was completely visible at a certain point on his path
			
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
		
		CollisionResult closestCollision = Utils.getNearestCollisionFrom(cam.getLocation(), cam.getDirection(), collidables);

		FighterNode fighterNodeUnderCursor = null;

		if (closestCollision != null) {
			Geometry geometry = closestCollision.getGeometry();
			fighterNodeUnderCursor = (FighterNode)getParent(geometry, "fighterNode");
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

		List<FighterNode> sustainedFireNeighbours = getFighterNodesWithinDistance(getFighterNodeUnderCursor(), getFighterNodes(), Necromunda.SUSTAINED_FIRE_RADIUS);
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

		if (getCurrentPathBoxNode() != null) {
			rootNode.detachChild(getCurrentPathBoxNode());
		}

		currentPath = null;
	}

	private void abortClimbing() {
		if (currentClimbPath != null) {
			selectedFighterNode.setLocalTranslation(currentClimbPath.getStart());
		}

		currentClimbPath = null;
	}

	private void tearDownTargeting() {
		rootNode.detachChildNamed("currentLineOfSightLine");
		removeCurrentWeaponTemplate();
	}

	private void removeCurrentWeaponTemplate() {
		rootNode.detachChildNamed("currentTemplateNode");
		currentTemplateNode = null;
	}

	private void commitMovement() {
		Fighter selectedFighter = selectedFighterNode.getFighter();

		Vector3f movementVector = currentPath.getVector();
		float distance = movementVector.length();
		float remainingMovementDistance = selectedFighter.getRemainingMovementDistance() - distance;
		selectedFighter.setRemainingMovementDistance(remainingMovementDistance);

		currentPath.getOrigin().set(selectedFighterNode.getLocalTranslation());

		for (Fighter object : selectedFighter.getGang().getGangMembers()) {
			if (object != selectedFighter) {
				if (object.hasMoved() || object.hasRun()) {
					object.setRemainingMovementDistance(0);
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
			selectedFighter.setRemainingMovementDistance(0);
			selectionMode = SelectionMode.SELECT;
		}

		if (selectedFighter.getRemainingMovementDistance() < 0.01f) {
			selectedFighter.setRemainingMovementDistance(0);
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
		collidables.add(getBuildingsBoundsNode());
		
		List<FighterNode> hostileFighterNodes = getHostileFighterNodesFrom(getFighterNodes());
		
		for (FighterNode fighterNode : hostileFighterNodes) {
			VisibilityInfo visibilityInfo = getVisibilityInfo(selectedFighterNode, fighterNode.getCollisionShapePointCloud(), collidables);
			
			if (visibilityInfo.getNumberOfPoints() == visibilityInfo.getNumberOfVisiblePoints()) {
				fighterNode.getFighter().setHidden(false);
			}
		}
		
		List<FighterNode> fighterNodesWithinInitiativeRange = getFighterNodesWithinDistance(selectedFighterNode, hostileFighterNodes, selectedFighterNode.getFighter().getInitiative()); 
		
		for (FighterNode fighterNode : fighterNodesWithinInitiativeRange) {
			fighterNode.getFighter().setHidden(false);
		}
	}

	private void commitClimb() {
		Fighter selectedFighter = selectedFighterNode.getFighter();

		float distance = currentClimbPath.getLength();
		float remainingMovementDistance = selectedFighter.getRemainingMovementDistance() - distance;
		selectedFighter.setRemainingMovementDistance(remainingMovementDistance);

		currentClimbPath.getStart().set(selectedFighterNode.getLocalTranslation());

		for (Fighter object : selectedFighter.getGang().getGangMembers()) {
			if (object != selectedFighter) {
				if (object.hasMoved() || object.hasRun()) {
					object.setRemainingMovementDistance(0);
				}
			}
		}

		if (selectedFighter.isGoingToRun()) {
			selectedFighter.setHasRun(true);
		}
		else {
			selectedFighter.setHasMoved(true);
		}

		if (selectedFighter.getRemainingMovementDistance() < 0.01f) {
			selectedFighter.setRemainingMovementDistance(0);
		}

		abortClimbing();

		selectionMode = SelectionMode.SELECT;
		
		revealHiddenFighters();
		
		if (!isHideable(selectedFighterNode)) {
			selectedFighterNode.getFighter().setHidden(false);
		}
	}

	private Geometry getPathBoxGeometryFor(Vector3f halfExtents) {
		Box box = new Box(halfExtents.getX(), halfExtents.getY(), halfExtents.getZ());
		Geometry boxGeometry = new Geometry("currentPathBoxGeometry", box);
		boxGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.VALID_PATH));
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
		MyBox boundingBox = (MyBox)fighterNode.getBoundingVolume().getMesh();
		Vector3f halfExtents = new Vector3f(boundingBox.xExtent, boundingBox.yExtent, boundingBox.zExtent);

		return halfExtents;
	}

	private void setBaseSelected(FighterNode fighterNode) {
		Material baseMaterial = materialFactory.createMaterial(MaterialIdentifier.SELECTED);
		fighterNode.setBaseMaterial(baseMaterial);
	}

	private void setBaseTargeted(FighterNode fighterNode) {
		Material baseMaterial = materialFactory.createMaterial(MaterialIdentifier.TARGETED);
		fighterNode.setBaseMaterial(baseMaterial);
	}

	private void setBaseNormal(FighterNode fighterNode) {
		Material baseMaterial = materialFactory.createMaterial(MaterialIdentifier.NORMAL);
		fighterNode.setBaseMaterial(baseMaterial);
	}
	
	private boolean hasValidPosition(Geometry boundingVolume) {
		boolean intersectsFighter = false;
		boolean intersectsBuilding = false;
		
		for (FighterNode otherFighterNode : getFighterNodes()) {
			if ((boundingVolume != otherFighterNode.getBoundingVolume()) && (otherFighterNode.getBoundingVolume() != selectedFighterNode.getBoundingVolume())) {
				if(Utils.intersect(boundingVolume, otherFighterNode.getBoundingVolume())) {
					intersectsFighter = true;
					break;
				}
			}
		}
		
		for (Spatial spatial : getBuildingsBoundsNode().getChildren()) {
			Node buildingNode = (Node)spatial;
			
			for (Spatial boundsGeometry : buildingNode.getChildren()) {
				Geometry bounds = (Geometry)boundsGeometry;
				
				if (Utils.intersect(boundingVolume, bounds)) {
					intersectsBuilding = true;
					break;
				}
			}
		}
		
		if (intersectsFighter || intersectsBuilding) {
			System.out.println("Position invalid...");
			return false;
		}
		else {
			return true;
		}
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
		crosshair.setLocalTranslation(settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2, settings.getHeight() / 2
				+ crosshair.getLineHeight() / 2, 0);
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
	
			CollisionResult closestCollision = Utils.getNearestCollisionFrom(sourceLocation, direction, collidables);
	
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
		CollisionResult closestCollision = Utils.getNearestCollisionFrom(cam.getLocation(), cam.getDirection(), collidables);
		
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
		collidables.add(getBuildingsBoundsNode());
		CollisionResult closestCollision = Utils.getNearestCollisionFrom(cam.getLocation(), cam.getDirection(), collidables);

		if ((closestCollision != null) && closestCollision.getContactNormal().angleBetween(Vector3f.UNIT_Y) <= MAX_COLLISION_NORMAL_ANGLE) {
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

		if (closestCollision != null && closestCollision.getContactNormal().angleBetween(Vector3f.UNIT_Y) <= MAX_COLLISION_NORMAL_ANGLE) {
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
			if (selectionMode == SelectionMode.MOVE) {
				setUpMovement();
			}
			else if (selectionMode == SelectionMode.TARGET) {
				setUpTargeting();
			}
			else if (selectionMode == SelectionMode.DEPLOY_FIGHTER) {
				Vector3f nearestIntersection = getSceneryCollisionPoint();
				
				if (nearestIntersection == null) {
					return;
				}
				
				updateModelPosition(nearestIntersection);
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
				else {
					Vector3f nearestIntersection = getTableCollisionPoint();
	
					if (nearestIntersection == null) {
						return;
					}
					
					if (selectedBuildingNode == null) {
						selectedBuildingNode = buildingNodes.poll();
						buildingsNode.attachChild(selectedBuildingNode);
					}
					
					selectedBuildingNode.setLocalTranslation(nearestIntersection);
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

		@Override
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
		else if ((selectionMode == SelectionMode.DEPLOY_BUILDING) && name.equals("SkipBuilding")) {
			skipBuilding();
		}
		else if (selectionMode == SelectionMode.REROLL) {
			if (name.equals("Yes")) {
				currentWeapon.dealDamageTo(currentTarget);
				selectionMode = SelectionMode.SELECT;
			}
			else if (name.equals("No")) {
				selectionMode = SelectionMode.SELECT;
			}
			
			stateManager.getState(RerollAppState.class).setEnabled(false);
			stateManager.getState(GeneralAppState.class).setEnabled(true);
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
							selectedFighter.setIsGoingToRun(false);
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
							selectedFighter.setIsGoingToRun(true);
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
								weapon.resetNumberOfShots();

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
					List<Weapon> weapons = selectedFighter.getWeapons();

					if (!weapons.isEmpty()) {
						RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();

						if (weapon == null) {
							weapon = (RangeCombatWeapon) selectedFighter.getWeapons().get(0);
						}
						else {
							int index = weapons.indexOf(weapon);

							if (index < weapons.size() - 1) {
								weapon = (RangeCombatWeapon) weapons.get(index + 1);
							}
							else {
								weapon = (RangeCombatWeapon) selectedFighter.getWeapons().get(0);
							}
						}

						selectedFighter.setSelectedRangeCombatWeapon(weapon);
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

	private class TemplateRemover {
		private TemplateNode temporaryWeaponTemplate;
		private int timeToRemove;

		public TemplateRemover(TemplateNode temporaryWeaponTemplate) {
			this.temporaryWeaponTemplate = temporaryWeaponTemplate;
			this.timeToRemove = 2000;
		}

		public void remove() {
			rootNode.detachChild(temporaryWeaponTemplate);
		}

		public int getTimer() {
			return timeToRemove;
		}

		public void setTimer(int timer) {
			this.timeToRemove = timer;
		}
	}
	
	private List<LadderNode> getLaddersInReach(Vector3f origin, float baseRadius) {
		List<LadderNode> laddersInReach = new ArrayList<LadderNode>();
		
		for (LadderNode ladder : (List<LadderNode>)getLaddersFrom(buildingsNode)) {
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
				List<FighterNode> surroundingFighterNodes = getFighterNodesWithinDistance(fighterNode, getFighterNodes(), Necromunda.UNPIN_BY_INITIATIVE_DISTANCE);
				List<Fighter> reliableMates = new ArrayList<Fighter>();

				for (FighterNode surroundingFighterNode : surroundingFighterNodes) {
					Fighter surroundingFighter = surroundingFighterNode.getFighter();

					if (fighter.getGang().getGangMembers().contains(surroundingFighter) && surroundingFighter.isReliableMate()) {
						reliableMates.add(surroundingFighter);
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
		removeTemplateTrails();
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
		}
		else {
			objectPosition = currentPath.getOrigin();
		}

		if (nearestIntersection == null) {
			return;
		}

		float slope = FastMath.abs(nearestIntersection.getY() - objectPosition.getY());

		if (slope > MAX_SLOPE) {
			return;
		}

		if (currentPath == null) {
			currentPath = new Line(objectPosition.clone(), nearestIntersection);
		}

		Vector3f movementVector = nearestIntersection.subtract(currentPath.getOrigin());
		float distance = movementVector.length();
		float remainingMovementDistance = selectedFighterNode.getFighter().getRemainingMovementDistance();

		if (distance > remainingMovementDistance) {
			movementVector.normalizeLocal().multLocal(remainingMovementDistance);
		}

		currentPath.getDirection().set(currentPath.getOrigin().add(movementVector));
	}

	private void updateCurrentPathBox() {
		if (getCurrentPathBoxNode() == null) {
			Node currentPathBoxNode = new Node("currentPathBoxNode");	
			rootNode.attachChild(currentPathBoxNode);
		}
		else {
			getCurrentPathBoxNode().detachChildNamed("currentPathBoxGeometry");
		}
		
		Vector3f pathHalfExtents = getPathHalfExtents(selectedFighterNode, currentPath);
		Geometry pathBoxGeometry = getPathBoxGeometryFor(pathHalfExtents);

		getCurrentPathBoxNode().attachChild(pathBoxGeometry);

		Vector3f vector = currentPath.getOrigin().add(currentPath.getVector().mult(0.5f)).addLocal(selectedFighterNode.getLocalCenter());
		getCurrentPathBoxNode().setLocalTranslation(vector);
		getCurrentPathBoxNode().lookAt(currentPath.getDirection().add(selectedFighterNode.getLocalCenter()), Vector3f.UNIT_Y);

		selectedFighterNode.setLocalTranslation(currentPath.getDirection());
	}
	
	private List<Vector3f> getPathBoxNodePointCloud(Node pathBoxNode) {
		List<Vector3f> pointCloud = new ArrayList<Vector3f>();
		
		GhostControl ghostControl = pathBoxNode.getControl(GhostControl.class);
		BoxCollisionShape collisionShape = (BoxCollisionShape)ghostControl.getCollisionShape();
		
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
				currentTemplateNode = TemplateNode.createTemplateNode(assetManager, getSelectedRangeCombatWeapon().getCurrentAmmunition());
			}

			if (getSelectedRangeCombatWeapon().isTemplateAttached()) {
				Vector3f lineOfSightVector = line.getDirection().subtract(line.getOrigin());
				lineOfSightVector = lineOfSightVector.normalize();
				
				float angle = FastMath.acos(lineOfSightVector.x);
				
				if (lineOfSightVector.z < 0) {
					angle = -angle;
				}
				
				Matrix3f rotationMatrix = new Matrix3f(FastMath.cos(angle), 0, -FastMath.sin(angle), 0, 1, 0, FastMath.sin(angle), 0, FastMath.cos(angle));
				currentTemplateNode.setLocalRotation(rotationMatrix);
				currentTemplateNode.setLocalTranslation(selectedFighterNode.getLocalCenter().add(selectedFighterNode.getLocalTranslation()));
			}
			else {
				currentTemplateNode.setLocalTranslation(line.getDirection());
			}

			rootNode.detachChildNamed("currentTemplateNode");
			rootNode.attachChild(currentTemplateNode);
		}
	}
	
	private boolean isTargetUnderCursor() {
		return (getFighterNodeUnderCursor() != null) && (selectedFighterNode != getFighterNodeUnderCursor());
	}
	
	private Line getLineOfSight(FighterNode source, FighterNode target) {
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

	private float getLineLength(Line line) {
		return line.getOrigin().distance(line.getDirection());
	}

	public List<FighterNode> getHostileFighterNodesFrom(List<FighterNode> fighterNodes) {
		List<FighterNode> hostileFighterNodes = new ArrayList<FighterNode>();

		for (FighterNode fighterNode : fighterNodes) {
			Fighter fighter = fighterNode.getFighter();

			if (game.getHostileGangers().contains(fighter)) {
				hostileFighterNodes.add(fighterNode);
			}
		}

		return hostileFighterNodes;
	}

	private List<FighterNode> getVisibleFighterNodes(FighterNode source, List<FighterNode> fighterNodes) {
		List<FighterNode> visibleFighterNodes = new ArrayList<FighterNode>();

		List<Collidable> collidables = getTemplateBoundingVolumes();
		collidables.add(getBuildingsBoundsNode());

		for (FighterNode fighterNode : fighterNodes) {
			VisibilityInfo visibilityInfo = getVisibilityInfo(source, fighterNode.getCollisionShapePointCloud(), collidables);
			
			if ((visibilityInfo.getNumberOfVisiblePoints() > 0) && !fighterNode.getFighter().isHidden()) {
				visibleFighterNodes.add(fighterNode);
			}
		}

		return visibleFighterNodes;
	}

	private FighterNode getNearestFighterNodeFrom(FighterNode source, List<FighterNode> fighterNodes) {
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

	private List<FighterNode> getFighterNodesWithinDistance(FighterNode fighterNode, List<FighterNode> fighterNodes, float maxDistance) {
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
			Geometry fighterBoundingBox = fighterNode.getBoundingVolume();

			for (Geometry templateBoundingBox : templateNode.getBoundingVolumes()) {
				if (Utils.intersect(fighterBoundingBox, templateBoundingBox)) {
					fighterNodesUnderTemplate.add(fighterNode);
					break;
				}
			}
		}

		return fighterNodesUnderTemplate;
	}

	private void queueTemplateNodeForRemoval(TemplateNode templateNode) {
		if (!currentTemplateNode.isTemplatePersistent()) {
			templateNode.setName("temporaryTemplateNode");
			TemplateRemover templateRemover = new TemplateRemover(templateNode);
			templateRemovers.add(templateRemover);
		}
		else {
			templateNode.setName("persistentTemplateNode");
			getObjectsNode().attachChild(templateNode);
		}
	}

	public void fireTemplate(TemplateNode templateNode) {
		List<FighterNode> affectedFighterNodes = getFighterNodesUnderTemplate(templateNode, getFighterNodes());
		pinNormalFighters(affectedFighterNodes);
		templateNode.dealDamageTo(affectedFighterNodes);
	}

	private void pinNormalFighters(List<FighterNode> fighterNodes) {
		for (FighterNode fighterNode : fighterNodes) {
			Fighter fighter = fighterNode.getFighter();

			if (fighter.isNormal()) {
				fighter.setState(State.PINNED);
			}
		}
	}

	private String getStatusTextFrom(Necromunda game) {
		StringBuilder statusText = new StringBuilder();

		String string = String.format("Turn %s, %s\n", game.getTurn(), game.getCurrentGang().toString());
		statusText.append(string);

		if (selectedFighterNode != null) {
			Fighter fighter = selectedFighterNode.getFighter();
			statusText.append(String.format("%s, %s, %sFlesh Wounds: %s\n", fighter.getName(), fighter.getState(), (fighter.isWebbed() ? "Webbed, " : ""), fighter
					.getFleshWounds()));

			RangeCombatWeapon weapon = fighter.getSelectedRangeCombatWeapon();

			if (weapon != null) {
				String broken = weapon.isBroken() ? " (Broken), " : ", ";
				String mode = (weapon.getAmmunitions().size() > 1) ? String.format(" Ammunition: %s, ", weapon.getCurrentAmmunition().getName()) : "";
				statusText.append(String.format("%s%s%s%s\n", weapon, broken, mode, weapon.getProfileString()));
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

	public String getTerrainType() {
		return terrainType;
	}

	public void setTerrainType(String terrainType) {
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

	public List<FighterNode> getValidTargetFighterNodesFor(FighterNode source) {
		List<FighterNode> validTargetFighterNodes = new ArrayList<FighterNode>();
		
		List<FighterNode> visibleHostileFighterNodes = getVisibleFighterNodes(source, getHostileFighterNodesFrom(getFighterNodes()));

		boolean normalFighterNodeFound = false;

		while ((!normalFighterNodeFound) && (!visibleHostileFighterNodes.isEmpty())) {
			FighterNode fighterNode = getNearestFighterNodeFrom(source, visibleHostileFighterNodes);
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
}
