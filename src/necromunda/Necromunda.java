package necromunda;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Observable;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import necromunda.Gang.Phase;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class Necromunda extends Observable {
	public enum SelectionMode {
		DEPLOY,
		SELECT,
		MOVE,
		CLIMB,
		TARGET,
		REROLL
	}
	
	public static final float RUN_SPOT_DISTANCE = 8;
	public static final float UNPIN_BY_INITIATIVE_DISTANCE = 2;
	public static final float SUSTAINED_FIRE_RADIUS = 4;
	public static final float STRAY_SHOT_RADIUS = 0.5f;
	public static Necromunda game;
	private static String statusMessage;
	
	private List<Gang> gangs;
	private List<Building> buildings;
	private Fighter selectedFighter;
	private SelectionMode selectionMode;
	private Gang currentGang;
	private int turn;
	private JFrame necromundaFrame;
	
	private LinkedList<Fighter> deployQueue;
	
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
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.setDismissDelay(Integer.MAX_VALUE);
		
		Necromunda game = Necromunda.getInstance();
	}
	
	public Necromunda() {
		gangs = new ArrayList<Gang>();
		selectionMode = SelectionMode.DEPLOY;
		turn = 1;
		statusMessage = "";
		buildings = createBuildings();
		
		deployQueue = new LinkedList<Fighter>();
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
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
		
		buildings.add(new Building(FastMath.PI / 4, new Vector3f(8, 0, 8), "01"));
		buildings.add(new Building(FastMath.PI / -8, new Vector3f(40, 0, 8), "01"));
		buildings.add(new Building(FastMath.PI / 2, new Vector3f(6, 0, 40), "01"));
		buildings.add(new Building(0, new Vector3f(24, 0, 22), "03"));
		buildings.add(new Building(0, new Vector3f(38, 0, 40), "01"));
		buildings.add(new Building(FastMath.PI / 8 * 3, new Vector3f(8, 0, 24), "02"));
		buildings.add(new Building(FastMath.PI / 8 * -3, new Vector3f(40, 0, 24), "02"));
		
		return buildings;
	}
	
	private void initialiseGUI() {
		GangGenerationPanel gangGenerationPanel = new GangGenerationPanel(this);
		
		necromundaFrame = new JFrame("Necromunda");
		necromundaFrame.setSize(1000, 800);
		necromundaFrame.setResizable(false);
		necromundaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		necromundaFrame.setLocationRelativeTo(null);
		necromundaFrame.setLayout(new BoxLayout(necromundaFrame.getContentPane(), BoxLayout.PAGE_AXIS));
		necromundaFrame.add(gangGenerationPanel);
		necromundaFrame.setVisible(true);
	}
	
	public void objectDeployed() {
		selectedFighter = deployQueue.poll();
		
		if (selectedFighter == null) {
			currentGang = gangs.get(0);
			currentGang.turnStarted();
			selectionMode = SelectionMode.SELECT;
		}
	}
	
	public List<Fighter> getVisibleObjects(Vector3f sourceCoordinate, List<? extends Fighter> objectsToCheck) {
		List<Fighter> objectsWithLineOfSight = new ArrayList<Fighter>();
		return objectsWithLineOfSight;
	}
	
	public void endTurn() {
		currentGang.turnEnded();
		
		if (((gangs.indexOf(currentGang) + 1) % gangs.size()) == 0) {
			turn++;
		}
		
		currentGang = getNextGang();

		currentGang.turnStarted();
		
		selectedFighter = null;
		
		selectionMode = SelectionMode.SELECT;
	}
	
	private Gang getNextGang() {
		Gang gang = null;
		int gangIndex = gangs.indexOf(currentGang);
		
		if ((gangIndex + 1) == gangs.size()) {
			gang = gangs.get(0);
		}
		else {
			gang = gangs.get(gangIndex + 1);
		}
		
		return gang;
	}
	
	public void nextPhase() {
		switch (currentGang.getPhase()) {
			case MOVEMENT:
				currentGang.setPhase(Phase.SHOOTING);
				break;
			case SHOOTING:
			case HAND_TO_HAND:
			case RECOVERY:
		}
		
		selectionMode = SelectionMode.SELECT;
	}
	
	public void updateStatus() {
		setChanged();
		notifyObservers();
	}
	
	public void commitGeneratedGangs(Enumeration<?> gangs) {		
		while (gangs.hasMoreElements()) {
			Gang nextGang = (Gang)gangs.nextElement();
			
			deployQueue.addAll(nextGang.getGangMembers());
			
			if (getCurrentGang() == null) {
				setCurrentGang(nextGang);
				setSelectedFighter(deployQueue.poll());
			}
			
			getGangs().add(nextGang);
		}
	}

	public Gang getCurrentGang() {
		return currentGang;
	}

	public void setCurrentGang(Gang currentGang) {
		this.currentGang = currentGang;
	}

	public Fighter getSelectedFighter() {
		return selectedFighter;
	}

	public void setSelectedFighter(Fighter selectedFighter) {
		this.selectedFighter = selectedFighter;
	}

	public List<Gang> getGangs() {
		return gangs;
	}

	public JFrame getNecromundaFrame() {
		return necromundaFrame;
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public int getTurn() {
		return turn;
	}

	public static String getStatusMessage() {
		return statusMessage;
	}

	public static void setStatusMessage(String statusMessage) {
		Necromunda.statusMessage = statusMessage;
	}
	
	public static void appendToStatusMessage(String statusMessage) {
		if (Necromunda.statusMessage.equals("")) {
			Necromunda.statusMessage = statusMessage;
		}
		else {
			Necromunda.statusMessage = String.format("%s %s", Necromunda.statusMessage, statusMessage);
		}
	}
	
	public Phase getPhase() {
		return currentGang.getPhase();
	}
	
	public List<Fighter> getHostileGangers() {
		return currentGang.getHostileGangers(gangs);
	}
}
