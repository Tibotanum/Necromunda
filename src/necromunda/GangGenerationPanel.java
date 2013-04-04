package necromunda;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import com.jme3.system.JmeContext.Type;

import necromunda.MaterialFactory.MaterialIdentifier;

import weapons.*;

public class GangGenerationPanel extends JPanel implements ItemListener {
	private JLabel gangNameLabel;
	private JTextField gangNameTextField;
	private JLabel houseLabel;
	private JComboBox houseComboBox;
	private JLabel fighterNameLabel;
	private JTextField fighterNameTextField;
	private JLabel fighterTypeLabel;
	private JComboBox fighterTypeComboBox;
	private JComboBox weaponComboBox;
	private JComboBox terrainTypeComboBox;
	private JLabel weaponProfileStringLabel;
	private JList fighterWeaponList;
	private JButton okButton;
	private JButton addGangButton;
	private JButton removeGangButton;
	private JButton saveGangButton;
	private JButton loadGangButton;
	private JButton addFighterButton;
	private JButton removeFighterButton;
	private JButton addWeaponButton;
	private JButton removeWeaponButton;

	private JLabel movementLabel;
	private JLabel weaponSkillLabel;
	private JLabel ballisticSkillLabel;
	private JLabel strengthLabel;
	private JLabel toughnessLabel;
	private JLabel woundsLabel;
	private JLabel initiativeLabel;
	private JLabel attacksLabel;
	private JLabel leadershipLabel;
	private JLabel gangRatingLabel;
	private JLabel gangRating;

	private JCheckBox invertMouseCheckBox;
	private JCheckBox createLargeTableCheckBox;

	private JTextField movementTextField;
	private JTextField weaponSkillTextField;
	private JTextField ballisticSkillTextField;
	private JTextField strengthTextField;
	private JTextField toughnessTextField;
	private JTextField woundsTextField;
	private JTextField initiativeTextField;
	private JTextField attacksTextField;
	private JTextField leadershipTextField;

	private JTabbedPane tabbedPane;
	private JPanel mainPanel;
	private JPanel optionsPanel;

	private static JList gangList;
	private JList fighterList;

	private FighterImagePanel fighterImagePanel;
	private JSpinner fighterImageSpinner;

	private List<BasedModelImage> basedModelImages;

	public GangGenerationPanel(final Necromunda game) {
		mainPanel = new JPanel();
		optionsPanel = new JPanel();

		setLayout(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());

		String basePath = "/Images/Textures/Fighters/";

		basedModelImages = new ArrayList<BasedModelImage>();

		/*
		 * The first number is the offset of the left base edge to the left edge
		 * of the image in pixels. The second is the base width on the image in
		 * pixels. This is for scaling the images correctly.
		 */

		basedModelImages.add(new BasedModelImage(basePath + "EscherBoss01.png", 33, 134, House.ESCHER, Fighter.Type.LEADER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger01.png", 9, 130, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger02.png", 2, 127, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger03.png", 17, 128, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger04.png", 0, 129, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger05.png", 0, 113, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger06.png", 32, 115, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger07.png", 0, 116, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger08.png", 30, 114, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherGanger09.png", 18, 115, House.ESCHER, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "EscherHeavy01.png", 0, 129, House.ESCHER, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "EscherJuve01.png", 0, 128, House.ESCHER, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "EscherJuve02.png", 9, 131, House.ESCHER, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter02.png", 53, 173, House.ESCHER,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "DelaqueBoss01.png", 0, 121, House.DELAQUE, Fighter.Type.LEADER));
		basedModelImages.add(new BasedModelImage(basePath + "DelaqueGanger01.png", 1, 125, House.DELAQUE, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "DelaqueGanger02.png", 1, 129, House.DELAQUE, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "DelaqueGanger03.png", 1, 123, House.DELAQUE, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "DelaqueGanger04.png", 1, 123, House.DELAQUE, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "DelaqueHeavy01.png", 1, 125, House.DELAQUE, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "DelaqueHeavy02.png", 21, 114, House.DELAQUE, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "DelaqueJuve01.png", 34, 124, House.DELAQUE, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "DelaqueJuve02.png", 18, 122, House.DELAQUE, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.DELAQUE,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "OrlockBoss01.png", 47, 130, House.ORLOCK, Fighter.Type.LEADER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockBoss02.png", 100, 135, House.ORLOCK, Fighter.Type.LEADER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockGanger01.png", 22, 142, House.ORLOCK, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockGanger02.png", 40, 140, House.ORLOCK, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockGanger03.png", 1, 133, House.ORLOCK, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockGanger04.png", 5, 130, House.ORLOCK, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockGanger05.png", 1, 136, House.ORLOCK, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockGanger06.png", 1, 135, House.ORLOCK, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockGanger07.png", 2, 134, House.ORLOCK, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockGanger08.png", 30, 135, House.ORLOCK, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockHeavy01.png", 2, 143, House.ORLOCK, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockHeavy02.png", 3, 133, House.ORLOCK, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockHeavy03.png", 4, 141, House.ORLOCK, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockJuve01.png", 44, 142, House.ORLOCK, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockJuve02.png", 65, 122, House.ORLOCK, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockJuve03.png", 12, 149, House.ORLOCK, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "OrlockJuve04.png", 0, 145, House.ORLOCK, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.ORLOCK,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "GoliathBoss01.png", 38, 77, House.GOLIATH, Fighter.Type.LEADER));
		basedModelImages.add(new BasedModelImage(basePath + "GoliathGanger01.png", 65, 125, House.GOLIATH, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "GoliathGanger02.png", 5, 128, House.GOLIATH, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "GoliathGanger03.png", 24, 78, House.GOLIATH, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "GoliathGanger04.png", 10, 75, House.GOLIATH, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "GoliathHeavy01.png", 10, 125, House.GOLIATH, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "GoliathJuve01.png", 10, 123, House.GOLIATH, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "GoliathJuve02.png", 39, 79, House.GOLIATH, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "GoliathJuve03.png", 2, 77, House.GOLIATH, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.GOLIATH,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "CawdorBoss01.png", 20, 135, House.CAWDOR, Fighter.Type.LEADER));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorGanger01.png", 0, 127, House.CAWDOR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorGanger02.png", 0, 125, House.CAWDOR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorGanger03.png", 0, 135, House.CAWDOR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorGanger04.png", 0, 135, House.CAWDOR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorGanger05.png", 0, 120, House.CAWDOR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorGanger06.png", 0, 110, House.CAWDOR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorGanger07.png", 0, 107, House.CAWDOR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorHeavy01.png", 5, 130, House.CAWDOR, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorJuve01.png", 18, 132, House.CAWDOR, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorJuve02.png", 0, 130, House.CAWDOR, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorJuve03.png", 17, 119, House.CAWDOR, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "CawdorJuve04.png", 0, 118, House.CAWDOR, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.CAWDOR,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "VanSaarBoss01.png", 19, 126, House.VAN_SAAR, Fighter.Type.LEADER));
		basedModelImages.add(new BasedModelImage(basePath + "VanSaarGanger01.png", 0, 130, House.VAN_SAAR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "VanSaarGanger02.png", 0, 130, House.VAN_SAAR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "VanSaarGanger03.png", 0, 120, House.VAN_SAAR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "VanSaarGanger04.png", 0, 125, House.VAN_SAAR, Fighter.Type.GANGER));
		basedModelImages.add(new BasedModelImage(basePath + "VanSaarHeavy01.png", 0, 131, House.VAN_SAAR, Fighter.Type.HEAVY));
		basedModelImages.add(new BasedModelImage(basePath + "VanSaarJuve01.png", 15, 125, House.VAN_SAAR, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "VanSaarJuve02.png", 20, 130, House.VAN_SAAR, Fighter.Type.JUVE));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.VAN_SAAR,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "RedemptionistPriest01.png", 85, 118, House.REDEMPTIONISTS,
				Fighter.Type.REDEMPTIONIST_PRIEST));
		basedModelImages.add(new BasedModelImage(basePath + "RedemptionistCrusader01.png", 43, 124, House.REDEMPTIONISTS,
				Fighter.Type.REDEMPTIONIST_CRUSADER));
		basedModelImages.add(new BasedModelImage(basePath + "RedemptionistCrusader02.png", 14, 120, House.REDEMPTIONISTS,
				Fighter.Type.REDEMPTIONIST_CRUSADER));
		basedModelImages.add(new BasedModelImage(basePath + "RedemptionistCrusader03.png", 0, 120, House.REDEMPTIONISTS,
				Fighter.Type.REDEMPTIONIST_CRUSADER));
		basedModelImages.add(new BasedModelImage(basePath + "RedemptionistZealot01.png", 0, 120, House.REDEMPTIONISTS,
				Fighter.Type.REDEMPTIONIST_ZEALOT));
		basedModelImages.add(new BasedModelImage(basePath + "RedemptionistDeacon01.png", 0, 124, House.REDEMPTIONISTS,
				Fighter.Type.REDEMPTIONIST_DEACON));
		basedModelImages.add(new BasedModelImage(basePath + "RedemptionistDevotee01.png", 0, 120, House.REDEMPTIONISTS,
				Fighter.Type.REDEMTIONIST_DEVOTEE));
		basedModelImages.add(new BasedModelImage(basePath + "RedemptionistDevotee02.png", 0, 124, House.REDEMPTIONISTS,
				Fighter.Type.REDEMTIONIST_DEVOTEE));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.REDEMPTIONISTS,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "ScavvyBoss01.png", 61, 130, House.SCAVVIES,
				Fighter.Type.SCAVVY_LEADER));
		basedModelImages.add(new BasedModelImage(basePath + "ScavvyScavvy01.png", 49, 123, House.SCAVVIES, Fighter.Type.SCAVVY));
		basedModelImages.add(new BasedModelImage(basePath + "ScavvyScavvy02.png", 16, 125, House.SCAVVIES, Fighter.Type.SCAVVY));
		basedModelImages.add(new BasedModelImage(basePath + "ScavvyScavvy03.png", 66, 127, House.SCAVVIES, Fighter.Type.SCAVVY));
		basedModelImages.add(new BasedModelImage(basePath + "ScavvyScavvy04.png", 0, 125, House.SCAVVIES, Fighter.Type.SCAVVY));
		basedModelImages.add(new BasedModelImage(basePath + "ScavvyScavvy05.png", 0, 123, House.SCAVVIES, Fighter.Type.SCAVVY));
		basedModelImages.add(new BasedModelImage(basePath + "ScavvyScavvy06.png", 0, 125, House.SCAVVIES, Fighter.Type.SCAVVY));
		basedModelImages.add(new BasedModelImage(basePath + "ScavvyScaly01.png", 9, 126, House.SCAVVIES, Fighter.Type.SCALY));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.SCAVVIES,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "RatskinChief01.png", 0, 130, House.RATSKINS, Fighter.Type.RATSKIN_CHIEF));
		basedModelImages
				.add(new BasedModelImage(basePath + "RatskinRatskin01.png", 46, 126, House.RATSKINS, Fighter.Type.RATSKIN));
		basedModelImages
				.add(new BasedModelImage(basePath + "RatskinRatskin02.png", 5, 121, House.RATSKINS, Fighter.Type.RATSKIN));
		basedModelImages
				.add(new BasedModelImage(basePath + "RatskinRatskin03.png", 0, 128, House.RATSKINS, Fighter.Type.RATSKIN));
		basedModelImages
				.add(new BasedModelImage(basePath + "RatskinRatskin04.png", 0, 127, House.RATSKINS, Fighter.Type.RATSKIN));
		basedModelImages.add(new BasedModelImage(basePath + "RatskinTotemWarrior01.png", 43, 122, House.RATSKINS,
				Fighter.Type.RATSKIN_WARRIOR));
		basedModelImages.add(new BasedModelImage(basePath + "RatskinBrave01.png", 13, 127, House.RATSKINS, Fighter.Type.RATSKIN_BRAVE));
		basedModelImages.add(new BasedModelImage(basePath + "RatskinBrave02.png", 0, 122, House.RATSKINS, Fighter.Type.RATSKIN_BRAVE));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.RATSKINS,
				Fighter.Type.BOUNTY_HUNTER));

		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter01.png", 48, 196, House.BOUNTY_HUNTERS,
				Fighter.Type.BOUNTY_HUNTER));
		basedModelImages.add(new BasedModelImage(basePath + "BountyHunter02.png", 53, 173, House.BOUNTY_HUNTERS,
				Fighter.Type.BOUNTY_HUNTER));

		gangNameLabel = new JLabel("Gang Name");
		gangNameTextField = new JTextField();

		houseLabel = new JLabel("Gang House");
		houseComboBox = new JComboBox(House.class.getEnumConstants());

		addGangButton = new JButton("Add Gang");
		addGangButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DefaultListModel model = (DefaultListModel) gangList.getModel();

				House house = (House) houseComboBox.getSelectedItem();

				Gang newGang = new Gang(gangNameTextField.getText(), house);

				model.addElement(newGang);
			}
		});

		saveGangButton = new JButton("Save Gang");
		saveGangButton.setEnabled(false);
		saveGangButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();

					FileOutputStream fos = null;
					ObjectOutputStream oos = null;

					try {
						fos = new FileOutputStream(selectedFile);
						oos = new ObjectOutputStream(fos);
						oos.writeObject(gangList.getSelectedValue());
					}
					catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					finally {
						try {
							if (oos != null) {
								oos.close();
							}
						}
						catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});

		fighterNameLabel = new JLabel("Fighter Name");
		fighterNameTextField = new JTextField();

		SpinnerListModel spinnerListModel = new SpinnerListModel(filterBasedModelImages(basedModelImages, House.ESCHER,
				Fighter.Type.LEADER));
		fighterImageSpinner = new JSpinner(spinnerListModel);
		fighterImagePanel = new FighterImagePanel(spinnerListModel);
		fighterImageSpinner.setPreferredSize(new Dimension(150, 150));
		fighterImageSpinner.setEditor(fighterImagePanel);
		fighterImageSpinner.addChangeListener(fighterImagePanel);
		fighterImageSpinner.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				BasedModelImage image = (BasedModelImage) fighterImageSpinner.getModel().getValue();

				Fighter fighter = Fighter.createInstance(image.getFighterType(), fighterNameTextField.getText(), null);

				FighterProfile profile = fighter.getProfile();

				movementTextField.setText(String.valueOf(profile.getMovement()));
				weaponSkillTextField.setText(String.valueOf(profile.getWeaponSkill()));
				ballisticSkillTextField.setText(String.valueOf(profile.getBallisticSkill()));
				strengthTextField.setText(String.valueOf(profile.getStrength()));
				toughnessTextField.setText(String.valueOf(profile.getToughness()));
				woundsTextField.setText(String.valueOf(profile.getWounds()));
				initiativeTextField.setText(String.valueOf(profile.getInitiative()));
				attacksTextField.setText(String.valueOf(profile.getAttacks()));
				leadershipTextField.setText(String.valueOf(profile.getLeadership()));
			}
		});

		movementLabel = new JLabel("Movement");
		weaponSkillLabel = new JLabel("Weapon Skill");
		ballisticSkillLabel = new JLabel("Ballistic Skill");
		strengthLabel = new JLabel("Strength");
		toughnessLabel = new JLabel("Toughness");
		woundsLabel = new JLabel("Wounds");
		initiativeLabel = new JLabel("Initiative");
		attacksLabel = new JLabel("Attacks");
		leadershipLabel = new JLabel("Leadership");
		gangRatingLabel = new JLabel("Gang Rating: ");
		gangRating = new JLabel("0");

		movementTextField = new JTextField();
		setDefaultSize(movementTextField);
		weaponSkillTextField = new JTextField();
		setDefaultSize(weaponSkillTextField);
		ballisticSkillTextField = new JTextField();
		setDefaultSize(ballisticSkillTextField);
		strengthTextField = new JTextField();
		setDefaultSize(strengthTextField);
		toughnessTextField = new JTextField();
		setDefaultSize(toughnessTextField);
		woundsTextField = new JTextField();
		setDefaultSize(woundsTextField);
		initiativeTextField = new JTextField();
		setDefaultSize(initiativeTextField);
		attacksTextField = new JTextField();
		setDefaultSize(attacksTextField);
		leadershipTextField = new JTextField();
		setDefaultSize(leadershipTextField);

		fighterTypeLabel = new JLabel("Fighter Type");
		fighterTypeComboBox = new JComboBox(Fighter.Type.class.getEnumConstants());
		fighterTypeComboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				updateFighterImages();
			}
		});
		fighterTypeComboBox.setSelectedIndex(0);

		weaponComboBox = new JComboBox();
		weaponComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) weaponComboBox.getMinimumSize().getHeight()));

		weaponComboBox.addItem(new Autopistol());
		weaponComboBox.addItem(new BoltPistol());
		weaponComboBox.addItem(new HandFlamer());
		weaponComboBox.addItem(new Laspistol());
		weaponComboBox.addItem(new NeedlePistol());
		weaponComboBox.addItem(new PlasmaPistol());
		weaponComboBox.addItem(new StubGun());
		weaponComboBox.addItem(new WebPistol());
		weaponComboBox.addItem(new Autogun());
		weaponComboBox.addItem(new Boltgun());
		weaponComboBox.addItem(new Lasgun());
		weaponComboBox.addItem(new NeedleRifle());
		weaponComboBox.addItem(new Shotgun());
		weaponComboBox.addItem(new Flamer());
		weaponComboBox.addItem(new Musket());
		weaponComboBox.addItem(new Blunderbuss());
		weaponComboBox.addItem(new GrenadeLauncher());
		weaponComboBox.addItem(new MeltaGun());
		weaponComboBox.addItem(new PlasmaGun());
		weaponComboBox.addItem(new Autocannon());
		weaponComboBox.addItem(new HeavyBolter());
		weaponComboBox.addItem(new HeavyPlasmaGun());
		weaponComboBox.addItem(new HeavyStubber());
		weaponComboBox.addItem(new Lascannon());
		weaponComboBox.addItem(new MissileLauncher());
		weaponComboBox.addItem(new SpearGun());
		weaponComboBox.addItem(new ScatterCannon());
		weaponComboBox.addItem(new Discus());
		weaponComboBox.addItem(new ThrowingAxe());
		weaponComboBox.addItem(new FragGrenades());
		weaponComboBox.addItem(new KrakGrenades());
		weaponComboBox.addItem(new PlasmaGrenades());
		weaponComboBox.addItem(new ToxBombs());
		weaponComboBox.addItem(new Handbow());
		weaponComboBox.addItemListener(this);

		weaponProfileStringLabel = new JLabel();
		updateWeaponString();

		fighterWeaponList = new JList(new DefaultListModel());
		fighterWeaponList.setPrototypeCellValue("MMMMMMMMMMMMMMMMMMMMMM");
		fighterWeaponList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				Object value = fighterWeaponList.getSelectedValue();

				if (value != null) {
					removeWeaponButton.setEnabled(true);
				}
				else {
					removeWeaponButton.setEnabled(false);
				}
			}
		});

		addWeaponButton = new JButton("Add Weapon");
		addWeaponButton.setEnabled(false);
		addWeaponButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (fighterList.getSelectedValue() != null) {
					Fighter selectedFighter = (Fighter) fighterList.getSelectedValue();

					Weapon weapon = null;

					try {
						weapon = (Weapon) ((Weapon) weaponComboBox.getSelectedItem()).getClass().newInstance();
					}
					catch (InstantiationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					selectedFighter.addWeapon(weapon);
					weapon.setOwner(selectedFighter);
					updateGangRating();

					((DefaultListModel) fighterWeaponList.getModel()).addElement(weapon);
				}
			}
		});

		removeWeaponButton = new JButton("Remove Weapon");
		removeWeaponButton.setEnabled(false);
		removeWeaponButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Fighter selectedFighter = (Fighter) fighterList.getSelectedValue();
				Object[] values = fighterWeaponList.getSelectedValues();
				DefaultListModel model = (DefaultListModel) fighterWeaponList.getModel();

				for (Object o : values) {
					model.removeElement(o);
				}

				selectedFighter.removeAllWeapons();

				for (int i = 0; i < model.getSize(); i++) {
					Weapon weapon = (Weapon) model.getElementAt(i);
					selectedFighter.addWeapon(weapon);
				}

				updateGangRating();
			}
		});

		gangList = new JList(new DefaultListModel());
		gangList.setPrototypeCellValue("MMMMMMMMMMMMMMMMMMMMMM");
		gangList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				Gang selectedGang = (Gang) gangList.getSelectedValue();

				if (selectedGang != null) {
					DefaultListModel model = (DefaultListModel) fighterList.getModel();
					model.removeAllElements();

					for (Fighter fighter : selectedGang.getGangMembers()) {
						model.addElement(fighter);
					}

					updateGangRating();

					addFighterButton.setEnabled(true);
					removeGangButton.setEnabled(true);

					if (!selectedGang.getGangMembers().isEmpty()) {
						saveGangButton.setEnabled(true);
					}
					else {
						saveGangButton.setEnabled(false);
					}

					updateFighterImages();
				}
				else {
					addFighterButton.setEnabled(false);
					removeGangButton.setEnabled(false);
					saveGangButton.setEnabled(false);
				}
			}
		});

		fighterList = new JList(new DefaultListModel());
		fighterList.setPrototypeCellValue("MMMMMMMMMMMMMMMMMMMMMM");
		fighterList.setCellRenderer(new FighterListCellRenderer());
		fighterList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if (fighterList.getSelectedValue() != null) {
					Fighter selectedFighter = (Fighter) fighterList.getSelectedValue();
					FighterProfile profile = selectedFighter.getProfile();

					movementTextField.setText(String.valueOf(profile.getMovement()));
					weaponSkillTextField.setText(String.valueOf(profile.getWeaponSkill()));
					ballisticSkillTextField.setText(String.valueOf(profile.getBallisticSkill()));
					strengthTextField.setText(String.valueOf(profile.getStrength()));
					toughnessTextField.setText(String.valueOf(profile.getToughness()));
					woundsTextField.setText(String.valueOf(profile.getWounds()));
					initiativeTextField.setText(String.valueOf(profile.getInitiative()));
					attacksTextField.setText(String.valueOf(profile.getAttacks()));
					leadershipTextField.setText(String.valueOf(profile.getLeadership()));

					DefaultListModel model = (DefaultListModel) fighterWeaponList.getModel();
					model.removeAllElements();

					for (Weapon weapon : selectedFighter.getWeapons()) {
						model.addElement(weapon);
					}

					addWeaponButton.setEnabled(true);
					removeFighterButton.setEnabled(true);
				}
				else {
					addWeaponButton.setEnabled(false);
					removeFighterButton.setEnabled(false);
				}
			}
		});

		removeGangButton = new JButton("Remove Gang");
		removeGangButton.setEnabled(false);
		removeGangButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Object[] values = gangList.getSelectedValues();
				DefaultListModel model = (DefaultListModel) gangList.getModel();

				for (Object o : values) {
					model.removeElement(o);
				}

				((DefaultListModel) fighterList.getModel()).clear();

				okButton.getAction().setEnabled(getNumberOfFighters() > 0);
			}
		});

		loadGangButton = new JButton("Load Gang");
		loadGangButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();

					FileInputStream fis = null;
					ObjectInputStream ois = null;

					try {
						fis = new FileInputStream(selectedFile);
						ois = new ObjectInputStream(fis);

						DefaultListModel model = (DefaultListModel) gangList.getModel();

						Gang gang = (Gang) ois.readObject();

						model.addElement(gang);

						okButton.getAction().setEnabled(true);
					}
					catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					finally {
						try {
							if (ois != null) {
								ois.close();
							}
						}
						catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});

		addFighterButton = new JButton("Add Fighter");
		addFighterButton.setEnabled(false);
		addFighterButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				BasedModelImage image = (BasedModelImage) fighterImageSpinner.getModel().getValue();

				if ((gangList.getSelectedValue() != null) && (image != null)) {
					Gang selectedGang = (Gang) gangList.getSelectedValue();

					Fighter fighter = Fighter.createInstance(image.getFighterType(), fighterNameTextField.getText(), selectedGang);

					FighterProfile profile = fighter.getProfile();

					profile.setMovement(Integer.parseInt(movementTextField.getText()));
					profile.setWeaponSkill(Integer.parseInt(weaponSkillTextField.getText()));
					profile.setBallisticSkill(Integer.parseInt(ballisticSkillTextField.getText()));
					profile.setStrength(Integer.parseInt(strengthTextField.getText()));
					profile.setToughness(Integer.parseInt(toughnessTextField.getText()));
					profile.setWounds(Integer.parseInt(woundsTextField.getText()));
					profile.setInitiative(Integer.parseInt(initiativeTextField.getText()));
					profile.setAttacks(Integer.parseInt(attacksTextField.getText()));
					profile.setLeadership(Integer.parseInt(leadershipTextField.getText()));

					movementTextField.setText(String.valueOf(profile.getMovement()));
					weaponSkillTextField.setText(String.valueOf(profile.getWeaponSkill()));
					ballisticSkillTextField.setText(String.valueOf(profile.getBallisticSkill()));
					strengthTextField.setText(String.valueOf(profile.getStrength()));
					toughnessTextField.setText(String.valueOf(profile.getToughness()));
					woundsTextField.setText(String.valueOf(profile.getWounds()));
					initiativeTextField.setText(String.valueOf(profile.getInitiative()));
					attacksTextField.setText(String.valueOf(profile.getAttacks()));
					leadershipTextField.setText(String.valueOf(profile.getLeadership()));

					fighter.setFighterImage(image);

					addFighter(fighter);
					
					generateScavvyFollowers();
					
					updateGangRating();
				}

				okButton.getAction().setEnabled(true);
				saveGangButton.setEnabled(true);
			}
		});

		removeFighterButton = new JButton("Remove Fighter");
		removeFighterButton.setEnabled(false);
		removeFighterButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Gang selectedGang = (Gang) gangList.getSelectedValue();
				Object[] values = fighterList.getSelectedValues();
				DefaultListModel model = (DefaultListModel) fighterList.getModel();

				for (Object o : values) {
					model.removeElement(o);

					if (o instanceof Fighter) {
						selectedGang.removeFighter((Fighter) o);
					}
				}

				updateGangRating();

				saveGangButton.setEnabled(!selectedGang.getGangMembers().isEmpty());

				okButton.getAction().setEnabled(getNumberOfFighters() > 0);
			}
		});

		okButton = new JButton("OK");
		OKButtonAction action = new OKButtonAction("OK", game);
		okButton.setAction(action);

		tabbedPane = new JTabbedPane();

		invertMouseCheckBox = new JCheckBox("Invert mouse");
		invertMouseCheckBox.setSelected(true);

		createLargeTableCheckBox = new JCheckBox("Large table");
		createLargeTableCheckBox.setSelected(true);

		terrainTypeComboBox = new JComboBox(game.getTerrainTextureMap().keySet().toArray());

		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(0, 4, 5, 5));
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panel1.add(gangNameLabel);
		panel1.add(gangNameTextField);
		panel1.add(addGangButton);
		panel1.add(saveGangButton);
		panel1.add(houseLabel);
		panel1.add(houseComboBox);
		panel1.add(removeGangButton);
		panel1.add(loadGangButton);
		panel1.add(fighterNameLabel);
		panel1.add(fighterNameTextField);
		panel1.add(addFighterButton);
		panel1.add(removeFighterButton);
		panel1.add(fighterTypeLabel);
		panel1.add(fighterTypeComboBox);

		optionsPanel.add(invertMouseCheckBox);
		optionsPanel.add(createLargeTableCheckBox);
		optionsPanel.add(terrainTypeComboBox);

		Box mainBox = Box.createHorizontalBox();
		mainBox.add(fighterImageSpinner);

		JScrollPane gangListScrollPane = new JScrollPane(gangList);
		mainBox.add(gangListScrollPane);

		JScrollPane fighterListScrollPane = new JScrollPane(fighterList);
		mainBox.add(fighterListScrollPane);

		Box subBox = Box.createVerticalBox();
		subBox.add(gangListScrollPane);
		subBox.add(fighterListScrollPane);

		mainBox.add(subBox);

		Box panel2 = Box.createVerticalBox();
		panel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		GridLayout layout3 = new GridLayout(2, 2, 5, 5);
		JPanel panel3 = new JPanel();
		panel3.setLayout(layout3);

		panel3.add(weaponComboBox);
		panel3.add(weaponProfileStringLabel);
		panel3.add(addWeaponButton);
		panel3.add(removeWeaponButton);

		panel2.add(panel3);

		panel2.add(Box.createRigidArea(new Dimension(5, 5)));

		JScrollPane fighterWeaponListScrollPane = new JScrollPane(fighterWeaponList);
		panel2.add(fighterWeaponListScrollPane);
		panel2.add(Box.createRigidArea(new Dimension(5, 5)));

		Box box4 = Box.createHorizontalBox();
		box4.add(gangRatingLabel);
		box4.add(gangRating);

		panel2.add(box4);
		panel2.add(Box.createRigidArea(new Dimension(5, 5)));

		Box box5 = Box.createHorizontalBox();

		Box box6 = Box.createVerticalBox();

		box6.add(movementLabel);
		box6.add(movementTextField);

		box5.add(box6);

		Box box7 = Box.createVerticalBox();

		box7.add(weaponSkillLabel);
		box7.add(weaponSkillTextField);

		box5.add(box7);

		Box box8 = Box.createVerticalBox();

		box8.add(ballisticSkillLabel);
		box8.add(ballisticSkillTextField);

		box5.add(box8);

		Box box9 = Box.createVerticalBox();

		box9.add(strengthLabel);
		box9.add(strengthTextField);

		box5.add(box9);

		Box box10 = Box.createVerticalBox();

		box10.add(toughnessLabel);
		box10.add(toughnessTextField);

		box5.add(box10);

		Box box11 = Box.createVerticalBox();

		box11.add(woundsLabel);
		box11.add(woundsTextField);

		box5.add(box11);

		Box box12 = Box.createVerticalBox();

		box12.add(initiativeLabel);
		box12.add(initiativeTextField);

		box5.add(box12);

		Box box13 = Box.createVerticalBox();

		box13.add(attacksLabel);
		box13.add(attacksTextField);

		box5.add(box13);

		Box box14 = Box.createVerticalBox();

		box14.add(leadershipLabel);
		box14.add(leadershipTextField);

		box5.add(box14);

		panel2.add(box5);
		panel2.add(Box.createRigidArea(new Dimension(5, 5)));

		mainPanel.add(panel1, BorderLayout.NORTH);
		mainPanel.add(mainBox, BorderLayout.CENTER);
		mainPanel.add(panel2, BorderLayout.SOUTH);

		tabbedPane.add("Gang Creation", mainPanel);
		tabbedPane.add("Options", optionsPanel);

		JPanel okButtonPanel = new JPanel();
		okButtonPanel.add(okButton);

		add(tabbedPane, BorderLayout.CENTER);
		add(okButtonPanel, BorderLayout.SOUTH);
	}
	
	private void addFighter(Fighter fighter) {
		Gang selectedGang = (Gang) gangList.getSelectedValue();
		selectedGang.addFighter(fighter);
		((DefaultListModel) fighterList.getModel()).addElement(fighter);
	}

	private List<BasedModelImage> filterBasedModelImages(List<BasedModelImage> basedModelImages, House house,
			Fighter.Type fighterType) {
		List<BasedModelImage> filteredBasedModelImages = new ArrayList<BasedModelImage>();

		for (BasedModelImage basedModelImage : basedModelImages) {
			if ((basedModelImage.getHouse().equals(house) && basedModelImage.getFighterType().equals(fighterType))
					|| basedModelImage.getHouse().equals(House.BOUNTY_HUNTERS)) {
				filteredBasedModelImages.add(basedModelImage);
			}
		}

		return filteredBasedModelImages;
	}

	private void updateFighterImages() {
		if (gangList != null) {
			Gang selectedGang = (Gang) gangList.getSelectedValue();

			if (selectedGang != null) {
				House house = selectedGang.getHouse();
				Fighter.Type fighterType = (Fighter.Type) fighterTypeComboBox.getSelectedItem();
				List<BasedModelImage> filteredBasedModelImages = filterBasedModelImages(basedModelImages, house, fighterType);

				if (!filteredBasedModelImages.isEmpty()) {
					SpinnerListModel spinnerListModel = new SpinnerListModel(filteredBasedModelImages);
					fighterImageSpinner.setModel(spinnerListModel);
					fighterImagePanel = new FighterImagePanel(spinnerListModel);
					fighterImageSpinner.removeChangeListener((ChangeListener) fighterImageSpinner.getEditor());
					fighterImageSpinner.setEditor(fighterImagePanel);
					fighterImageSpinner.addChangeListener(fighterImagePanel);
				}
			}
		}

		updateProfileTextFields();
	}

	private void updateProfileTextFields() {
		BasedModelImage image = (BasedModelImage) fighterImageSpinner.getModel().getValue();

		if (image != null) {
			Fighter fighter = Fighter.createInstance(image.getFighterType(), fighterNameTextField.getText(), null);

			FighterProfile profile = fighter.getProfile();

			movementTextField.setText(String.valueOf(profile.getMovement()));
			weaponSkillTextField.setText(String.valueOf(profile.getWeaponSkill()));
			ballisticSkillTextField.setText(String.valueOf(profile.getBallisticSkill()));
			strengthTextField.setText(String.valueOf(profile.getStrength()));
			toughnessTextField.setText(String.valueOf(profile.getToughness()));
			woundsTextField.setText(String.valueOf(profile.getWounds()));
			initiativeTextField.setText(String.valueOf(profile.getInitiative()));
			attacksTextField.setText(String.valueOf(profile.getAttacks()));
			leadershipTextField.setText(String.valueOf(profile.getLeadership()));
		}
	}

	private int getNumberOfFighters() {
		int number = 0;

		DefaultListModel gangModel = (DefaultListModel) gangList.getModel();

		for (Object o : gangModel.toArray()) {
			Gang gang = (Gang) o;

			number += gang.getGangMembers().size();
		}

		return number;
	}

	private void setDefaultSize(JComponent component) {
		component.setPreferredSize(new Dimension(20, 30));
		component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
	}

	/*
	 * private void updateWeaponProfileLabel() { Weapon selectedWeapon =
	 * (Weapon)weaponComboBox.getSelectedItem();
	 * 
	 * if (selectedWeapon != null) {
	 * weaponProfileStringLabel.setText(selectedWeapon.getProfileString()); } }
	 */

	private void updateGangRating() {
		Gang selectedGang = (Gang) gangList.getSelectedValue();
		gangRating.setText(Integer.toString(selectedGang.getGangRating()));
	}

	private class OKButtonAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2075713189081422100L;
		private Necromunda game;

		public OKButtonAction(String name, Necromunda game) {
			super(name);
			this.game = game;
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			DefaultListModel model = (DefaultListModel) gangList.getModel();
			Enumeration<?> gangs = model.elements();
			game.commitGeneratedGangs(gangs);

			final Necromunda3dProvider necromunda3dProvider = new Necromunda3dProvider(game);
			necromunda3dProvider.setInvertMouse(invertMouseCheckBox.isSelected());
			necromunda3dProvider.setCreateLargeTable(createLargeTableCheckBox.isSelected());
			String materialIdentifier = game.getTerrainTextureMap().get(terrainTypeComboBox.getSelectedItem());
			necromunda3dProvider.setTerrainMaterialIdentifier(materialIdentifier);

			Thread thread = new Thread(new Runnable() {

				public void run() {
					necromunda3dProvider.start();
				}

			});

			thread.start();

			((JFrame) getTopLevelAncestor()).dispose();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		ItemSelectable itemSelectable = e.getItemSelectable();

		if ((itemSelectable == weaponComboBox) && (e.getStateChange() == ItemEvent.SELECTED)) {
			updateWeaponString();
		}
	}

	private void updateWeaponString() {
		Weapon selectedWeapon = (Weapon) weaponComboBox.getSelectedItem();
		weaponProfileStringLabel.setText(selectedWeapon.getProfileString());
	}

	// Scavvy follower generation

	public void generateScavvyFollowers() {
		// If the model added is a Scavvy Boss then generate his wasteland
		// followers.
		// The code is placed here so that it only runs once when the Boss has
		// been added to the roster
		// and doesn't overwrite his stats.

		Fighter.Type fighterType = (Fighter.Type) fighterTypeComboBox.getSelectedItem();

		if (fighterType == Fighter.Type.SCAVVY_LEADER) {

			switch (Utils.rollD(3)) {
			case 1:
				switch (Utils.rollD(3)) {
				case 1:
					generateThreeZombies();
					break;
				case 2:
					generateFourZombies();
					break;
				case 3:
					generateFiveZombies();
					break;
				}
				break;
			case 2:
				switch (Utils.rollD(3)) {
				case 1:
					generateTwoDogs();
					break;
				case 2:
					generateThreeDogs();
					break;
				case 3:
					generateFourDogs();
					break;
				}
				break;
			case 3:
				switch (Utils.rollD(3)) {
				case 1:
					generateGhoul();
					break;
				case 2:
					generateTwoGhouls();
					break;
				case 3:
					generateThreeGhouls();
					break;
				}
				break;
			}
		}
	}

	// //Scavvy Zombies

	public void generateThreeZombies() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage zombieImage01 = new BasedModelImage(basePath + "ScavvyZombie01.png", 21, 154, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage02 = new BasedModelImage(basePath + "ScavvyZombie02.png", 0, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage03 = new BasedModelImage(basePath + "ScavvyZombie03.png", 0, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);

		Fighter zombie01 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie02 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie03 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);

		FighterProfile zombieProfile01 = zombie01.getProfile();
		FighterProfile zombieProfile02 = zombie02.getProfile();
		FighterProfile zombieProfile03 = zombie03.getProfile();

		zombieProfile01.setMovement(zombieProfile01.getMovement());
		zombieProfile01.setWeaponSkill(zombieProfile01.getWeaponSkill());
		zombieProfile01.setBallisticSkill(zombieProfile01.getBallisticSkill());
		zombieProfile01.setStrength(zombieProfile01.getStrength());
		zombieProfile01.setToughness(zombieProfile01.getToughness());
		zombieProfile01.setWounds(zombieProfile01.getWounds());
		zombieProfile01.setInitiative(zombieProfile01.getInitiative());
		zombieProfile01.setAttacks(zombieProfile01.getAttacks());
		zombieProfile01.setLeadership(zombieProfile01.getLeadership());

		zombieProfile02.setMovement(zombieProfile02.getMovement());
		zombieProfile02.setWeaponSkill(zombieProfile02.getWeaponSkill());
		zombieProfile02.setBallisticSkill(zombieProfile02.getBallisticSkill());
		zombieProfile02.setStrength(zombieProfile02.getStrength());
		zombieProfile02.setToughness(zombieProfile02.getToughness());
		zombieProfile02.setWounds(zombieProfile02.getWounds());
		zombieProfile02.setInitiative(zombieProfile02.getInitiative());
		zombieProfile02.setAttacks(zombieProfile02.getAttacks());
		zombieProfile02.setLeadership(zombieProfile02.getLeadership());

		zombieProfile03.setMovement(zombieProfile03.getMovement());
		zombieProfile03.setWeaponSkill(zombieProfile03.getWeaponSkill());
		zombieProfile03.setBallisticSkill(zombieProfile03.getBallisticSkill());
		zombieProfile03.setStrength(zombieProfile03.getStrength());
		zombieProfile03.setToughness(zombieProfile03.getToughness());
		zombieProfile03.setWounds(zombieProfile03.getWounds());
		zombieProfile03.setInitiative(zombieProfile03.getInitiative());
		zombieProfile03.setAttacks(zombieProfile03.getAttacks());
		zombieProfile03.setLeadership(zombieProfile03.getLeadership());

		zombie01.setFighterImage(zombieImage01);
		zombie02.setFighterImage(zombieImage02);
		zombie03.setFighterImage(zombieImage03);

		addFighter(zombie01);
		addFighter(zombie02);
		addFighter(zombie03);
	}

	public void generateFourZombies() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage zombieImage01 = new BasedModelImage(basePath + "ScavvyZombie01.png", 21, 154, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage02 = new BasedModelImage(basePath + "ScavvyZombie02.png", 0, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage03 = new BasedModelImage(basePath + "ScavvyZombie03.png", 0, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage04 = new BasedModelImage(basePath + "ScavvyZombie01.png", 21, 154, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);

		Fighter zombie01 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie02 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie03 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie04 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);

		FighterProfile zombieProfile01 = zombie01.getProfile();
		FighterProfile zombieProfile02 = zombie02.getProfile();
		FighterProfile zombieProfile03 = zombie03.getProfile();
		FighterProfile zombieProfile04 = zombie04.getProfile();

		zombieProfile01.setMovement(zombieProfile01.getMovement());
		zombieProfile01.setWeaponSkill(zombieProfile01.getWeaponSkill());
		zombieProfile01.setBallisticSkill(zombieProfile01.getBallisticSkill());
		zombieProfile01.setStrength(zombieProfile01.getStrength());
		zombieProfile01.setToughness(zombieProfile01.getToughness());
		zombieProfile01.setWounds(zombieProfile01.getWounds());
		zombieProfile01.setInitiative(zombieProfile01.getInitiative());
		zombieProfile01.setAttacks(zombieProfile01.getAttacks());
		zombieProfile01.setLeadership(zombieProfile01.getLeadership());

		zombieProfile02.setMovement(zombieProfile02.getMovement());
		zombieProfile02.setWeaponSkill(zombieProfile02.getWeaponSkill());
		zombieProfile02.setBallisticSkill(zombieProfile02.getBallisticSkill());
		zombieProfile02.setStrength(zombieProfile02.getStrength());
		zombieProfile02.setToughness(zombieProfile02.getToughness());
		zombieProfile02.setWounds(zombieProfile02.getWounds());
		zombieProfile02.setInitiative(zombieProfile02.getInitiative());
		zombieProfile02.setAttacks(zombieProfile02.getAttacks());
		zombieProfile02.setLeadership(zombieProfile02.getLeadership());

		zombieProfile03.setMovement(zombieProfile03.getMovement());
		zombieProfile03.setWeaponSkill(zombieProfile03.getWeaponSkill());
		zombieProfile03.setBallisticSkill(zombieProfile03.getBallisticSkill());
		zombieProfile03.setStrength(zombieProfile03.getStrength());
		zombieProfile03.setToughness(zombieProfile03.getToughness());
		zombieProfile03.setWounds(zombieProfile03.getWounds());
		zombieProfile03.setInitiative(zombieProfile03.getInitiative());
		zombieProfile03.setAttacks(zombieProfile03.getAttacks());
		zombieProfile03.setLeadership(zombieProfile03.getLeadership());

		zombieProfile04.setMovement(zombieProfile04.getMovement());
		zombieProfile04.setWeaponSkill(zombieProfile04.getWeaponSkill());
		zombieProfile04.setBallisticSkill(zombieProfile04.getBallisticSkill());
		zombieProfile04.setStrength(zombieProfile04.getStrength());
		zombieProfile04.setToughness(zombieProfile04.getToughness());
		zombieProfile04.setWounds(zombieProfile04.getWounds());
		zombieProfile04.setInitiative(zombieProfile04.getInitiative());
		zombieProfile04.setAttacks(zombieProfile04.getAttacks());
		zombieProfile04.setLeadership(zombieProfile04.getLeadership());

		zombie01.setFighterImage(zombieImage01);
		zombie02.setFighterImage(zombieImage02);
		zombie03.setFighterImage(zombieImage03);
		zombie04.setFighterImage(zombieImage04);

		addFighter(zombie01);
		addFighter(zombie02);
		addFighter(zombie03);
		addFighter(zombie04);
	}

	public void generateFiveZombies() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage zombieImage01 = new BasedModelImage(basePath + "ScavvyZombie01.png", 21, 154, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage02 = new BasedModelImage(basePath + "ScavvyZombie02.png", 0, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage03 = new BasedModelImage(basePath + "ScavvyZombie03.png", 0, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage04 = new BasedModelImage(basePath + "ScavvyZombie01.png", 21, 154, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);
		BasedModelImage zombieImage05 = new BasedModelImage(basePath + "ScavvyZombie02.png", 0, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_ZOMBIE);

		Fighter zombie01 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie02 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie03 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie04 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);
		Fighter zombie05 = Fighter.createInstance(Fighter.Type.SCAVVY_ZOMBIE, "Scavvy Zombie", selectedGang);

		FighterProfile zombieProfile01 = zombie01.getProfile();
		FighterProfile zombieProfile02 = zombie02.getProfile();
		FighterProfile zombieProfile03 = zombie03.getProfile();
		FighterProfile zombieProfile04 = zombie04.getProfile();
		FighterProfile zombieProfile05 = zombie05.getProfile();

		zombieProfile01.setMovement(zombieProfile01.getMovement());
		zombieProfile01.setWeaponSkill(zombieProfile01.getWeaponSkill());
		zombieProfile01.setBallisticSkill(zombieProfile01.getBallisticSkill());
		zombieProfile01.setStrength(zombieProfile01.getStrength());
		zombieProfile01.setToughness(zombieProfile01.getToughness());
		zombieProfile01.setWounds(zombieProfile01.getWounds());
		zombieProfile01.setInitiative(zombieProfile01.getInitiative());
		zombieProfile01.setAttacks(zombieProfile01.getAttacks());
		zombieProfile01.setLeadership(zombieProfile01.getLeadership());

		zombieProfile02.setMovement(zombieProfile02.getMovement());
		zombieProfile02.setWeaponSkill(zombieProfile02.getWeaponSkill());
		zombieProfile02.setBallisticSkill(zombieProfile02.getBallisticSkill());
		zombieProfile02.setStrength(zombieProfile02.getStrength());
		zombieProfile02.setToughness(zombieProfile02.getToughness());
		zombieProfile02.setWounds(zombieProfile02.getWounds());
		zombieProfile02.setInitiative(zombieProfile02.getInitiative());
		zombieProfile02.setAttacks(zombieProfile02.getAttacks());
		zombieProfile02.setLeadership(zombieProfile02.getLeadership());

		zombieProfile03.setMovement(zombieProfile03.getMovement());
		zombieProfile03.setWeaponSkill(zombieProfile03.getWeaponSkill());
		zombieProfile03.setBallisticSkill(zombieProfile03.getBallisticSkill());
		zombieProfile03.setStrength(zombieProfile03.getStrength());
		zombieProfile03.setToughness(zombieProfile03.getToughness());
		zombieProfile03.setWounds(zombieProfile03.getWounds());
		zombieProfile03.setInitiative(zombieProfile03.getInitiative());
		zombieProfile03.setAttacks(zombieProfile03.getAttacks());
		zombieProfile03.setLeadership(zombieProfile03.getLeadership());

		zombieProfile04.setMovement(zombieProfile04.getMovement());
		zombieProfile04.setWeaponSkill(zombieProfile04.getWeaponSkill());
		zombieProfile04.setBallisticSkill(zombieProfile04.getBallisticSkill());
		zombieProfile04.setStrength(zombieProfile04.getStrength());
		zombieProfile04.setToughness(zombieProfile04.getToughness());
		zombieProfile04.setWounds(zombieProfile04.getWounds());
		zombieProfile04.setInitiative(zombieProfile04.getInitiative());
		zombieProfile04.setAttacks(zombieProfile04.getAttacks());
		zombieProfile04.setLeadership(zombieProfile04.getLeadership());

		zombieProfile05.setMovement(zombieProfile05.getMovement());
		zombieProfile05.setWeaponSkill(zombieProfile05.getWeaponSkill());
		zombieProfile05.setBallisticSkill(zombieProfile05.getBallisticSkill());
		zombieProfile05.setStrength(zombieProfile05.getStrength());
		zombieProfile05.setToughness(zombieProfile05.getToughness());
		zombieProfile05.setWounds(zombieProfile05.getWounds());
		zombieProfile05.setInitiative(zombieProfile05.getInitiative());
		zombieProfile05.setAttacks(zombieProfile05.getAttacks());
		zombieProfile05.setLeadership(zombieProfile05.getLeadership());

		zombie01.setFighterImage(zombieImage01);
		zombie02.setFighterImage(zombieImage02);
		zombie03.setFighterImage(zombieImage03);
		zombie04.setFighterImage(zombieImage04);
		zombie05.setFighterImage(zombieImage05);

		addFighter(zombie01);
		addFighter(zombie02);
		addFighter(zombie03);
		addFighter(zombie04);
		addFighter(zombie05);
	}

	// ////Scavvy Dogs

	public void generateTwoDogs() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage dogImage01 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);
		BasedModelImage dogImage02 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);

		Fighter dog01 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);
		Fighter dog02 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);

		FighterProfile dogProfile01 = dog01.getProfile();
		FighterProfile dogProfile02 = dog02.getProfile();

		dogProfile01.setMovement(dogProfile01.getMovement());
		dogProfile01.setWeaponSkill(dogProfile01.getWeaponSkill());
		dogProfile01.setBallisticSkill(dogProfile01.getBallisticSkill());
		dogProfile01.setStrength(dogProfile01.getStrength());
		dogProfile01.setToughness(dogProfile01.getToughness());
		dogProfile01.setWounds(dogProfile01.getWounds());
		dogProfile01.setInitiative(dogProfile01.getInitiative());
		dogProfile01.setAttacks(dogProfile01.getAttacks());
		dogProfile01.setLeadership(dogProfile01.getLeadership());

		dogProfile02.setMovement(dogProfile02.getMovement());
		dogProfile02.setWeaponSkill(dogProfile02.getWeaponSkill());
		dogProfile02.setBallisticSkill(dogProfile02.getBallisticSkill());
		dogProfile02.setStrength(dogProfile02.getStrength());
		dogProfile02.setToughness(dogProfile02.getToughness());
		dogProfile02.setWounds(dogProfile02.getWounds());
		dogProfile02.setInitiative(dogProfile02.getInitiative());
		dogProfile02.setAttacks(dogProfile02.getAttacks());
		dogProfile02.setLeadership(dogProfile02.getLeadership());

		dog01.setFighterImage(dogImage01);
		dog02.setFighterImage(dogImage02);

		addFighter(dog01);
		addFighter(dog02);
	}

	public void generateThreeDogs() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage dogImage01 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);
		BasedModelImage dogImage02 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);
		BasedModelImage dogImage03 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);

		Fighter dog01 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);
		Fighter dog02 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);
		Fighter dog03 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);

		FighterProfile dogProfile01 = dog01.getProfile();
		FighterProfile dogProfile02 = dog02.getProfile();
		FighterProfile dogProfile03 = dog03.getProfile();

		dogProfile01.setMovement(dogProfile01.getMovement());
		dogProfile01.setWeaponSkill(dogProfile01.getWeaponSkill());
		dogProfile01.setBallisticSkill(dogProfile01.getBallisticSkill());
		dogProfile01.setStrength(dogProfile01.getStrength());
		dogProfile01.setToughness(dogProfile01.getToughness());
		dogProfile01.setWounds(dogProfile01.getWounds());
		dogProfile01.setInitiative(dogProfile01.getInitiative());
		dogProfile01.setAttacks(dogProfile01.getAttacks());
		dogProfile01.setLeadership(dogProfile01.getLeadership());

		dogProfile02.setMovement(dogProfile02.getMovement());
		dogProfile02.setWeaponSkill(dogProfile02.getWeaponSkill());
		dogProfile02.setBallisticSkill(dogProfile02.getBallisticSkill());
		dogProfile02.setStrength(dogProfile02.getStrength());
		dogProfile02.setToughness(dogProfile02.getToughness());
		dogProfile02.setWounds(dogProfile02.getWounds());
		dogProfile02.setInitiative(dogProfile02.getInitiative());
		dogProfile02.setAttacks(dogProfile02.getAttacks());
		dogProfile02.setLeadership(dogProfile02.getLeadership());

		dogProfile03.setMovement(dogProfile03.getMovement());
		dogProfile03.setWeaponSkill(dogProfile03.getWeaponSkill());
		dogProfile03.setBallisticSkill(dogProfile03.getBallisticSkill());
		dogProfile03.setStrength(dogProfile03.getStrength());
		dogProfile03.setToughness(dogProfile03.getToughness());
		dogProfile03.setWounds(dogProfile03.getWounds());
		dogProfile03.setInitiative(dogProfile03.getInitiative());
		dogProfile03.setAttacks(dogProfile03.getAttacks());
		dogProfile03.setLeadership(dogProfile03.getLeadership());

		dog01.setFighterImage(dogImage01);
		dog02.setFighterImage(dogImage02);
		dog03.setFighterImage(dogImage03);

		addFighter(dog01);
		addFighter(dog02);
		addFighter(dog03);
	}

	public void generateFourDogs() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage dogImage01 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);
		BasedModelImage dogImage02 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);
		BasedModelImage dogImage03 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);
		BasedModelImage dogImage04 = new BasedModelImage(basePath + "ScavvyDog01.png", 0, 192, House.SCAVVIES,
				Fighter.Type.SCAVVY_DOG);

		Fighter dog01 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);
		Fighter dog02 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);
		Fighter dog03 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);
		Fighter dog04 = Fighter.createInstance(Fighter.Type.SCAVVY_DOG, "Scavvy Dog", selectedGang);

		FighterProfile dogProfile01 = dog01.getProfile();
		FighterProfile dogProfile02 = dog02.getProfile();
		FighterProfile dogProfile03 = dog03.getProfile();
		FighterProfile dogProfile04 = dog04.getProfile();

		dogProfile01.setMovement(dogProfile01.getMovement());
		dogProfile01.setWeaponSkill(dogProfile01.getWeaponSkill());
		dogProfile01.setBallisticSkill(dogProfile01.getBallisticSkill());
		dogProfile01.setStrength(dogProfile01.getStrength());
		dogProfile01.setToughness(dogProfile01.getToughness());
		dogProfile01.setWounds(dogProfile01.getWounds());
		dogProfile01.setInitiative(dogProfile01.getInitiative());
		dogProfile01.setAttacks(dogProfile01.getAttacks());
		dogProfile01.setLeadership(dogProfile01.getLeadership());

		dogProfile02.setMovement(dogProfile02.getMovement());
		dogProfile02.setWeaponSkill(dogProfile02.getWeaponSkill());
		dogProfile02.setBallisticSkill(dogProfile02.getBallisticSkill());
		dogProfile02.setStrength(dogProfile02.getStrength());
		dogProfile02.setToughness(dogProfile02.getToughness());
		dogProfile02.setWounds(dogProfile02.getWounds());
		dogProfile02.setInitiative(dogProfile02.getInitiative());
		dogProfile02.setAttacks(dogProfile02.getAttacks());
		dogProfile02.setLeadership(dogProfile02.getLeadership());

		dogProfile03.setMovement(dogProfile03.getMovement());
		dogProfile03.setWeaponSkill(dogProfile03.getWeaponSkill());
		dogProfile03.setBallisticSkill(dogProfile03.getBallisticSkill());
		dogProfile03.setStrength(dogProfile03.getStrength());
		dogProfile03.setToughness(dogProfile03.getToughness());
		dogProfile03.setWounds(dogProfile03.getWounds());
		dogProfile03.setInitiative(dogProfile03.getInitiative());
		dogProfile03.setAttacks(dogProfile03.getAttacks());
		dogProfile03.setLeadership(dogProfile03.getLeadership());

		dogProfile04.setMovement(dogProfile04.getMovement());
		dogProfile04.setWeaponSkill(dogProfile04.getWeaponSkill());
		dogProfile04.setBallisticSkill(dogProfile04.getBallisticSkill());
		dogProfile04.setStrength(dogProfile04.getStrength());
		dogProfile04.setToughness(dogProfile04.getToughness());
		dogProfile04.setWounds(dogProfile04.getWounds());
		dogProfile04.setInitiative(dogProfile04.getInitiative());
		dogProfile04.setAttacks(dogProfile04.getAttacks());
		dogProfile04.setLeadership(dogProfile04.getLeadership());

		dog01.setFighterImage(dogImage01);
		dog02.setFighterImage(dogImage02);
		dog03.setFighterImage(dogImage03);
		dog04.setFighterImage(dogImage04);

		addFighter(dog01);
		addFighter(dog02);
		addFighter(dog03);
		addFighter(dog04);
	}

	// ////Scavvy Ghouls

	public void generateGhoul() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage ghoulImage01 = new BasedModelImage(basePath + "ScavvyGhoul01.png", 17, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_GHOUL);

		Fighter ghoul01 = Fighter.createInstance(Fighter.Type.SCAVVY_GHOUL, "Scavvy Ghoul", selectedGang);

		FighterProfile ghoulProfile01 = ghoul01.getProfile();

		ghoulProfile01.setMovement(ghoulProfile01.getMovement());
		ghoulProfile01.setWeaponSkill(ghoulProfile01.getWeaponSkill());
		ghoulProfile01.setBallisticSkill(ghoulProfile01.getBallisticSkill());
		ghoulProfile01.setStrength(ghoulProfile01.getStrength());
		ghoulProfile01.setToughness(ghoulProfile01.getToughness());
		ghoulProfile01.setWounds(ghoulProfile01.getWounds());
		ghoulProfile01.setInitiative(ghoulProfile01.getInitiative());
		ghoulProfile01.setAttacks(ghoulProfile01.getAttacks());
		ghoulProfile01.setLeadership(ghoulProfile01.getLeadership());

		ghoul01.setFighterImage(ghoulImage01);

		addFighter(ghoul01);
	}

	public void generateTwoGhouls() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage ghoulImage01 = new BasedModelImage(basePath + "ScavvyGhoul01.png", 17, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_GHOUL);
		BasedModelImage ghoulImage02 = new BasedModelImage(basePath + "ScavvyGhoul02.png", 5, 163, House.SCAVVIES,
				Fighter.Type.SCAVVY_GHOUL);

		Fighter ghoul01 = Fighter.createInstance(Fighter.Type.SCAVVY_GHOUL, "Scavvy Ghoul", selectedGang);
		Fighter ghoul02 = Fighter.createInstance(Fighter.Type.SCAVVY_GHOUL, "Scavvy Ghoul", selectedGang);

		FighterProfile ghoulProfile01 = ghoul01.getProfile();
		FighterProfile ghoulProfile02 = ghoul02.getProfile();

		ghoulProfile01.setMovement(ghoulProfile01.getMovement());
		ghoulProfile01.setWeaponSkill(ghoulProfile01.getWeaponSkill());
		ghoulProfile01.setBallisticSkill(ghoulProfile01.getBallisticSkill());
		ghoulProfile01.setStrength(ghoulProfile01.getStrength());
		ghoulProfile01.setToughness(ghoulProfile01.getToughness());
		ghoulProfile01.setWounds(ghoulProfile01.getWounds());
		ghoulProfile01.setInitiative(ghoulProfile01.getInitiative());
		ghoulProfile01.setAttacks(ghoulProfile01.getAttacks());
		ghoulProfile01.setLeadership(ghoulProfile01.getLeadership());

		ghoulProfile02.setMovement(ghoulProfile02.getMovement());
		ghoulProfile02.setWeaponSkill(ghoulProfile02.getWeaponSkill());
		ghoulProfile02.setBallisticSkill(ghoulProfile02.getBallisticSkill());
		ghoulProfile02.setStrength(ghoulProfile02.getStrength());
		ghoulProfile02.setToughness(ghoulProfile02.getToughness());
		ghoulProfile02.setWounds(ghoulProfile02.getWounds());
		ghoulProfile02.setInitiative(ghoulProfile02.getInitiative());
		ghoulProfile02.setAttacks(ghoulProfile02.getAttacks());
		ghoulProfile02.setLeadership(ghoulProfile02.getLeadership());

		ghoul01.setFighterImage(ghoulImage01);
		ghoul02.setFighterImage(ghoulImage02);

		addFighter(ghoul01);
		addFighter(ghoul02);
	}

	public void generateThreeGhouls() {

		String basePath = "/Images/Textures/Fighters/";

		Gang selectedGang = (Gang) gangList.getSelectedValue();

		BasedModelImage ghoulImage01 = new BasedModelImage(basePath + "ScavvyGhoul01.png", 17, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_GHOUL);
		BasedModelImage ghoulImage02 = new BasedModelImage(basePath + "ScavvyGhoul02.png", 5, 163, House.SCAVVIES,
				Fighter.Type.SCAVVY_GHOUL);
		BasedModelImage ghoulImage03 = new BasedModelImage(basePath + "ScavvyGhoul03.png", 0, 160, House.SCAVVIES,
				Fighter.Type.SCAVVY_GHOUL);

		Fighter ghoul01 = Fighter.createInstance(Fighter.Type.SCAVVY_GHOUL, "Scavvy Ghoul", selectedGang);
		Fighter ghoul02 = Fighter.createInstance(Fighter.Type.SCAVVY_GHOUL, "Scavvy Ghoul", selectedGang);
		Fighter ghoul03 = Fighter.createInstance(Fighter.Type.SCAVVY_GHOUL, "Scavvy Ghoul", selectedGang);

		FighterProfile ghoulProfile01 = ghoul01.getProfile();
		FighterProfile ghoulProfile02 = ghoul02.getProfile();
		FighterProfile ghoulProfile03 = ghoul03.getProfile();

		ghoulProfile01.setMovement(ghoulProfile01.getMovement());
		ghoulProfile01.setWeaponSkill(ghoulProfile01.getWeaponSkill());
		ghoulProfile01.setBallisticSkill(ghoulProfile01.getBallisticSkill());
		ghoulProfile01.setStrength(ghoulProfile01.getStrength());
		ghoulProfile01.setToughness(ghoulProfile01.getToughness());
		ghoulProfile01.setWounds(ghoulProfile01.getWounds());
		ghoulProfile01.setInitiative(ghoulProfile01.getInitiative());
		ghoulProfile01.setAttacks(ghoulProfile01.getAttacks());
		ghoulProfile01.setLeadership(ghoulProfile01.getLeadership());

		ghoulProfile02.setMovement(ghoulProfile02.getMovement());
		ghoulProfile02.setWeaponSkill(ghoulProfile02.getWeaponSkill());
		ghoulProfile02.setBallisticSkill(ghoulProfile02.getBallisticSkill());
		ghoulProfile02.setStrength(ghoulProfile02.getStrength());
		ghoulProfile02.setToughness(ghoulProfile02.getToughness());
		ghoulProfile02.setWounds(ghoulProfile02.getWounds());
		ghoulProfile02.setInitiative(ghoulProfile02.getInitiative());
		ghoulProfile02.setAttacks(ghoulProfile02.getAttacks());
		ghoulProfile02.setLeadership(ghoulProfile02.getLeadership());

		ghoulProfile03.setMovement(ghoulProfile03.getMovement());
		ghoulProfile03.setWeaponSkill(ghoulProfile03.getWeaponSkill());
		ghoulProfile03.setBallisticSkill(ghoulProfile03.getBallisticSkill());
		ghoulProfile03.setStrength(ghoulProfile03.getStrength());
		ghoulProfile03.setToughness(ghoulProfile03.getToughness());
		ghoulProfile03.setWounds(ghoulProfile03.getWounds());
		ghoulProfile03.setInitiative(ghoulProfile03.getInitiative());
		ghoulProfile03.setAttacks(ghoulProfile03.getAttacks());
		ghoulProfile03.setLeadership(ghoulProfile03.getLeadership());

		ghoul01.setFighterImage(ghoulImage01);
		ghoul02.setFighterImage(ghoulImage02);
		ghoul03.setFighterImage(ghoulImage03);

		addFighter(ghoul01);
		addFighter(ghoul02);
		addFighter(ghoul03);
	}

	private class FighterListCellRenderer extends DefaultListCellRenderer {
		private final static int FIGHTER_ICON_SIZE_X = 40;
		private final static int FIGHTER_ICON_SIZE_Y = 40;

		private final static int FIGHTER_LIST_ENTRY_WIDTH = 50;
		private final static int FIGHTER_LIST_ENTRY_HEIGHT = 50;

		/**
		 * 
		 */
		private static final long serialVersionUID = 8983191457622992727L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);

			ListModel model = list.getModel();

			if (model.getSize() > 0) {
				Fighter fighter = (Fighter) model.getElementAt(index);
				BasedModelImage basedModelImage = fighter.getFighterImage();
				ImageIcon imageIcon = new ImageIcon(scaleImage(FIGHTER_ICON_SIZE_X, FIGHTER_ICON_SIZE_Y,
						basedModelImage.getImage()));
				renderer.setIcon(imageIcon);
				renderer.setText(String.format("%s (%s)", fighter.toString(), fighter.getProfile()));

				return renderer;
			}
			else {
				renderer.setPreferredSize(new Dimension(FIGHTER_LIST_ENTRY_WIDTH, FIGHTER_LIST_ENTRY_HEIGHT));

				return renderer;
			}
		}

		private Image scaleImage(int targetX, int targetY, Image image) {
			int imageX = image.getWidth(this);
			int imageY = image.getHeight(this);
			float ratio = 0;

			if (imageX > imageY) {
				ratio = (float) targetX / imageX;
			}
			else {
				ratio = (float) targetY / imageY;
			}

			int finalX = (int) (imageX * ratio);
			int finalY = (int) (imageY * ratio);

			return image.getScaledInstance(finalX, finalY, Image.SCALE_SMOOTH);
		}
	}
}
