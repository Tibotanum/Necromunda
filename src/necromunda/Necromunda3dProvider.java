package necromunda;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import javax.imageio.ImageIO;

import java.util.logging.Level;
import java.util.logging.Logger;

import necromunda.Fighter.State;
import necromunda.MaterialFactory.MaterialIdentifier;
import necromunda.Necromunda.Phase;
import necromunda.Necromunda.SelectionMode;
import weapons.Ammunition;
import weapons.RangeCombatWeapon;
import weapons.Weapon;
import weapons.WebPistol;
import weapons.RangeCombatWeapon.WeaponType;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.plugins.NeoTextureMaterialKey;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Rectangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.CompareMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Necromunda3dProvider extends SimpleApplication implements Observer {
	public static final float MAX_COLLISION_NORMAL_ANGLE = 0.05f;
	public static final float MAX_SLOPE = 0.05f;
	public static final float NOT_TOUCH_DISTANCE = 0.01f;
	public static final float MAX_LADDER_DISTANCE = 0.5f;
	public static final boolean ENABLE_PHYSICS_DEBUG = false;
	public static final Vector3f GROUND_BUFFER = new Vector3f(0, NOT_TOUCH_DISTANCE, 0);
	private Necromunda game;
	
	private boolean invertMouse;
	
	private FighterNode selectedFighterNode;
	private List<FighterNode> validTargetFighterNodes;
	private List<FighterNode> fighterNodes;
	
	private Node buildingsNode;
	private Node selectedBuildingNode;

	private RigidBodyControl buildingsControl;
	private Line currentPath;
	private ClimbPath currentClimbPath;
	private Node currentPathBoxNode;
	private Line currentLineOfSight;
	private Node currentLineOfSightBoxNode;
	private TemplateNode currentTemplateNode;
	private List<TemplateNode> templateNodes;
	private List<TemplateRemover> templateRemovers;
	private boolean physicsTickLock1;
	private boolean physicsTickLock2;
	private List<FighterNode> targetedFighterNodes;

	private List<FighterNode> validSustainedFireTargetFighterNodes;
	
	private boolean rightButtonDown;

	private LinkedList<Node> buildingNodes;
	private LadderNode currentLadder;
	private List<LadderNode> currentLadders;

	private BitmapText statusMessage;

	private MaterialFactory materialFactory;
	
	private String terrainType;

	public Necromunda3dProvider(Necromunda game) {
		super(new StatsAppState(), new DebugKeysAppState());
		
		this.game = game;

		fighterNodes = new ArrayList<FighterNode>();

		buildingsNode = new Node("buildingsNode");
		
		buildingNodes = new LinkedList<Node>();

		templateNodes = new ArrayList<TemplateNode>();

		templateRemovers = new ArrayList<TemplateRemover>();

		targetedFighterNodes = new ArrayList<FighterNode>();

		validSustainedFireTargetFighterNodes = new ArrayList<FighterNode>();

		validTargetFighterNodes = new ArrayList<FighterNode>();

		AppSettings settings = new AppSettings(false);
		settings.setTitle("Necromunda");
		settings.setSettingsDialogImage("/Textures/Splashscreen01.png");
		settings.setFrameRate(60);
		//settings.setSamples(4);
		//settings.setVSync(true);
		settings.setIcons(createFrameIcons());
		setSettings(settings);
	}
	
	private BufferedImage[] createFrameIcons() {
		List<BufferedImage> iconImages = new ArrayList<BufferedImage>();
		
		try {
			iconImages.add(ImageIO.read(getClass().getResource("/Textures/Orlock_Logo_tiny.png")));
			iconImages.add(ImageIO.read(getClass().getResource("/Textures/Orlock_Logo_small.png")));
			iconImages.add(ImageIO.read(getClass().getResource("/Textures/Orlock_Logo_medium.png")));
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
		
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

		ScreenshotAppState screenshotAppState = new ScreenshotAppState();
		stateManager.attach(screenshotAppState);

		assetManager.registerLocator("", ClasspathLocator.class.getName());
		assetManager.registerLoader("com.jme3.material.plugins.NeoTextureMaterialLoader", "tgr");

		materialFactory = new MaterialFactory(assetManager, this);

		Node tableNode = createTableNode();
		rootNode.attachChild(tableNode);
		
		createBuildings();

		PhysicsSpace physicsSpace = getPhysicsSpace();
		physicsSpace.addCollisionListener(new PhysicsCollisionListenerImpl());
		physicsSpace.addTickListener(new PhysicsTickListenerImpl());

		Node objectsNode = new Node("objectsNode");
		objectsNode.setShadowMode(ShadowMode.CastAndReceive);

		rootNode.attachChild(objectsNode);
		buildingsNode.setShadowMode(ShadowMode.CastAndReceive);
		rootNode.attachChild(buildingsNode);

		setDisplayFps(false);
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

		TextureKey key0 = new TextureKey("Textures/sky_top_bottom.PNG", true);
		key0.setGenerateMips(true);
		key0.setAsCube(true);
		Texture tex0 = assetManager.loadTexture(key0);

		TextureKey key1 = new TextureKey("Textures/sky_left.PNG", true);
		key1.setGenerateMips(true);
		key1.setAsCube(true);
		Texture tex1 = assetManager.loadTexture(key1);

		TextureKey key2 = new TextureKey("Textures/sky_right.PNG", true);
		key2.setGenerateMips(true);
		key2.setAsCube(true);
		Texture tex2 = assetManager.loadTexture(key2);

		TextureKey key3 = new TextureKey("Textures/sky_front.PNG", true);
		key3.setGenerateMips(true);
		key3.setAsCube(true);
		Texture tex3 = assetManager.loadTexture(key3);

		TextureKey key4 = new TextureKey("Textures/sky_back.PNG", true);
		key4.setGenerateMips(true);
		key4.setAsCube(true);
		Texture tex4 = assetManager.loadTexture(key4);

		Geometry sky = (Geometry) SkyFactory.createSky(assetManager, tex1, tex2, tex3, tex4, tex0, tex0);
		// Fix bug which sometimes culls the skybox
		sky.setLocalScale(100);
		rootNode.attachChild(sky);

		MouseListener mouseListener = new MouseListener();

		inputManager.addMapping("leftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping("rightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addListener(mouseListener, "leftClick");
		inputManager.addListener(mouseListener, "rightClick");

		inputManager.addMapping("Move_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("Move_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("Move_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping("Move_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addListener(mouseListener, "Move_Left", "Move_Right", "Move_Up", "Move_Down");

		KeyboardListener keyboardListener = new KeyboardListener();

		inputManager.addMapping("Break", new KeyTrigger(KeyInput.KEY_B));
		inputManager.addListener(keyboardListener, "Break");

		inputManager.addMapping("Move", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(keyboardListener, "Move");

		inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_R));
		inputManager.addListener(keyboardListener, "Run");
		
		inputManager.addMapping("Hide", new KeyTrigger(KeyInput.KEY_I));
		inputManager.addListener(keyboardListener, "Hide");

		inputManager.addMapping("Climb", new KeyTrigger(KeyInput.KEY_C));
		inputManager.addListener(keyboardListener, "Climb");

		inputManager.addMapping("Cycle", new KeyTrigger(KeyInput.KEY_Y));
		inputManager.addListener(keyboardListener, "Cycle");

		inputManager.addMapping("Yes", new KeyTrigger(KeyInput.KEY_Y));
		inputManager.addListener(keyboardListener, "Yes");

		inputManager.addMapping("Mode", new KeyTrigger(KeyInput.KEY_O));
		inputManager.addListener(keyboardListener, "Mode");

		inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_H));
		inputManager.addListener(keyboardListener, "Shoot");

		inputManager.addMapping("NextPhase", new KeyTrigger(KeyInput.KEY_N));
		inputManager.addListener(keyboardListener, "NextPhase");

		inputManager.addMapping("No", new KeyTrigger(KeyInput.KEY_N));
		inputManager.addListener(keyboardListener, "No");

		inputManager.addMapping("EndTurn", new KeyTrigger(KeyInput.KEY_E));
		inputManager.addListener(keyboardListener, "EndTurn");
		
		inputManager.addMapping("SkipBuilding", new KeyTrigger(KeyInput.KEY_K));
		inputManager.addListener(keyboardListener, "SkipBuilding");
		
		if (ENABLE_PHYSICS_DEBUG) {
			physicsSpace.enableDebug(assetManager);
			//createLadderLines();
		}
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

	private void createBuildings() {
		for (Building building : game.getBuildings()) {
			BuildingNode buildingNode = new BuildingNode("buildingNode");
			
			for (String identifier : building.getIdentifiers()) {
				Material buildingMaterial = materialFactory.createBuildingMaterial(identifier);
	
				Spatial model = assetManager.loadModel("Building" + identifier + ".mesh.xml");
				model.setMaterial(buildingMaterial);

				buildingNode.attachChild(model);
				
				//Create ladders
				Material selectedMaterial = materialFactory.createMaterial(MaterialIdentifier.SELECTED);
				
				List<LadderNode> ladders = LadderNode.createLaddersFrom("/Building" + identifier + ".ladder", selectedMaterial);
				
				for (LadderNode ladder : ladders) {
					buildingNode.attachChild(ladder);
				}
			}
			
			buildingNodes.add(buildingNode);
		}
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
	public void update(Observable o, Object arg) {
		updateModels();
		statusMessage.setText(getStatusTextFrom(game));
	}

	private void updateModels() {
		Iterator<FighterNode> it = fighterNodes.iterator();
		selectedFighterNode = null;

		while (it.hasNext()) {
			FighterNode fighterNode = it.next();
			Fighter fighter = fighterNode.getFighter();

			if (fighter.isOutOfAction()) {
				it.remove();
				getObjectsNode().detachChild(fighterNode);
				getPhysicsSpace().remove(fighterNode.getGhostControl());
			}

			if (fighter == game.getSelectedFighter()) {
				selectedFighterNode = fighterNode;
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

		if (currentTemplateNode != null) {
			colouriseBasesUnderTemplate(currentTemplateNode);
		}

		for (TemplateNode templateNode : templateNodes) {
			colouriseBasesUnderTemplate(templateNode);
		}
	}

	private Node getObjectsNode() {
		return (Node) rootNode.getChild("objectsNode");
	}

	private Node getBuildingsNode() {
		return (Node) rootNode.getChild("buildingsNode");
	}

	private Node getTableNode() {
		return (Node) rootNode.getChild("tableNode");
	}

	public void setInvertMouse(boolean invertMouse) {
		this.invertMouse = invertMouse;
	}

	private void colouriseBasesUnderTemplate(TemplateNode templateNode) {
		List<FighterNode> fighterNodesUnderTemplate = getFighterNodesUnderTemplate(templateNode, fighterNodes);

		for (FighterNode fighterNodeUnderTemplate : fighterNodesUnderTemplate) {
			setBaseTargeted(fighterNodeUnderTemplate);
		}
	}

	private void executeKeyboardAction(String name) {
		Necromunda.setStatusMessage("");
		Fighter selectedFighter = game.getSelectedFighter();

		if (name.equals("NextPhase")) {
			tearDownCurrentPath();
			game.setSelectionMode(SelectionMode.SELECT);
			game.nextPhase();
		}
		else if (name.equals("EndTurn")) {
			tearDownCurrentPath();
			game.setSelectionMode(SelectionMode.SELECT);
			game.endTurn();
			turnStarted();
		}
		else if (name.equals("SkipBuilding") && game.getSelectionMode().equals(SelectionMode.DEPLOY_BUILDING)) {
			skipBuilding();
		}
		else if (selectedFighter == null) {
			Necromunda.setStatusMessage("You must select a fighter first.");
		}
		else {
			if (game.getSelectionMode().equals(SelectionMode.SELECT) && isMemberOfCurrentGang(selectedFighterNode)) {
				if (name.equals("Break") && game.getPhase().equals(Phase.MOVEMENT)) {
					if (selectedFighter.isPinned()) {
						Necromunda.setStatusMessage("This ganger can not break the web.");
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

						game.setSelectionMode(SelectionMode.MOVE);
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

						game.setSelectionMode(SelectionMode.MOVE);
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

								game.setSelectionMode(SelectionMode.TARGET);

								updateValidTargetFighterNodes();

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
			else if (name.equals("Climb") && isMemberOfCurrentGang(selectedFighterNode)) {
				if (game.getSelectionMode().equals(SelectionMode.MOVE)) {
					currentLadders = getLaddersInReach(currentPath.getOrigin(), selectedFighter.getBaseRadius());
	
					if (currentLadders.isEmpty()) {
						Necromunda.setStatusMessage("There is no ladder in reach.");
					}
					else {
						currentLadder = currentLadders.get(0);
						game.setSelectionMode(SelectionMode.CLIMB);
						climbLadder(currentLadder);
					}
				}
				else if (game.getSelectionMode().equals(SelectionMode.CLIMB)) {
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
		}
	}
	
	private boolean isMemberOfCurrentGang(FighterNode fighterNode) {
		return game.getCurrentGang().getGangMembers().contains(selectedFighterNode.getFighter());
	}
	
	private void climbLadder(LadderNode ladder) {
		Vector3f currentPathOrigin;
		
		if (currentPath != null) {
			currentPathOrigin = currentPath.getOrigin();
			tearDownCurrentPath();
		}
		else {
			currentPathOrigin = currentClimbPath.getStart().clone();
		}
		
		currentClimbPath = new ClimbPath(currentPathOrigin.clone());
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
			
			if (game.getSelectionMode().equals(SelectionMode.SELECT)) {
				select();
			}
			else if (game.getSelectionMode().equals(SelectionMode.MOVE)) {
				move();
			}
			else if (game.getSelectionMode().equals(SelectionMode.CLIMB)) {
				climb();
			}
			else if (game.getSelectionMode().equals(SelectionMode.TARGET)) {
				target();
			}
			else if (game.getSelectionMode().equals(SelectionMode.DEPLOY_BUILDING)) {
				deployBuilding();
			}
			else if (game.getSelectionMode().equals(SelectionMode.DEPLOY_MODEL)) {
				deployModel();
			}
		}
	}
	
	private void deployBuilding() {
		Vector3f nearestIntersection = getTableCollisionPoint();

		if (nearestIntersection == null) {
			return;
		}

		selectedBuildingNode = selectedBuildingNode.clone(false);
		selectedBuildingNode.setLocalTranslation(nearestIntersection);
		buildingsNode.attachChild(selectedBuildingNode);
	}
	
	private void skipBuilding() {
		buildingsNode.detachChild(selectedBuildingNode);

		selectedBuildingNode = buildingNodes.poll();
		
		Vector3f nearestIntersection = getTableCollisionPoint();

		if (nearestIntersection == null) {
			return;
		}

		if (selectedBuildingNode == null) {
			CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(buildingsNode);
			buildingsControl = new RigidBodyControl(sceneShape, 0);
			buildingsControl.setKinematic(false);
			buildingsNode.addControl(buildingsControl);
			getPhysicsSpace().add(buildingsNode);
			
			game.setSelectionMode(SelectionMode.DEPLOY_MODEL);
			updateModelPosition();
		}
		else {
			selectedBuildingNode.setLocalTranslation(nearestIntersection);
			buildingsNode.attachChild(selectedBuildingNode);
		}
	}
	
	private void updateModelPosition() {
		Vector3f nearestIntersection = getSceneryCollisionPoint();

		if (nearestIntersection == null) {
			return;
		}

		if (selectedFighterNode == null) {
			selectedFighterNode = new FighterNode("fighterNode", game.getSelectedFighter(), materialFactory);

			getPhysicsSpace().add(selectedFighterNode.getGhostControl());
			getObjectsNode().attachChild(selectedFighterNode);
			fighterNodes.add(selectedFighterNode);

			game.updateStatus();
		}

		selectedFighterNode.setLocalTranslation(nearestIntersection);
		lockPhysics();
	}

	private void deployModel() {
		Vector3f contactPoint = getSceneryCollisionPoint();

		List<FighterNode> fighterNodesWithinDistance = getFighterNodesWithinDistance(selectedFighterNode, fighterNodes, NOT_TOUCH_DISTANCE);

		if ((contactPoint != null) && (selectedFighterNode != null) && hasValidPosition(selectedFighterNode) && fighterNodesWithinDistance.isEmpty()) {
			game.fighterDeployed();
		}
		
		/*List<Vector3f> pointCloud = selectedFighterNode.getCollisionShapePointCloud();
		
		Material material = materialFactory.createMaterial(MaterialIdentifier.SELECTED);
		
		for (Vector3f vector : pointCloud) {
			Quad quad = new Quad(0.01f, 0.01f);
			Geometry geometry = new Geometry("cloudpoint", quad);
			geometry.setMaterial(material);
			geometry.setLocalTranslation(vector);
			rootNode.attachChild(geometry);
		}*/
	}

	private void select() {
		if (selectedFighterNode != null) {
			deselectFighter();
		}

		FighterNode fighterNodeUnderCursor = getFighterNodeUnderCursor();

		if (fighterNodeUnderCursor != null) {
			selectFighter(fighterNodeUnderCursor);
		}
	}

	private void move() {
		if (hasValidPosition(selectedFighterNode) && currentPath.isValid()
				&& (getFighterNodesWithinDistance(selectedFighterNode, fighterNodes, NOT_TOUCH_DISTANCE).isEmpty())) {
			List<FighterNode> fighterNodesWithinDistance = getFighterNodesWithinDistance(selectedFighterNode, fighterNodes, Necromunda.RUN_SPOT_DISTANCE);
			List<FighterNode> hostileFighterNodesWithinDistance = getHostileFighterNodesFrom(fighterNodesWithinDistance);
			List<FighterNode> hostileFighterNodesWithinDistanceAndWithLineOfSight = getFighterNodesWithLineOfSightFrom(selectedFighterNode,
					hostileFighterNodesWithinDistance);

			if (selectedFighterNode.getFighter().isGoingToRun() && (!hostileFighterNodesWithinDistanceAndWithLineOfSight.isEmpty())) {
				Necromunda.setStatusMessage("You cannot run so close to an enemy fighter.");
			}
			else {
				commitMovement();
			}
		}
	}

	private void climb() {
		if (hasValidPosition(selectedFighterNode) && (getFighterNodesWithinDistance(selectedFighterNode, fighterNodes, NOT_TOUCH_DISTANCE).isEmpty())) {
			if (currentClimbPath.getLength() <= game.getSelectedFighter().getRemainingMovementDistance()) {
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
		
		List<Collidable> collidables = getBoundingVolumes();
		collidables.add(getBuildingsNode());
		
		boolean hideable = true;
		
		List<FighterNode> hostileFighterNodes = getHostileFighterNodesFrom(fighterNodes);
		
		for (FighterNode fighterNode : hostileFighterNodes) {
			VisibilityInfo visibilityInfo = getVisibilityInfo(fighterNode, selectedFighterNode.getCollisionShapePointCloud(), collidables);
			
			if (visibilityInfo.getNumberOfPoints() == visibilityInfo.getNumberOfVisiblePoints()) {
				hideable = false;
				Necromunda.setStatusMessage("This ganger cannot hide as he is seen by " + fighterNode.getFighter().getName() + ".");
				break;
			}
			
			if (isTargetWithinDistance(fighterNode, selectedFighterNode, fighterNode.getFighter().getInitiative())) {
				hideable = false;
				Necromunda.setStatusMessage("This ganger cannot hide as he is too close to " + fighterNode.getFighter().getName() + ".");
				break;
			}
		}
		
		if (hideable) {
			selectedFighterNode.getFighter().setHidden(true);
			selectedFighterNode.getFighter().setRemainingMovementDistance(0);
			game.setSelectionMode(SelectionMode.SELECT);
		}
	}
	
	private boolean isHideable(FighterNode fighterNode) {
		List<Collidable> collidables = getBoundingVolumes();
		collidables.add(getBuildingsNode());
		
		boolean hideable = true;
		
		List<FighterNode> hostileFighterNodes = getHostileFighterNodesFrom(fighterNodes);
		
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

	private void target() {
		Fighter selectedFighter = game.getSelectedFighter();
		RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();

		if (!weapon.isTargeted()) {
			weapon.trigger();

			fireTemplate(currentTemplateNode);

			removeTargetingFacilities();

			game.setSelectionMode(SelectionMode.SELECT);
		}
		else {
			FighterNode fighterNodeUnderCursor = getFighterNodeUnderCursor();

			if (fighterNodeUnderCursor == null) {
				return;
			}

			if (!getHostileFighterNodesFrom(fighterNodes).contains(fighterNodeUnderCursor)) {
				Necromunda.setStatusMessage("This fighter is not hostile.");
				return;
			}

			List<Collidable> collidables = getBoundingVolumes();

			if (currentTemplateNode != null) {
				collidables.removeAll(currentTemplateNode.getBoundingSpheres());
			}

			collidables.add(getBuildingsNode());
			
			/*List<FighterNode> otherFighterNodes = new ArrayList<FighterNode>(fighterNodes);
			otherFighterNodes.remove(selectedFighterNode);
			otherFighterNodes.remove(fighterNodeUnderCursor);
			
			collidables.addAll(otherFighterNodes);*/

			if (validSustainedFireTargetFighterNodes.isEmpty() && (!validTargetFighterNodes.contains(fighterNodeUnderCursor))) {
				Necromunda.setStatusMessage("This fighter is not a valid target.");
				return;
			}

			boolean targetAdded = addTarget(fighterNodeUnderCursor);

			if (targetAdded) {
				weapon.targetAdded();
			}

			if (weapon.getNumberOfShots() > 0) {
				return;
			}
			
			VisibilityInfo visibilityInfo = getVisibilityInfo(selectedFighterNode, fighterNodeUnderCursor.getCollisionShapePointCloud(), collidables);
			
			float visiblePercentage = visibilityInfo.getVisiblePercentage();

			Necromunda.appendToStatusMessage("Visible percentage: " + (visiblePercentage * 100));
			
			int hitModifier = 0;
			
			if ((visiblePercentage < 1.0) && (visiblePercentage >= 0.5)) {
				hitModifier = -1;
			}
			else if (visiblePercentage < 0.5) {
				hitModifier = -2;
			}
			
			fireTargetedWeapon(weapon, hitModifier);

			removeTargetingFacilities();

			targetedFighterNodes.clear();
			validSustainedFireTargetFighterNodes.clear();
		}

		game.setSelectionMode(SelectionMode.SELECT);
	}

	private void onRightClick(boolean isPressed) {
		if (isPressed) {
			rightButtonDown = true;
			
			if (game.getSelectionMode().equals(SelectionMode.MOVE)) {
				tearDownCurrentPath();
				game.setSelectionMode(SelectionMode.SELECT);
				deselectFighter();
			}
			else if (game.getSelectionMode().equals(SelectionMode.CLIMB)) {
				abortClimbing();
				game.setSelectionMode(SelectionMode.SELECT);
				deselectFighter();
			}
			else if (game.getSelectionMode().equals(SelectionMode.TARGET)) {
				removeTargetingFacilities();
				game.setSelectionMode(SelectionMode.SELECT);
				deselectFighter();
			}
		}
		else {
			rightButtonDown = false;
		}
	}

	private FighterNode getFighterNodeUnderCursor() {
		List<Collidable> collidables = new ArrayList<Collidable>();
		collidables.add(getObjectsNode());
		CollisionResult closestCollision = Utils.getNearestCollisionFrom(cam.getLocation(), cam.getDirection(), collidables);

		FighterNode fighterNodeUnderCursor = null;

		if (closestCollision != null) {
			Geometry geometry = closestCollision.getGeometry();
			fighterNodeUnderCursor = (FighterNode) getParent(geometry, "fighterNode");
		}

		return fighterNodeUnderCursor;
	}

	private void updateValidTargetFighterNodes() {
		validTargetFighterNodes.clear();
		List<FighterNode> visibleHostileFighterNodes = getFighterNodesWithLineOfSightFrom(selectedFighterNode, getHostileFighterNodesFrom(fighterNodes));

		boolean normalFighterNodeFound = false;

		while ((!normalFighterNodeFound) && (!visibleHostileFighterNodes.isEmpty())) {
			FighterNode fighterNode = getNearestFighterNodeFrom(selectedFighterNode, visibleHostileFighterNodes);
			Fighter fighter = fighterNode.getFighter();

			if (fighter.isNormal() || fighter.isPinned()) {
				normalFighterNodeFound = true;
			}

			validTargetFighterNodes.add(fighterNode);
			visibleHostileFighterNodes.remove(fighterNode);
		}
	}

	private boolean addTarget(FighterNode fighterNode) {
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

		List<FighterNode> sustainedFireNeighbours = getFighterNodesWithinDistance(getFighterNodeUnderCursor(), fighterNodes, Necromunda.SUSTAINED_FIRE_RADIUS);
		sustainedFireNeighbours = getFighterNodesWithLineOfSightFrom(selectedFighterNode, sustainedFireNeighbours);

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

	private void tearDownCurrentPath() {
		if (currentPath != null) {
			selectedFighterNode.setLocalTranslation(currentPath.getOrigin());
		}
		
		if (currentClimbPath != null) {
			selectedFighterNode.setLocalTranslation(currentClimbPath.getStart());
		}

		rootNode.detachChildNamed("currentPathBoxNode");

		if (currentPathBoxNode != null) {
			GhostControl physicsGhostObject = currentPathBoxNode.getControl(GhostControl.class);
			getPhysicsSpace().remove(physicsGhostObject);
		}

		currentPath = null;
		currentPathBoxNode = null;
	}

	private void abortClimbing() {
		if (currentClimbPath != null) {
			selectedFighterNode.setLocalTranslation(currentClimbPath.getStart());
		}

		currentClimbPath = null;
	}

	private void removeTargetingFacilities() {
		rootNode.detachChildNamed("currentLineOfSightBoxNode");
		rootNode.detachChildNamed("currentLineOfSightLine");

		if (currentLineOfSightBoxNode != null) {
			GhostControl physicsGhostObject = currentLineOfSightBoxNode.getControl(GhostControl.class);
			getPhysicsSpace().remove(physicsGhostObject);
		}

		currentLineOfSight = null;
		currentLineOfSightBoxNode = null;

		removeCurrentWeaponTemplate();
	}

	private void removeCurrentWeaponTemplate() {
		rootNode.detachChildNamed("currentTemplateNode");
		currentTemplateNode = null;
	}

	private void commitMovement() {
		Fighter selectedObject = game.getSelectedFighter();

		Vector3f movementVector = currentPath.getVector();
		float distance = movementVector.length();
		float remainingMovementDistance = selectedObject.getRemainingMovementDistance() - distance;
		selectedObject.setRemainingMovementDistance(remainingMovementDistance);

		currentPath.getOrigin().set(selectedFighterNode.getLocalTranslation());

		for (Fighter object : selectedObject.getGang().getGangMembers()) {
			if (object != selectedObject) {
				if (object.hasMoved() || object.hasRun()) {
					object.setRemainingMovementDistance(0);
				}
			}
		}

		if (selectedObject.isGoingToRun()) {
			selectedObject.setHasRun(true);
		}
		else {
			selectedObject.setHasMoved(true);
		}

		if (selectedObject.isSpotted() && selectedObject.hasRun()) {
			selectedObject.setRemainingMovementDistance(0);
			game.setSelectionMode(SelectionMode.SELECT);
		}

		if (selectedObject.getRemainingMovementDistance() < 0.01f) {
			selectedObject.setRemainingMovementDistance(0);
			tearDownCurrentPath();
			game.setSelectionMode(SelectionMode.SELECT);
		}
		
		revealHiddenFighters();
		
		if (!isHideable(selectedFighterNode)) {
			selectedFighterNode.getFighter().setHidden(false);
		}
	}
	
	private void revealHiddenFighters() {
		List<Collidable> collidables = getBoundingVolumes();
		collidables.add(getBuildingsNode());
		
		List<FighterNode> hostileFighterNodes = getHostileFighterNodesFrom(fighterNodes);
		
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
		Fighter selectedObject = game.getSelectedFighter();

		float distance = currentClimbPath.getLength();
		float remainingMovementDistance = selectedObject.getRemainingMovementDistance() - distance;
		selectedObject.setRemainingMovementDistance(remainingMovementDistance);

		currentClimbPath.getStart().set(selectedFighterNode.getLocalTranslation());

		for (Fighter object : selectedObject.getGang().getGangMembers()) {
			if (object != selectedObject) {
				if (object.hasMoved() || object.hasRun()) {
					object.setRemainingMovementDistance(0);
				}
			}
		}

		if (selectedObject.isGoingToRun()) {
			selectedObject.setHasRun(true);
		}
		else {
			selectedObject.setHasMoved(true);
		}

		if (selectedObject.getRemainingMovementDistance() < 0.01f) {
			selectedObject.setRemainingMovementDistance(0);
		}

		abortClimbing();

		deselectFighter();

		game.setSelectionMode(SelectionMode.SELECT);
		
		revealHiddenFighters();
		
		if (!isHideable(selectedFighterNode)) {
			selectedFighterNode.getFighter().setHidden(false);
		}
	}

	private Geometry getPathBoxGeometryFor(BoxCollisionShape collisionShape) {
		Vector3f halfExtents = collisionShape.getHalfExtents();

		Box box = new Box(halfExtents.getX(), halfExtents.getY(), halfExtents.getZ());
		Geometry boxGeometry = new Geometry("currentPathBoxGeometry", box);
		boxGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.PATH));
		boxGeometry.setQueueBucket(Bucket.Translucent);

		return boxGeometry;
	}

	private BoxCollisionShape getPathBoxCollisionShapeOf(FighterNode fighterNode, Line path) {
		Vector3f halfExtents = getHalfExtentsOf(fighterNode);
		float pathLength = path.length();

		Vector3f vector = new Vector3f(halfExtents.getX(), halfExtents.getY(), pathLength / 2);
		BoxCollisionShape collisionShape = new BoxCollisionShape(vector);

		return collisionShape;
	}

	private Vector3f getHalfExtentsOf(FighterNode fighterNode) {
		GhostControl control = fighterNode.getGhostControl();
		CylinderCollisionShape shape = (CylinderCollisionShape)control.getCollisionShape();
		Vector3f halfExtents = shape.getHalfExtents();

		return halfExtents;
	}

	private void selectFighter(FighterNode fighterNode) {
		Fighter fighter = fighterNode.getFighter();

		/*
		 * if (fighter.getGang() == game.getCurrentGang()) {
		 * game.setSelectedFighter(fighter); }
		 */

		game.setSelectedFighter(fighter);
	}

	private void deselectFighter() {
		game.setSelectedFighter(null);
	}

	private void setBaseSelected(Node model) {
		Spatial base = model.getChild("base");
		base.setMaterial(materialFactory.createMaterial(MaterialIdentifier.SELECTED));
	}

	private void setBaseTargeted(Node model) {
		Spatial base = model.getChild("base");
		base.setMaterial(materialFactory.createMaterial(MaterialIdentifier.TARGETED));
	}

	private void setBaseNormal(Node model) {
		Spatial base = model.getChild("base");
		base.setMaterial(materialFactory.createMaterial(MaterialIdentifier.NORMAL));
	}

	private boolean hasValidPosition(FighterNode fighterNode) {
		if (!fighterNode.isPositionValid() || isPhysicsLocked()) {
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
	
	private VisibilityInfo getVisibilityInfo(FighterNode source, List<Vector3f> pointCloud, List<Collidable> collidables) {
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

	private List<Collidable> getBoundingVolumes() {
		List<Collidable> boundingVolumes = new ArrayList<Collidable>();

		for (TemplateNode templateNode : templateNodes) {
			boundingVolumes.addAll(templateNode.getBoundingSpheres());
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
		collidables.add(getBuildingsNode());
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

	private class PhysicsCollisionListenerImpl implements PhysicsCollisionListener {

		@Override
		public void collision(PhysicsCollisionEvent event) {
			Spatial a = event.getNodeA();
			Spatial b = event.getNodeB();

			Spatial selectedCollisionShapeNode = null;

			if (selectedFighterNode != null) {
				selectedCollisionShapeNode = selectedFighterNode.getChild("collisionShapeNode");
			}

			Spatial targetedCollisionShapeNode = null;

			FighterNode fighterNodeUnderCursor = getFighterNodeUnderCursor();

			if (fighterNodeUnderCursor != null) {
				targetedCollisionShapeNode = fighterNodeUnderCursor.getChild("collisionShapeNode");
			}

			if ((a == selectedCollisionShapeNode) && (b.getName().equals("buildingsNode")) || (b == selectedCollisionShapeNode)
					&& (a.getName().equals("buildingsNode"))) {
				selectedFighterNode.setPositionValid(false);
			}
			else if ((a == selectedCollisionShapeNode) && (b.getName().equals("collisionShapeNode")) || (b == selectedCollisionShapeNode)
					&& (a.getName().equals("collisionShapeNode"))) {
				selectedFighterNode.setPositionValid(false);
			}
			else if ((b.getName().equals("currentLineOfSightBoxNode")
					&& ((a.getName().equals("collisionShapeNode") && (a != selectedCollisionShapeNode) && (a != targetedCollisionShapeNode))) || (a.getName()
					.equals("currentLineOfSightBoxNode"))
					&& ((b.getName().equals("collisionShapeNode") && (b != selectedCollisionShapeNode) && (b != targetedCollisionShapeNode))))) {
				if (currentLineOfSight != null) {
					currentLineOfSight.setValid(false);
				}
			}
			else if ((a.getName().equals("currentLineOfSightBoxNode")) && (b.getName().equals("buildingsNode"))
					|| (b.getName().equals("currentLineOfSightBoxNode")) && (a.getName().equals("buildingsNode"))) {
				if (currentLineOfSight != null) {
					currentLineOfSight.setValid(false);
				}
			}
			else if ((a.getName().equals("currentPathBoxNode")) && ((b.getName().equals("collisionShapeNode")) && (b != selectedCollisionShapeNode))
					|| (b.getName().equals("currentPathBoxNode")) && ((a.getName().equals("collisionShapeNode")) && (a != selectedCollisionShapeNode))) {
				if (currentPath != null) {
					currentPath.setValid(false);
				}
			}
			else if ((a.getName().equals("currentPathBoxNode")) && (b.getName().equals("buildingsNode")) || (b.getName().equals("currentPathBoxNode"))
					&& (a.getName().equals("buildingsNode"))) {
				if (currentPath != null) {
					currentPath.setValid(false);
				}
			}
		}
	}

	private class PhysicsTickListenerImpl implements PhysicsTickListener {
		@Override
		public void prePhysicsTick(PhysicsSpace space, float f) {
		}

		@Override
		public void physicsTick(PhysicsSpace space, float f) {
			if (!physicsTickLock1) {
				physicsTickLock1 = true;
			}
			else {
				physicsTickLock2 = true;
			}
		}
	}

	private class MouseListener implements ActionListener, AnalogListener {
		public void onAction(String name, boolean isPressed, float tpf) {
			executeMouseAction(name, isPressed);

			game.updateStatus();
		}

		public void onAnalog(String name, float value, float tpf) {
			if (game.getSelectionMode().equals(SelectionMode.MOVE)) {
				setUpMovement();
			}
			else if (game.getSelectionMode().equals(SelectionMode.TARGET)) {
				setUpTargeting();
			}
			else if (game.getSelectionMode().equals(SelectionMode.DEPLOY_MODEL)) {
				updateModelPosition();
			}
			else if (game.getSelectionMode().equals(SelectionMode.DEPLOY_BUILDING)) {
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

			updateModels();
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

	private class KeyboardListener implements ActionListener {

		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (isPressed) {
				executeKeyboardAction(name);
				game.updateStatus();
			}
		}
	}

	private class TemplateRemover {
		private TemplateNode temporaryWeaponTemplate;
		private int timer;

		public TemplateRemover(TemplateNode temporaryWeaponTemplate) {
			this.temporaryWeaponTemplate = temporaryWeaponTemplate;
			this.timer = 2000;
		}

		public void remove() {
			rootNode.detachChild(temporaryWeaponTemplate);
			templateNodes.remove(temporaryWeaponTemplate);
			updateModels();
		}

		public int getTimer() {
			return timer;
		}

		public void setTimer(int timer) {
			this.timer = timer;
		}
	}
	
	private List<LadderNode> getLaddersInReach(Vector3f origin, float baseRadius) {
		List<LadderNode> laddersInReach = new ArrayList<LadderNode>();
		
		for (LadderNode ladder : (List<LadderNode>)getLaddersFrom(buildingsNode)) {
			float distance = ladder.getWorldStart().distance(origin);
			
			if ((distance - baseRadius) <= MAX_LADDER_DISTANCE) {
				laddersInReach.add(ladder);
			}
		}
		
		return laddersInReach;
	}

	private void unpinFighters() {
		for (FighterNode fighterNode : fighterNodes) {
			Fighter fighter = fighterNode.getFighter();

			if (game.getCurrentGang().getGangMembers().contains(fighter) && fighter.isPinned()) {
				List<FighterNode> surroundingFighterNodes = getFighterNodesWithinDistance(fighterNode, fighterNodes, Necromunda.UNPIN_BY_INITIATIVE_DISTANCE);
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
		Iterator<TemplateNode> it = templateNodes.iterator();

		while (it.hasNext()) {
			TemplateNode templateNode = it.next();

			if (templateNode.isTemplateToBeRemoved()) {
				it.remove();
				rootNode.detachChild(templateNode);
			}
		}
	}

	private void moveTemplates() {
		for (TemplateNode templateNode : templateNodes) {
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
		for (TemplateNode templateNode : templateNodes) {
			List<FighterNode> affectedFighterNodes = getFighterNodesUnderTemplate(templateNode, fighterNodes);
			templateNode.dealDamageTo(affectedFighterNodes);
		}
	}

	private void removeTemplateTrails() {
		for (TemplateNode templateNode : templateNodes) {
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
		float remainingMovementDistance = game.getSelectedFighter().getRemainingMovementDistance();

		if (distance > remainingMovementDistance) {
			movementVector.normalizeLocal().multLocal(remainingMovementDistance);
		}

		currentPath.getDirection().set(currentPath.getOrigin().add(movementVector));
	}

	private void updateCurrentPathBox() {
		BoxCollisionShape boxCollisionShape = getPathBoxCollisionShapeOf(selectedFighterNode, currentPath);
		GhostControl physicsGhostObject;

		if (currentPathBoxNode == null) {
			physicsGhostObject = new GhostControl(boxCollisionShape);

			currentPathBoxNode = new Node("currentPathBoxNode");
			currentPathBoxNode.addControl(physicsGhostObject);

			getPhysicsSpace().add(physicsGhostObject);
		}
		else {
			currentPathBoxNode.detachChildNamed("currentPathBoxGeometry");

			physicsGhostObject = currentPathBoxNode.getControl(GhostControl.class);
			physicsGhostObject.setCollisionShape(boxCollisionShape);
		}

		currentPath.setValid(true);

		currentPathBoxNode.attachChild(getPathBoxGeometryFor(boxCollisionShape));

		rootNode.attachChild(currentPathBoxNode);

		Vector3f halfExtents = getHalfExtentsOf(selectedFighterNode);
		Vector3f upTranslation = new Vector3f(0, halfExtents.getY(), 0);
		Vector3f vector = currentPath.getOrigin().add(currentPath.getVector().mult(0.5f)).addLocal(upTranslation);
		currentPathBoxNode.setLocalTranslation(vector);
		currentPathBoxNode.lookAt(currentPath.getDirection().add(upTranslation), Vector3f.UNIT_Y);

		selectedFighterNode.setLocalTranslation(currentPath.getDirection());
		lockPhysics();
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
		Fighter selectedFighter = game.getSelectedFighter();
		RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();
		Ammunition currentAmmunition = weapon.getCurrentAmmunition();

		if (currentAmmunition.isTemplated()) {
			Line line = null;

			if (weapon.isTargeted()) {
				if (getFighterNodeUnderCursor() == null) {
					removeTargetingFacilities();
					return;
				}

				updateCurrentLineOfSight();
				updateCurrentLineOfSightLine();
				updateCurrentLineOfSightBox();
				line = currentLineOfSight;
			}
			else {
				Vector3f upTranslation = new Vector3f(0, selectedFighter.getBaseRadius() * 1.5f, 0);
				line = new Line(selectedFighterNode.getLocalTranslation().add(upTranslation), getSceneryCollisionPoint().add(upTranslation));
			}

			if (currentTemplateNode == null) {
				currentTemplateNode = TemplateNode.createTemplateNode(assetManager, currentAmmunition);
			}

			Vector3f lineOfSightVector = line.getDirection().subtract(line.getOrigin());
			lineOfSightVector = lineOfSightVector.normalize();

			if (weapon.isTemplateAttached()) {
				currentTemplateNode.rotateUpTo(lineOfSightVector);
				currentTemplateNode.setLocalTranslation(line.getOrigin());
			}
			else {
				currentTemplateNode.setLocalTranslation(line.getDirection());
			}

			rootNode.detachChildNamed("currentTemplateNode");
			rootNode.attachChild(currentTemplateNode);
		}
		else {
			if (getFighterNodeUnderCursor() != null) {
				updateCurrentLineOfSight();
				updateCurrentLineOfSightLine();
				updateCurrentLineOfSightBox();
			}
			else {
				removeTargetingFacilities();
			}
		}
	}

	private void updateCurrentLineOfSight() {
		Vector3f sourceCenter = selectedFighterNode.getChild("collisionShapeNode").getWorldTranslation().clone().add(0, selectedFighterNode.getCenterToHeadOffset(), 0);
		Vector3f targetCenter = getFighterNodeUnderCursor().getChild("collisionShapeNode").getWorldTranslation().clone();

		if (currentLineOfSight == null) {
			currentLineOfSight = new Line(sourceCenter, targetCenter);
		}
		else {
			currentLineOfSight.getOrigin().set(sourceCenter);
			currentLineOfSight.getDirection().set(targetCenter);
		}
	}

	private void updateCurrentLineOfSightLine() {
		Geometry lineGeometry = (Geometry) rootNode.getChild("currentLineOfSightLine");

		Vector3f start = currentLineOfSight.getOrigin();
		Vector3f end = currentLineOfSight.getDirection();

		if (lineGeometry == null) {
			com.jme3.scene.shape.Line line = new com.jme3.scene.shape.Line(start, end);
			lineGeometry = new Geometry("currentLineOfSightLine", line);
			lineGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.SELECTED));

			rootNode.attachChild(lineGeometry);
		}
		else {
			com.jme3.scene.shape.Line line = (com.jme3.scene.shape.Line) lineGeometry.getMesh();
			line.updatePoints(start, end);
		}
	}

	private void updateCurrentLineOfSightBox() {
		float lineOfSightLength = getLineLength(currentLineOfSight);
		Vector3f halfExtents = new Vector3f(0.1f, 0.1f, lineOfSightLength * 0.5f);
		CollisionShape boxCollisionShape = new BoxCollisionShape(halfExtents);
		GhostControl physicsGhostObject;

		if (currentLineOfSightBoxNode == null) {
			physicsGhostObject = new GhostControl(boxCollisionShape);

			currentLineOfSightBoxNode = new Node("currentLineOfSightBoxNode");
			currentLineOfSightBoxNode.addControl(physicsGhostObject);

			getPhysicsSpace().add(physicsGhostObject);

			rootNode.attachChild(currentLineOfSightBoxNode);
		}
		else {
			physicsGhostObject = currentLineOfSightBoxNode.getControl(GhostControl.class);
			physicsGhostObject.setCollisionShape(boxCollisionShape);
		}

		currentLineOfSight.setValid(true);

		Vector3f vector = currentLineOfSight.getOrigin().add(currentLineOfSight.getVector().mult(0.5f));
		currentLineOfSightBoxNode.setLocalTranslation(vector);
		lockPhysics();
		currentLineOfSightBoxNode.lookAt(currentLineOfSight.getDirection(), Vector3f.UNIT_Y);
	}

	private float getLineLength(Line line) {
		return line.getOrigin().distance(line.getDirection());
	}

	private PhysicsSpace getPhysicsSpace() {
		return stateManager.getState(BulletAppState.class).getPhysicsSpace();
	}

	private void lockPhysics() {
		physicsTickLock1 = false;
		physicsTickLock2 = false;
	}

	private boolean isPhysicsLocked() {
		return !physicsTickLock1 || !physicsTickLock2;
	}

	private List<FighterNode> getHostileFighterNodesFrom(List<FighterNode> fighterNodes) {
		List<FighterNode> hostileFighterNodes = new ArrayList<FighterNode>();

		for (FighterNode fighterNode : fighterNodes) {
			Fighter fighter = fighterNode.getFighter();

			if (game.getHostileGangers().contains(fighter)) {
				hostileFighterNodes.add(fighterNode);
			}
		}

		return hostileFighterNodes;
	}

	private List<FighterNode> getFighterNodesWithLineOfSightFrom(FighterNode source, List<FighterNode> fighterNodes) {
		List<FighterNode> fighterNodesWithLineOfSight = new ArrayList<FighterNode>();

		List<Collidable> collidables = getBoundingVolumes();
		collidables.add(getBuildingsNode());

		for (FighterNode fighterNode : fighterNodes) {
			VisibilityInfo visibilityInfo = getVisibilityInfo(source, fighterNode.getCollisionShapePointCloud(), collidables);
			
			if ((visibilityInfo.getNumberOfVisiblePoints() > 0) && !fighterNode.getFighter().isHidden()) {
				fighterNodesWithLineOfSight.add(fighterNode);
			}
		}

		return fighterNodesWithLineOfSight;
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
			CylinderCollisionShape shape = (CylinderCollisionShape)fighterNode.getGhostControl().getCollisionShape();
			Vector3f halfExtents = shape.getHalfExtents();
			Vector3f localTranslation = fighterNode.getLocalTranslation().clone();
			Fighter fighter = fighterNode.getFighter();
			localTranslation.y += fighter.getBaseRadius() * 1.5f;
			BoundingBox boundingBox = new BoundingBox(localTranslation, halfExtents.x, halfExtents.y, halfExtents.z);

			for (BoundingSphere sphere : templateNode.getBoundingSpheres()) {
				if (boundingBox.intersectsSphere(sphere)) {
					fighterNodesUnderTemplate.add(fighterNode);
					break;
				}
			}
		}

		return fighterNodesUnderTemplate;
	}

	private void fireTargetedWeapon(RangeCombatWeapon weapon, int hitModifier) {
		weapon.trigger();

		Iterator<FighterNode> targetedFighterNodesIterator = targetedFighterNodes.iterator();

		while (targetedFighterNodesIterator.hasNext()) {
			FighterNode fighterNode = targetedFighterNodesIterator.next();

			float distance = fighterNode.getLocalTranslation().distance(selectedFighterNode.getLocalTranslation());

			if (distance > weapon.getMaximumRange()) {
				Necromunda.appendToStatusMessage("Object out of range.");
				targetedFighterNodesIterator.remove();
				continue;
			}

			Fighter selectedFighter = game.getSelectedFighter();

			int targetHitRoll = 7 - selectedFighter.getBallisticSkill() - weapon.getRangeModifier(distance) - hitModifier;

			if (targetHitRoll >= 10) {
				Necromunda.appendToStatusMessage(String.format("You need a %s to hit - impossible!", targetHitRoll));
				targetedFighterNodesIterator.remove();
				continue;
			}

			Necromunda.appendToStatusMessage(String.format("Target hit roll is %s.", targetHitRoll));

			int hitRoll = Utils.rollD6();

			if ((targetHitRoll > 6) && (hitRoll == 6)) {
				targetHitRoll -= 3;
				hitRoll = Utils.rollD6();
			}

			if ((hitRoll < targetHitRoll) || (hitRoll <= 1)) {
				Necromunda.appendToStatusMessage(String.format("Rolled a %s and missed...", hitRoll));
				shotHasMissed(weapon.isScattering());
				targetedFighterNodesIterator.remove();
				continue;
			}

			Necromunda.appendToStatusMessage(String.format("Rolled a %s and hit!", hitRoll));

			fireAtTarget(hitRoll);

			targetedFighterNodesIterator.remove();
		}
	}

	private void fireAtTarget(int hitRoll) {
		Fighter selectedFighter = game.getSelectedFighter();
		RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();

		weapon.hitRoll(hitRoll);

		if (currentTemplateNode != null) {
			fireTemplate(currentTemplateNode);
			queueTemplateNodeForRemoval(currentTemplateNode);
		}
		else {
			applyShotToTargets(weapon);
		}
	}

	private void shotHasMissed(boolean isScattering) {
		if (currentTemplateNode != null) {
			boolean hasEffect = true;

			if (isScattering) {
				List<Collidable> collidables = new ArrayList<Collidable>();
				collidables.add(getBuildingsNode());
				hasEffect = currentTemplateNode.scatter(getLineLength(currentLineOfSight), collidables);
			}

			if (hasEffect) {
				fireTemplate(currentTemplateNode);
			}

			queueTemplateNodeForRemoval(currentTemplateNode);
		}
	}

	private void queueTemplateNodeForRemoval(TemplateNode templateNode) {
		templateNodes.add(templateNode);

		if (!currentTemplateNode.isTemplatePersistent()) {
			templateNode.setName("temporaryTemplateNode");
			TemplateRemover templateRemover = new TemplateRemover(templateNode);
			templateRemovers.add(templateRemover);
		}
		else {
			templateNode.setName("persistentTemplateNode");
		}
	}

	private void applyShotToTargets(RangeCombatWeapon weapon) {
		List<FighterNode> affectedFighterNodes = new ArrayList<FighterNode>();

		FighterNode affectedFighterNode = targetedFighterNodes.get(0);
		affectedFighterNodes.add(affectedFighterNode);

		if (weapon.getAdditionalTargetRange() > 0) {
			List<FighterNode> fighterNodesWithinRange = getFighterNodesWithinDistance(affectedFighterNode, fighterNodes, weapon.getAdditionalTargetRange());
			List<FighterNode> visibleFighterNodes = getFighterNodesWithLineOfSightFrom(selectedFighterNode, fighterNodesWithinRange);

			affectedFighterNodes.addAll(visibleFighterNodes);
		}

		pinFighters(affectedFighterNodes);

		for (FighterNode fighterNode : affectedFighterNodes) {
			Fighter fighter = fighterNode.getFighter();
			weapon.dealDamageTo(fighter);
		}
	}

	private void fireTemplate(TemplateNode templateNode) {
		List<FighterNode> affectedFighterNodes = getFighterNodesUnderTemplate(templateNode, fighterNodes);
		pinFighters(affectedFighterNodes);
		templateNode.dealDamageTo(affectedFighterNodes);
	}

	private void pinFighters(List<FighterNode> fighterNodes) {
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

		if (game.getSelectedFighter() != null) {
			Fighter ganger = (Fighter) game.getSelectedFighter();
			statusText.append(String.format("%s, %s, %sFlesh Wounds: %s\n", ganger.getName(), ganger.getState(), (ganger.isWebbed() ? "Webbed, " : ""), ganger
					.getFleshWounds()));

			RangeCombatWeapon weapon = ganger.getSelectedRangeCombatWeapon();

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
	}

	public String getTerrainType() {
		return terrainType;
	}

	public void setTerrainType(String terrainType) {
		this.terrainType = terrainType;
	}
}
