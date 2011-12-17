package necromunda;


import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import necromunda.Fighter.State;
import necromunda.Gang.Phase;
import necromunda.MaterialFactory.MaterialIdentifier;
import necromunda.Necromunda.SelectionMode;
import weapons.Ammunition;
import weapons.RangeCombatWeapon;
import weapons.Weapon;
import weapons.WebPistol;
import weapons.RangeCombatWeapon.WeaponType;

import com.jme3.app.SimpleApplication;
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
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Necromunda3dProvider extends SimpleApplication implements Observer, ActionListener, AnalogListener {
	public static final float MAX_COLLISION_NORMAL_ANGLE = 0.05f;
	public static final float MAX_SLOPE = 0.05f;
	public static final float NOT_TOUCH_DISTANCE = 0.01f;
	public static final float MAX_LADDER_DISTANCE = 0.5f;
	public static final boolean ENABLE_PHYSICS_DEBUG = false;
	public static final Vector3f GROUND_BUFFER = new Vector3f(0, NOT_TOUCH_DISTANCE, 0);
	private Necromunda game;
	private FighterNode selectedFighterNode;
	private List<FighterNode> validTargetFighterNodes;
	private List<FighterNode> fighterNodes;
	
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

	private List<Node> buildingNodes;
	private List<Ladder> ladders;

	private BitmapText statusMessage;
	
	private MaterialFactory materialFactory;

	public Necromunda3dProvider(Necromunda game) {
		this.game = game;
		
		fighterNodes = new ArrayList<FighterNode>();

		buildingNodes = new ArrayList<Node>();

		templateNodes = new ArrayList<TemplateNode>();
		
		templateRemovers = new ArrayList<TemplateRemover>();
		
		targetedFighterNodes = new ArrayList<FighterNode>();
		
		validSustainedFireTargetFighterNodes = new ArrayList<FighterNode>();
		
		validTargetFighterNodes = new ArrayList<FighterNode>();
	}

	@Override
	public void simpleInitApp() {
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

		assetManager.registerLocator("", ClasspathLocator.class.getName());
		assetManager.registerLoader("com.jme3.material.plugins.NeoTextureMaterialLoader","tgr");
		
		materialFactory = new MaterialFactory(assetManager);
		
		Node tableNode = createTableNode();
		rootNode.attachChild(tableNode);

		Node buildingsNode = createBuildings();

		CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(buildingsNode);
		buildingsControl = new RigidBodyControl(sceneShape, 0);
		buildingsControl.setKinematic(false);
		buildingsNode.addControl(buildingsControl);

		PhysicsSpace physicsSpace = getPhysicsSpace();

		if (ENABLE_PHYSICS_DEBUG) {
			physicsSpace.enableDebug(assetManager);
		}

		physicsSpace.add(buildingsControl);
		physicsSpace.addCollisionListener(new PhysicsCollisionListenerImpl());
		physicsSpace.addTickListener(new PhysicsTickListenerImpl());

		Node objectsNode = new Node("objectsNode");

		rootNode.attachChild(objectsNode);
		rootNode.attachChild(buildingsNode);

		cam.setLocation(new Vector3f(0, 20, 50));
		getFlyByCamera().setMoveSpeed(20f);

		guiNode.detachAllChildren();

		invertMouse();

		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(-0.5f, -1.5f, -1).normalize());
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

		Geometry sky = (Geometry)SkyFactory.createSky(assetManager, tex1, tex2, tex3, tex4, tex0, tex0);
		//Fix bug which sometimes culls the skybox
		sky.setLocalScale(100);
		rootNode.attachChild(sky);

		Sphere rock = new Sphere(32, 32, 2f, false, true);
		rock.setTextureMode(Sphere.TextureMode.Projected);
		
		//Cylinder rock = new Cylinder(4, 32, 1, 1, false, true);

		Geometry shiny_rock = new Geometry("Shiny rock", rock);
		//shiny_rock.setLocalScale(100);
		shiny_rock.setQueueBucket(Bucket.Sky);
        shiny_rock.setCullHint(Spatial.CullHint.Never);
        
        /*Material mat_lit = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_lit.setTexture("ColorMap", assetManager.loadTexture("Textures/sky_sphere.PNG"));
		shiny_rock.setMaterial(mat_lit);*/
		
		/*Material mat_lit = new Material(assetManager, "Common/MatDefs/Misc/Sky.j3md");
        mat_lit.setTexture("Texture", assetManager.loadTexture("Textures/sky_sphere.png"));
        mat_lit.setVector3("NormalScale", Vector3f.UNIT_XYZ);
        mat_lit.setBoolean("SphereMap", true);
		shiny_rock.setMaterial(mat_lit);*/
		
		Node rockNode = new Node("Rock");
		rockNode.setQueueBucket(Bucket.Sky);
		rockNode.attachChild(shiny_rock);
		rockNode.move(0, 0, 0);
		rockNode.rotateUpTo(new Vector3f(0, 0, -1));
		//rootNode.attachChild(rockNode);

		inputManager.addMapping("leftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping("rightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addListener(this, "leftClick");
		inputManager.addListener(this, "rightClick");

		inputManager.addMapping("Move_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("Move_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("Move_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addMapping("Move_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addListener(this, "Move_Left", "Move_Right", "Move_Up", "Move_Down");

		KeyboardListener keyboardListener = new KeyboardListener();

		inputManager.addMapping("Break", new KeyTrigger(KeyInput.KEY_B));
		inputManager.addListener(keyboardListener, "Break");

		inputManager.addMapping("Move", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(keyboardListener, "Move");

		inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_R));
		inputManager.addListener(keyboardListener, "Run");

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

		ladders = new ArrayList<Ladder>();

		createLadders();
	}

	private Node createTableNode() {
		Box box = new Box(new Vector3f(24, -0.5f, 24), 24, 0.5f, 24);
		Geometry tableGeometry = new Geometry("tableGeometry", box);
		tableGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.TABLE_GRASS));

		Node tableNode = new Node("tableNode");
		tableNode.attachChild(tableGeometry);

		return tableNode;
	}

	private Node createBuildings() {
		Node buildingsNode = new Node("buildingsNode");

		for (Building building : game.getBuildings()) {
			Material buildingMaterial = materialFactory.createBuildingMaterial(building.getIdentifier());

			Spatial model = assetManager.loadModel("Building" + building.getIdentifier() + ".mesh.xml");
			model.setMaterial(buildingMaterial);

			Vector3f origin = building.getOrigin();
			float rotationAngle = building.getRotationAngle();
			
			Node buildingNode = new Node("buildingNode");
			buildingNodes.add(buildingNode);
			buildingNode.attachChild(model);
			buildingNode.setLocalTranslation(origin);
			buildingNode.rotate(0, rotationAngle, 0);

			buildingsNode.attachChild(buildingNode);
		}

		return buildingsNode;
	}

	private void createLadders() {
		Material selectedBaseMaterial = materialFactory.createMaterial(MaterialIdentifier.SELECTED);
		
		for (Node buildingNode : buildingNodes) {
			Vector3f vector1 = new Vector3f(-3.5f, 0, 4.5f);
			Ladder ladder1 = new Ladder(vector1, selectedBaseMaterial);

			Vector3f vector2 = new Vector3f(-3.5f, 3.5f, 4.5f);
			Ladder ladder2 = new Ladder(vector2, selectedBaseMaterial);

			ladder1.setPeer(ladder2);
			ladder2.setPeer(ladder1);

			ladders.add(ladder1);
			ladders.add(ladder2);

			buildingNode.attachChild(ladder1.getLineNode());
			buildingNode.attachChild(ladder2.getLineNode());
		}
	}

	private void invertMouse() {
		inputManager.deleteMapping("FLYCAM_Up");
		inputManager.deleteMapping("FLYCAM_Down");

		inputManager.addMapping("FLYCAM_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true), new KeyTrigger(KeyInput.KEY_UP));

		inputManager.addMapping("FLYCAM_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false), new KeyTrigger(KeyInput.KEY_DOWN));

		inputManager.addListener(getFlyByCamera(), "FLYCAM_Up", "FLYCAM_Down");
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

				GhostControl control = fighterNode.getGhostControl();
				getPhysicsSpace().remove(control);
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
			
			Node figureNode = (Node)fighterNode.getChild("figureNode");
			
			List<Spatial> children = new ArrayList<Spatial>(figureNode.getChildren());
			
			if (children != null) {
				Iterator<Spatial> iterator = children.iterator();
				
				while (iterator.hasNext()) {
					Spatial child = iterator.next();
					
					if (child.getName().equals("symbol")) {
						figureNode.detachChild(child);
					}
				}
			}
			
			if (fighter.isPinned()) {
				fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_PINNED));
			}
			else if (fighter.isDown()) {
				fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_DOWN));
			}
			else if (fighter.isSedated()) {
				fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_SEDATED));
			}
			else if (fighter.isComatose()) {
				fighterNode.attachSymbol(materialFactory.createMaterial(MaterialIdentifier.SYMBOL_COMATOSE));
			}
			
			Ladder nearestLadder = getNearestLadder(fighterNode.getLocalTranslation(), fighter.getBaseRadius());
			
			if (nearestLadder != null) {
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
		return (Node)rootNode.getChild("objectsNode");
	}
	
	private Node getBuildingsNode() {
		return (Node)rootNode.getChild("buildingsNode");
	}
	
	private Node getTableNode() {
		return (Node)rootNode.getChild("tableNode");
	}
	
	private void colouriseBasesUnderTemplate(TemplateNode templateNode) {
		List<FighterNode> fighterNodesUnderTemplate = getFighterNodesUnderTemplate(templateNode, fighterNodes);

		for (FighterNode fighterNodeUnderTemplate : fighterNodesUnderTemplate) {
			setBaseTargeted(fighterNodeUnderTemplate);
		}
	}

	

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (isPressed) {
			Necromunda.setStatusMessage("");

			executeAction(name);

			game.updateStatus();
		}
	}
	
	private void executeAction(String name) {
		if (name.equals("leftClick")) {
			onLeftClick();
		}
		else if (name.equals("rightClick")) {
			onRightClick();
		}
	}

	private void onLeftClick() {
		if (game.getSelectionMode().equals(SelectionMode.DEPLOY)) {
			deploy();
		}
		else if (game.getSelectionMode().equals(SelectionMode.SELECT)) {
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
	}
	
	private void deploy() {
		Vector3f contactPoint = getSceneryCollisionPoint();

		List<FighterNode> fighterNodesWithinDistance = getFighterNodesWithinDistance(selectedFighterNode, NOT_TOUCH_DISTANCE);

		if ((contactPoint != null) && (selectedFighterNode != null) && hasValidPosition(selectedFighterNode) && fighterNodesWithinDistance.isEmpty()) {
			game.objectDeployed();
		}
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
		if (hasValidPosition(selectedFighterNode) && currentPath.isValid() && (getFighterNodesWithinDistance(selectedFighterNode, NOT_TOUCH_DISTANCE).isEmpty())) {
			List<FighterNode> fighterNodesWithinDistance = getFighterNodesWithinDistance(selectedFighterNode, Necromunda.RUN_SPOT_DISTANCE);
			List<FighterNode> hostileFighterNodesWithinDistance = getHostileFighterNodesFrom(fighterNodesWithinDistance);
			List<FighterNode> hostileFighterNodesWithinDistanceAndWithLineOfSight = getFighterNodesWithLineOfSightFrom(selectedFighterNode, hostileFighterNodesWithinDistance);
			
			if (selectedFighterNode.getFighter().isGoingToRun() && (! hostileFighterNodesWithinDistanceAndWithLineOfSight.isEmpty())) {
				Necromunda.setStatusMessage("You cannot run so close to an enemy fighter.");
			}
			else {
				commitMovement();
			}
		}
	}
	
	private void climb() {
		if (hasValidPosition(selectedFighterNode) && (getFighterNodesWithinDistance(selectedFighterNode, NOT_TOUCH_DISTANCE).isEmpty())) {
			if (currentClimbPath.getLength() <= game.getSelectedFighter().getRemainingMovementDistance()) {
				commitClimb();
			}
			else {
				Necromunda.setStatusMessage("This ganger cannot climb that far.");
			}
		}
	}
	
	private void target() {
		Fighter selectedFighter = game.getSelectedFighter();
		RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();

		if (! weapon.isTargeted()) {
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

			if (! getHostileFighterNodesFrom(fighterNodes).contains(fighterNodeUnderCursor)) {
				Necromunda.setStatusMessage("This fighter is not hostile.");
				return;
			}
			
			List<Collidable> collidables = getBoundingVolumes();
			
			if (currentTemplateNode != null) {
				collidables.removeAll(currentTemplateNode.getBoundingSpheres());
			}
			
			collidables.add(getBuildingsNode());
			boolean hasLineOfSight = hasLineOfSight(selectedFighterNode, fighterNodeUnderCursor, collidables);

			if (! currentLineOfSight.isValid() || ! hasLineOfSight || isPhysicsLocked()) {
				Necromunda.setStatusMessage("Object out of sight.");
				return;
			}
			
			if (validSustainedFireTargetFighterNodes.isEmpty() && (! validTargetFighterNodes.contains(fighterNodeUnderCursor))) {
				Necromunda.setStatusMessage("This fighter is not the nearest target.");
				return;
			}
			
			boolean targetAdded = addTarget(fighterNodeUnderCursor);
			
			if (targetAdded) {
				weapon.targetAdded();
			}
			
			if (weapon.getNumberOfShots() > 0) {
				return;
			}
			
			fireTargetedWeapon(weapon);
			
			removeTargetingFacilities();
			
			targetedFighterNodes.clear();
			validSustainedFireTargetFighterNodes.clear();
		}

		game.setSelectionMode(SelectionMode.SELECT);
	}

	private void onRightClick() {
		if (game.getSelectionMode().equals(SelectionMode.MOVE)) {
			tearDownCurrentPath();
		}
		else if (game.getSelectionMode().equals(SelectionMode.CLIMB)) {
			abortClimbing();
		}
		else if (game.getSelectionMode().equals(SelectionMode.TARGET)) {
			removeTargetingFacilities();
		}
		
		game.setSelectionMode(SelectionMode.SELECT);

		deselectFighter();
	}

	private FighterNode getFighterNodeUnderCursor() {
		List<Collidable> collidables = new ArrayList<Collidable>();
		collidables.add(getObjectsNode());
		CollisionResult closestCollision = Utils.getNearestCollisionFrom(cam.getLocation(), cam.getDirection(), collidables);
		
		FighterNode fighterNodeUnderCursor;

		if (closestCollision == null) {
			fighterNodeUnderCursor = null;
		}
		else {
			Geometry geometry = closestCollision.getGeometry();
			FighterNode fighterNode = (FighterNode)getParent(geometry, "fighterNode");
	
			fighterNodeUnderCursor = fighterNode;
		}
		
		return fighterNodeUnderCursor;
	}
	
	private void updateValidTargetFighterNodes() {
		validTargetFighterNodes.clear();
		List<FighterNode> visibleHostileFighterNodes = getFighterNodesWithLineOfSightFrom(selectedFighterNode, getHostileFighterNodesFrom(fighterNodes));
		
		boolean normalFighterNodeFound = false;
		
		while ((! normalFighterNodeFound) && (! visibleHostileFighterNodes.isEmpty())) {
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
		
		List<FighterNode> sustainedFireNeighbours = getFighterNodesWithinDistance(getFighterNodeUnderCursor(), Necromunda.SUSTAINED_FIRE_RADIUS);
		
		validSustainedFireTargetFighterNodes.add(fighterNode);
		validSustainedFireTargetFighterNodes.addAll(sustainedFireNeighbours);
	}
	
	private boolean addSubsequentTarget(FighterNode fighterNode) {
		if (validSustainedFireTargetFighterNodes.contains(fighterNode)) {
			targetedFighterNodes.add(fighterNode);
			return true;
		}
		else {
			Necromunda.setStatusMessage("This target is too far away from the first.");
			return false;
		}
	}

	private void tearDownCurrentPath() {
		if (currentPath != null) {
			selectedFighterNode.setLocalTranslation(currentPath.getOrigin());
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
	}

	private void commitClimb() {
		Fighter selectedObject = game.getSelectedFighter();

		float distance = currentClimbPath.getLength();
		System.out.println("Climb Path Length: " + distance);
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
	}

	private Geometry getPathBoxGeometryFor(FighterNode fighterNode) {
		Vector3f halfExtents = getHalfExtentsOf(fighterNode);

		float pathLength = 0;

		if (currentPath != null) {
			pathLength = currentPath.length();
		}

		Box box = new Box(halfExtents.getX(), halfExtents.getY(), pathLength / 2);
		Geometry boxGeometry = new Geometry("currentPathBoxGeometry", box);
		boxGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.PATH));
		boxGeometry.setQueueBucket(Bucket.Transparent);

		return boxGeometry;
	}

	private CollisionShape getPathBoxCollisionShapeOf(FighterNode fighterNode, Line path) {
		Vector3f halfExtents = getHalfExtentsOf(fighterNode);
		float pathLength = 0;

		if (path != null) {
			pathLength = path.length();
		}

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

		if (fighter.getGang() == game.getCurrentGang()) {
			game.setSelectedFighter(fighter);
			game.updateStatus();
		}
	}

	private void deselectFighter() {
		game.setSelectedFighter(null);
		game.updateStatus();
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
		/*for (FighterNode otherFighterNode : fighterNodes) {
			System.out.println(otherFighterNode + ", " + otherFighterNode.isPositionValid());
		}*/

		if (! fighterNode.isPositionValid() || isPhysicsLocked()) {
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
		//crosshairs
		crosshair.setText("+");
		//center
		crosshair.setLocalTranslation(settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2, settings.getHeight() / 2 + crosshair.getLineHeight() / 2, 0);
		guiNode.attachChild(crosshair);
	}

	private void initStatusMessage() {
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		statusMessage = new BitmapText(guiFont, false);
		statusMessage.setSize(guiFont.getCharSet().getRenderedSize());
		statusMessage.setLocalTranslation(10, 120, 0);
		guiNode.attachChild(statusMessage);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		Vector3f nearestIntersection = getSceneryCollisionPoint();

		if (nearestIntersection == null) {
			return;
		}

		if (game.getSelectionMode().equals(SelectionMode.MOVE)) {
			setUpMovement();
		}
		else if (game.getSelectionMode().equals(SelectionMode.TARGET)) {
			setUpTargeting();
		}
		else if (game.getSelectionMode().equals(SelectionMode.DEPLOY)) {
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

		updateModels();
	}
	
	private boolean hasLineOfSight(FighterNode source, FighterNode target, List<Collidable> collidables) {
		Vector3f sourceUpTranslation = new Vector3f(0, source.getFighter().getBaseRadius() * 1.5f, 0);
		Vector3f targetUpTranslation = new Vector3f(0, target.getFighter().getBaseRadius() * 1.5f, 0);
		
		Vector3f sourceLocation = source.getLocalTranslation().add(sourceUpTranslation);
		Vector3f targetLocation = target.getLocalTranslation().add(targetUpTranslation);
		
		Vector3f direction = targetLocation.subtract(sourceLocation);
		
		CollisionResult closestCollision = Utils.getNearestCollisionFrom(sourceLocation, direction, collidables);
		
		if (closestCollision != null) {
			float distanceToTarget = direction.length();
			float distanceToCollisionPoint = closestCollision.getContactPoint().subtract(sourceLocation).length();
			
			if (distanceToCollisionPoint >= distanceToTarget) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}
	
	private List<Collidable> getBoundingVolumes() {
		List<Collidable> boundingVolumes = new ArrayList<Collidable>();
		
		for (TemplateNode templateNode : templateNodes) {
			boundingVolumes.addAll(templateNode.getBoundingSpheres());
		}
		
		return boundingVolumes;
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

	private Vector3f getLadderCollisionPoint(Ladder ladder) {
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
			
			if ((a == selectedCollisionShapeNode) && (b.getName().equals("buildingsNode"))
					|| (b == selectedCollisionShapeNode) && (a.getName().equals("buildingsNode"))) {
				selectedFighterNode.setPositionValid(false);
			}
			else if ((a == selectedCollisionShapeNode) && (b.getName().equals("collisionShapeNode"))
					|| (b == selectedCollisionShapeNode) && (a.getName().equals("collisionShapeNode"))) {
				selectedFighterNode.setPositionValid(false);
			}
			else if ((b.getName().equals("currentLineOfSightBoxNode") && ((a.getName().equals("collisionShapeNode") && (a != selectedCollisionShapeNode) && (a != targetedCollisionShapeNode)))
					|| (a.getName().equals("currentLineOfSightBoxNode")) && ((b.getName().equals("collisionShapeNode") && (b != selectedCollisionShapeNode) && (b != targetedCollisionShapeNode))))) {
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
			else if ((a.getName().equals("currentPathBoxNode")) && (b.getName().equals("buildingsNode"))
					|| (b.getName().equals("currentPathBoxNode")) && (a.getName().equals("buildingsNode"))) {
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

	private class KeyboardListener implements ActionListener {

		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (isPressed) {
				executeAction(name);
			}

			game.updateStatus();
		}
		
		private void executeAction(String name) {
			Necromunda.setStatusMessage("");
			Fighter selectedFighter = game.getSelectedFighter();
			
			if (name.equals("NextPhase")) {
				if (selectedFighterNode != null) {
					tearDownCurrentPath();
					game.setSelectionMode(SelectionMode.SELECT);
				}

				game.nextPhase();
			}
			else if (name.equals("EndTurn")) {
				if (selectedFighterNode != null) {
					tearDownCurrentPath();
					game.setSelectionMode(SelectionMode.SELECT);
					deselectFighter();
				}

				game.endTurn();
				turnStarted();
			}
			else if (selectedFighter == null) {
				Necromunda.setStatusMessage("You must select a fighter first.");
			}
			else {
				if (game.getSelectionMode().equals(SelectionMode.SELECT)) {
					if (name.equals("Break") && game.getPhase().equals(Phase.MOVEMENT)) {
						if (selectedFighter.isPinned()) {
							Necromunda.setStatusMessage("This ganger can not break the web.");
						}
						else {
							if (selectedFighter.isWebbed()) {
								int webRoll = Utils.rollD6();
	
								if ((webRoll + selectedFighter.getStrength()) >= 9) {
									selectedFighter.setWebbed(false);
									Necromunda.appendToStatusMessage("This ganger has broken the web.");
								}
								else {
									WebPistol.dealWebDamageTo(selectedFighter);
								}
							}
							else {
								Necromunda.setStatusMessage("This ganger is not webbed.");
							}
						}
					}
					else if (name.equals("Move") && game.getPhase().equals(Phase.MOVEMENT)) {
						if (selectedFighter.canMove() && !selectedFighter.hasRun()) {
							if (! selectedFighter.hasMoved()) {
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
							if (! selectedFighter.hasRun()) {
								selectedFighter.setIsGoingToRun(true);
							}
	
							game.setSelectionMode(SelectionMode.MOVE);
							setUpMovement();
						}
						else {
							Necromunda.setStatusMessage("This ganger cannot run.");
						}
					}
					else if (name.equals("Shoot") && game.getPhase().equals(Phase.SHOOTING)) {
						if (selectedFighter.canShoot()) {
							if (! selectedFighter.getWeapons().isEmpty()) {
								RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();
	
								if (weapon == null) {
									weapon = (RangeCombatWeapon)selectedFighter.getWeapons().get(0);
									selectedFighter.setSelectedRangeCombatWeapon(weapon);
								}
	
								if (weapon.isBroken()) {
									Necromunda.setStatusMessage("The selected weapon is broken.");
								}
								else if (! weapon.isEnabled()) {
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
	
						if (! weapons.isEmpty()) {
							RangeCombatWeapon weapon = selectedFighter.getSelectedRangeCombatWeapon();
	
							if (weapon == null) {
								weapon = (RangeCombatWeapon)selectedFighter.getWeapons().get(0);
							}
							else {
								int index = weapons.indexOf(weapon);
	
								if (index < weapons.size() - 1) {
									weapon = (RangeCombatWeapon)weapons.get(index + 1);
								}
								else {
									weapon = (RangeCombatWeapon)selectedFighter.getWeapons().get(0);
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
				else if (name.equals("Climb") && game.getSelectionMode().equals(SelectionMode.MOVE)) {
					Ladder nearestLadder = getNearestLadder(currentPath.getOrigin(), selectedFighter.getBaseRadius());
	
					if (nearestLadder == null) {
						Necromunda.setStatusMessage("There is no ladder in reach.");
					}
					else {
						game.setSelectionMode(SelectionMode.CLIMB);
						Vector3f currentPathOrigin = currentPath.getOrigin();
						tearDownCurrentPath();
						currentClimbPath = new ClimbPath(currentPathOrigin.clone());
						Vector3f nearestLadderCollisionPoint = getLadderCollisionPoint(nearestLadder);
						currentClimbPath.addToLength(nearestLadderCollisionPoint.distance(selectedFighterNode.getLocalTranslation()));
						selectedFighterNode.setLocalTranslation(getLadderCollisionPoint(nearestLadder.getPeer()));
						currentClimbPath.addToLength(nearestLadderCollisionPoint.distance(selectedFighterNode.getLocalTranslation()));
					}
				}
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

	private Ladder getNearestLadder(Vector3f origin, float baseRadius) {
		float distance = Float.MAX_VALUE;
		Ladder nearestLadder = null;

		for (Ladder ladder : ladders) {
			float tempDistance = ladder.getWorldStart().distance(origin);

			if (tempDistance < distance) {
				distance = tempDistance;
				nearestLadder = ladder;
			}
		}

		if ((distance - baseRadius) > MAX_LADDER_DISTANCE) {
			nearestLadder = null;
		}

		return nearestLadder;
	}

	private void unpinFighters() {
		for (FighterNode fighterNode : fighterNodes) {
			Fighter fighter = fighterNode.getFighter();

			if (game.getCurrentGang().getGangMembers().contains(fighter) && fighter.isPinned()) {
				List<FighterNode> surroundingFighterNodes = getFighterNodesWithinDistance(fighterNode, Necromunda.UNPIN_BY_INITIATIVE_DISTANCE);
				List<Fighter> reliableMates = new ArrayList<Fighter>();

				for (FighterNode surroundingFighterNode : surroundingFighterNodes) {
					Fighter surroundingFighter = surroundingFighterNode.getFighter();

					if (fighter.getGang().getGangMembers().contains(surroundingFighter) && surroundingFighter.isReliableMate()) {
						reliableMates.add(surroundingFighter);
					}
				}

				if (! reliableMates.isEmpty() || fighter instanceof Leader) {
					if (fighter.unpinByInitiative()) {
						Necromunda.appendToStatusMessage(String.format("%s unpins by initiative.", fighter));
					}
					else {
						Necromunda.appendToStatusMessage(String.format("%s fails to unpin by initiative.", fighter));
					}
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
		CollisionShape boxCollisionShape = getPathBoxCollisionShapeOf(selectedFighterNode, currentPath);
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

		currentPathBoxNode.attachChild(getPathBoxGeometryFor(selectedFighterNode));

		rootNode.attachChild(currentPathBoxNode);

		Vector3f halfExtents = getHalfExtentsOf(selectedFighterNode);
		Vector3f upTranslation = new Vector3f(0, halfExtents.getY(), 0);
		Vector3f vector = currentPath.getOrigin().add(currentPath.getVector().mult(0.5f)).addLocal(upTranslation);
		currentPathBoxNode.setLocalTranslation(vector);
		currentPathBoxNode.lookAt(currentPath.getDirection().add(upTranslation), Vector3f.UNIT_Y);

		selectedFighterNode.setLocalTranslation(currentPath.getDirection());
		lockPhysics();
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
		}
	}

	private void updateCurrentLineOfSight() {
		Vector3f sourceCenter = selectedFighterNode.getLocalTranslation().add(Vector3f.UNIT_Y);
		Vector3f targetCenter = getFighterNodeUnderCursor().getLocalTranslation().add(Vector3f.UNIT_Y);

		if (currentLineOfSight == null) {
			currentLineOfSight = new Line(sourceCenter, targetCenter);
		}
		else {
			currentLineOfSight.getOrigin().set(sourceCenter);
			currentLineOfSight.getDirection().set(targetCenter);
		}
	}

	private void updateCurrentLineOfSightLine() {
		Geometry lineGeometry = (Geometry)rootNode.getChild("currentLineOfSightLine");

		Vector3f start = currentLineOfSight.getOrigin();
		Vector3f end = currentLineOfSight.getDirection();

		if (lineGeometry == null) {
			com.jme3.scene.shape.Line line = new com.jme3.scene.shape.Line(start, end);
			lineGeometry = new Geometry("currentLineOfSightLine", line);
			lineGeometry.setMaterial(materialFactory.createMaterial(MaterialIdentifier.SELECTED));

			rootNode.attachChild(lineGeometry);
		}
		else {
			com.jme3.scene.shape.Line line = (com.jme3.scene.shape.Line)lineGeometry.getMesh();
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
			if (hasLineOfSight(source, fighterNode, collidables)) {
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

	private List<FighterNode> getFighterNodesWithinDistance(FighterNode fighterNode, float maxDistance) {
		List<FighterNode> otherFighterNodes = new ArrayList<FighterNode>();
		Fighter fighter = fighterNode.getFighter();

		for (FighterNode otherFighterNode : fighterNodes) {
			if (otherFighterNode == fighterNode) {
				continue;
			}

			Fighter otherFighter = otherFighterNode.getFighter();
			float distance = fighterNode.getLocalTranslation().distance(otherFighterNode.getLocalTranslation());
			distance -= fighter.getBaseRadius() + otherFighter.getBaseRadius();

			if (distance < maxDistance) {
				otherFighterNodes.add(otherFighterNode);
			}
		}

		return otherFighterNodes;
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
	
	private void fireTargetedWeapon(RangeCombatWeapon weapon) {
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
			
			int targetHitRoll = 7 - selectedFighter.getBallisticSkill() - weapon.getRangeModifier(distance);
			
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

		if (! currentTemplateNode.isTemplatePersistent()) {
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
			List<FighterNode> fighterNodesWithinRange = getFighterNodesWithinDistance(affectedFighterNode, weapon.getAdditionalTargetRange());
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
			Fighter ganger = (Fighter)game.getSelectedFighter();
			statusText.append(String.format("%s, %s, %sFlesh Wounds: %s\n", ganger.getName(), ganger.getState(), (ganger.isWebbed() ? "Webbed, " : ""), ganger.getFleshWounds()));

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

		if (game.getCurrentGang() != null) {
			statusText.append(game.getCurrentGang().getPhase().toString());
		}
		else {
			statusText.append(" ");
		}

		return statusText.toString();
	}

	@Override
	public void simpleUpdate(float tpf) {
		int millis = (int)(tpf * 1000);
		
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
}
