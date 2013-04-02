package necromunda;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javax.swing.*;

import necromunda.MaterialFactory.MaterialIdentifier;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class Necromunda extends Observable {
	public static final float RUN_SPOT_DISTANCE = 8;
	public static final float UNPIN_BY_INITIATIVE_DISTANCE = 2;
	public static final float SUSTAINED_FIRE_RADIUS = 4;
	public static final float STRAY_SHOT_RADIUS = 0.5f;
	public static Necromunda game;
	private static String statusMessage;
	
	private boolean debug;
	
	private CyclicList<Gang> gangs;
	private List<Building> buildings;
	private CyclicList<Fighter> deployQueue;
	private Gang currentGang;
	private int turn;
	private Phase phase;

	private Map<String, String> terrainTextureMap;
	
	private JFrame necromundaFrame;
	
	public static final int[][] STRENGTH_RESISTANCE_MAP = {
		{4, 5, 6, 6, 7, 7, 7, 7, 7, 7},
		{3, 4, 5, 6, 6, 7, 7, 7, 7, 7},
		{2, 3, 4, 5, 6, 6, 7, 7, 7, 7},
		{2, 2, 3, 4, 5, 6, 6, 7, 7, 7},
		{2, 2, 2, 3, 4, 5, 6, 6, 7, 7},
		{2, 2, 2, 2, 3, 4, 5, 6, 6, 7},
		{2, 2, 2, 2, 2, 3, 4, 5, 6, 6},
		{2, 2, 2, 2, 2, 2, 3, 4, 5, 6},
		{2, 2, 2, 2, 2, 2, 2, 3, 4, 5},
		{2, 2, 2, 2, 2, 2, 2, 2, 3, 4},
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Necromunda game = Necromunda.getInstance();
		
		for (String arg : args) {
			if (arg.equals("-debug")) {
				game.setDebug(true);
			}
		}
	}
	
	public Necromunda() {
		gangs = new CyclicList<Gang>();
		turn = 1;
		statusMessage = "";
		buildings = createBuildings();
		terrainTextureMap = createTerrainTextureMapping();
		
		deployQueue = new CyclicList<Fighter>();
		
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				initialiseGUI();
			}
			
		});
	}
	
	public static Necromunda getInstance() {
		if (game == null) {
			game = new Necromunda();
		}
		
		return game;
	}
	
	private List<Building> createBuildings() {
		List<Building> buildings = new ArrayList<Building>();
		
		Building building = new Building();
		building.putModel("SmallTower", "Tower");
		building.putBounds("SmallTowerBounds01");
		building.putBounds("SmallTowerBounds02");
		building.putBounds("SmallTowerBounds03");
		building.putBounds("SmallTowerBounds04");
		buildings.add(building);
		
		/*building = new Building();
		building.put("MineEntrance");
		buildings.add(building);*/
		
		//options
		
		building = new Building();
		building.putModel("LargeTower", "Tower");
		building.putBounds("LargeTowerBounds01");
		building.putBounds("LargeTowerBounds02");
		building.putBounds("LargeTowerBounds03");
		building.putBounds("LargeTowerBounds04");
		building.putBounds("LargeTowerBounds05");
		building.putBounds("LargeTowerBounds06");
		building.putBounds("LargeTowerBounds07");
		building.putBounds("LargeTowerBounds08");
		building.putBounds("LargeTowerBounds09");
		building.putBounds("LargeTowerBounds10");
		building.putBounds("LargeTowerBounds11");
		buildings.add(building);
		
		/*building = new Building();
		building.put("AcidPoolTank");
		building.put("AcidPoolBridge");
		buildings.add(building);

		building = new Building();
		building.put("PsykerHide");
		buildings.add(building);
		
		building = new Building();
		building.put("Container");
		buildings.add(building);
		
		building = new Building();
		building.put("Mushrooms");
		buildings.add(building);
		
		building = new Building();
		building.put("Barrels");
		buildings.add(building);
		
		building = new Building();
		building.put("WaterPumpControlBody");
		building.putModel("WaterPumpControlWheel", "SimpleRedPaint");
		building.putModel("WaterPumpControlPipes", "MetalNoir");
		buildings.add(building);
		
		building = new Building();
		building.put("Pipe");
		buildings.add(building);
		
		building = new Building();
		building.putModel("Pipe90", "Pipe");
		buildings.add(building);
		
		building = new Building();
		building.putModel("PipeDouble", "Pipe");
		buildings.add(building);
		
		building = new Building();
		building.putModel("PipeLittle", "Pipe");
		buildings.add(building);
		
		building = new Building();
		building.putModel("PipeLittle90", "Pipe");
		buildings.add(building);
		
		building = new Building();
		building.putModel("PipeLittle45", "Pipe");
		buildings.add(building);
		
		building = new Building();
		building.putModel("PipeT", "Pipe");
		buildings.add(building);
		
		building = new Building();
		building.putModel("PipeX", "Pipe");
		buildings.add(building);
		
		building = new Building();
		building.putModel("PipeY", "Pipe");
		buildings.add(building);*/
		
		return buildings;
	}
	
	private Map<String, String> createTerrainTextureMapping() {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("Grass", "Grass.tgr");
		map.put("Toxic Ash", "ToxicAsh.tgr");
		map.put("Dark Ash", "DarkAsh.tgr");
		map.put("Ash Waste", "AshWaste.tgr");
		map.put("Scorched Earth", "ScorchedAsh.tgr");
		/*map.put("Rust Dunes", "RedDunes.tgr");*/
		map.put("Acid Dunes", "GreenDunes.tgr");
				
		return map;
	}
	
	private void initialiseGUI() {
		GangGenerationPanel gangGenerationPanel = new GangGenerationPanel(this);
		
		necromundaFrame = new JFrame("Necromunda");
		necromundaFrame.setSize(1000, 800);
		necromundaFrame.setResizable(false);
		necromundaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		necromundaFrame.setLocationRelativeTo(null);
		necromundaFrame.add(gangGenerationPanel);
		necromundaFrame.setVisible(true);
		
		JOptionPane.showMessageDialog(gangGenerationPanel, "This is an unofficial implementation of Necromunda.\n" +
				" The copyright of Necromunda and all its related\n" +
				" artwork including the ganger pictures contained\n" +
				" in this software is owned by Games Workshop Limited\n" +
				" and is used without permission.");
	}
	
	public void deploymentFinished() {
		currentGang = gangs.get(0);
		currentGang.turnStarted();
		phase = Phase.MOVEMENT;
	}
	
	public void endTurn() {
		currentGang.turnEnded();
		
		if (((gangs.indexOf(currentGang) + 1) % gangs.size()) == 0) {
			turn++;
		}
		
		currentGang = gangs.next();

		currentGang.turnStarted();
		
		phase = Phase.MOVEMENT;
	}
	
	public void nextPhase() {
		switch (phase) {
			case MOVEMENT:
				phase = Phase.SHOOTING;
				break;
			case SHOOTING:
			case HAND_TO_HAND:
			case RECOVERY:
		}
	}
	
	public void commitGeneratedGangs(Enumeration<?> gangs) {		
		while (gangs.hasMoreElements()) {
			Gang nextGang = (Gang)gangs.nextElement();
			
			deployQueue.addAll(nextGang.getGangMembers());
			
			this.gangs.add(nextGang);
		}
	}

	public CyclicList<Fighter> getDeployQueue() {
		return deployQueue;
	}

	public Gang getCurrentGang() {
		return currentGang;
	}

	public void setCurrentGang(Gang currentGang) {
		this.currentGang = currentGang;
	}

	public JFrame getNecromundaFrame() {
		return necromundaFrame;
	}

	public int getTurn() {
		return turn;
	}
	
	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public static String getStatusMessage() {
		return statusMessage;
	}

	public static void setStatusMessage(String statusMessage) {
		Necromunda.statusMessage = statusMessage;
	}
	
	public static void appendToStatusMessage(String statusMessage) {
		String space = "";
		
		if ((Necromunda.statusMessage != null) && (Necromunda.statusMessage.length() > 0)) {
			space = " ";
		}
			
		Necromunda.statusMessage = String.format("%s%s%s", Necromunda.statusMessage, space, statusMessage);
	}
	
	public List<Fighter> getHostileGangers() {
		return currentGang.getHostileGangers(gangs);
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public Map<String, String> getTerrainTextureMap() {
		return terrainTextureMap;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
