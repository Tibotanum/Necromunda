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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jme3.system.JmeContext.Type;

import necromunda.Gang.House;
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
	
	private JList gangList;
	private JList fighterList;
	
	private FighterImagePanel fighterImagePanel;
	private JSpinner fighterImageSpinner;
	
	private List<BasedModelImage> fighterImages;
	
	public GangGenerationPanel(final Necromunda game) {
		mainPanel = new JPanel();
		optionsPanel = new JPanel();

		setLayout(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		
		String basePath = "/Images/Textures/Fighters/";
		
		fighterImages = new ArrayList<BasedModelImage>();
		
		fighterImages.add(new BasedModelImage(basePath + "EscherBoss01.png", 33, 134));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger01.png", 9, 130));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger02.png", 2, 127));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger03.png", 17, 128));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger04.png", 0, 129));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger05.png", 0, 113));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger06.png", 32, 115));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger07.png", 0, 116));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger08.png", 30, 114));
		fighterImages.add(new BasedModelImage(basePath + "EscherGanger09.png", 18, 115));
		fighterImages.add(new BasedModelImage(basePath + "EscherHeavy01.png", 0, 129));
		fighterImages.add(new BasedModelImage(basePath + "EscherKid01.png", 0, 128));
		fighterImages.add(new BasedModelImage(basePath + "EscherKid02.png", 9, 131));
		
		fighterImages.add(new BasedModelImage(basePath + "DelaqueBoss01.png", 2, 121));
		fighterImages.add(new BasedModelImage(basePath + "/DelaqueGanger01.png", 1, 125));
		fighterImages.add(new BasedModelImage(basePath + "DelaqueGanger02.png", 1, 129));
		fighterImages.add(new BasedModelImage(basePath + "DelaqueGanger03.png", 1, 123));
		fighterImages.add(new BasedModelImage(basePath + "DelaqueGanger04.png", 1, 123));
		fighterImages.add(new BasedModelImage(basePath + "DelaqueHeavy01.png", 1, 125));
		fighterImages.add(new BasedModelImage(basePath + "DelaqueHeavy02.png", 21, 114));
		fighterImages.add(new BasedModelImage(basePath + "DelaqueKid01.png", 34, 124));
		fighterImages.add(new BasedModelImage(basePath + "DelaqueKid02.png", 18, 122));
		
		gangNameLabel = new JLabel("Gang Name");
		gangNameTextField = new JTextField();
		
		houseLabel = new JLabel("Gang House");
		houseComboBox = new JComboBox(Gang.House.class.getEnumConstants());
		
		addGangButton = new JButton("Add Gang");
		addGangButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel model = (DefaultListModel)gangList.getModel();
				
				House house = (Gang.House)houseComboBox.getSelectedItem();
				
				Gang newGang = new Gang(gangNameTextField.getText(), house);
				
				model.addElement(newGang);
			}
		});
		
		saveGangButton = new JButton("Save Gang");
		saveGangButton.setEnabled(false);
		saveGangButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
			    int returnVal = fileChooser.showOpenDialog(getParent());
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
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

		SpinnerListModel spinnerListModel = new SpinnerListModel(fighterImages);
		fighterImageSpinner = new JSpinner(spinnerListModel);
		fighterImagePanel = new FighterImagePanel(spinnerListModel);
		fighterImageSpinner.setPreferredSize(new Dimension(150, 150));
		fighterImageSpinner.setEditor(fighterImagePanel);
		fighterImageSpinner.addChangeListener(fighterImagePanel);
		
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
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Fighter.Type fighterType = (Fighter.Type)fighterTypeComboBox.getSelectedItem();
				Class<? extends Fighter> fighterClass = fighterType.getAssociatedClass();
				
				try {
					Method method = fighterClass.getMethod("getTemplateProfile", new Class<?>[0]);
					FighterProfile profile = (FighterProfile)method.invoke(fighterClass.getClass(), new Object[0]);
					
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
				catch (SecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		fighterTypeComboBox.setSelectedIndex(0);
		
		weaponComboBox = new JComboBox();
		weaponComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)weaponComboBox.getMinimumSize().getHeight()));
		
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
		weaponComboBox.addItem(new GrenadeLauncher());
		weaponComboBox.addItem(new MeltaGun());
		weaponComboBox.addItem(new PlasmaGun());
		weaponComboBox.addItem(new Autocannon());
		weaponComboBox.addItem(new HeavyBolter());
		weaponComboBox.addItem(new HeavyPlasmaGun());
		weaponComboBox.addItem(new HeavyStubber());
		weaponComboBox.addItem(new Lascannon());
		weaponComboBox.addItem(new MissileLauncher());
		weaponComboBox.addItem(new FragGrenades());
		weaponComboBox.addItem(new KrakGrenades());
		weaponComboBox.addItem(new PlasmaGrenades());
		weaponComboBox.addItemListener(this);
		
		weaponProfileStringLabel = new JLabel();
		updateWeaponString();
		
		fighterWeaponList = new JList(new DefaultListModel());
		fighterWeaponList.setPrototypeCellValue("MMMMMMMMMMMMMMMMMMMMMM");
		fighterWeaponList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
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
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fighterList.getSelectedValue() != null) {
					Fighter selectedFighter = (Fighter)fighterList.getSelectedValue();
					
					Weapon weapon = null;
					
					try {
						weapon = (Weapon)((Weapon)weaponComboBox.getSelectedItem()).getClass().newInstance();
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
					
					((DefaultListModel)fighterWeaponList.getModel()).addElement(weapon);
				}
			}
		});
		
		removeWeaponButton = new JButton("Remove Weapon");
		removeWeaponButton.setEnabled(false);
		removeWeaponButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Fighter selectedFighter = (Fighter)fighterList.getSelectedValue();
				Object[] values = fighterWeaponList.getSelectedValues();
				DefaultListModel model = (DefaultListModel)fighterWeaponList.getModel();
				
				for (Object o : values) {
					model.removeElement(o);
				}

				selectedFighter.removeAllWeapons();
				
				for (int i = 0; i < model.getSize(); i++) {
					Weapon weapon = (Weapon)model.getElementAt(i);
					selectedFighter.addWeapon(weapon);
				}
				
				updateGangRating();
			}
		});
		
		gangList = new JList(new DefaultListModel());
		gangList.setPrototypeCellValue("MMMMMMMMMMMMMMMMMMMMMM");
		gangList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Gang selectedGang = (Gang)gangList.getSelectedValue();
				
				if (selectedGang != null) {
					DefaultListModel model = (DefaultListModel)fighterList.getModel();
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
					else{
						saveGangButton.setEnabled(false);
					}
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
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (fighterList.getSelectedValue() != null) {
					Fighter selectedFighter = (Fighter)fighterList.getSelectedValue();
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
					
					DefaultListModel model = (DefaultListModel)fighterWeaponList.getModel();
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
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] values = gangList.getSelectedValues();
				DefaultListModel model = (DefaultListModel)gangList.getModel();
				
				for (Object o : values) {
					model.removeElement(o);
				}
				
				((DefaultListModel)fighterList.getModel()).clear();
				
				okButton.getAction().setEnabled(getNumberOfFighters() > 0);
			}
		});
		
		loadGangButton = new JButton("Load Gang");
		loadGangButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
			    int returnVal = fileChooser.showOpenDialog(getParent());
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	File selectedFile = fileChooser.getSelectedFile();
			    	
			    	FileInputStream fis = null;
			    	ObjectInputStream ois = null;
			    	
					try {
						fis = new FileInputStream(selectedFile);
				        ois = new ObjectInputStream(fis);
				        
				        DefaultListModel model = (DefaultListModel)gangList.getModel();
						
				        Gang gang = (Gang)ois.readObject();
				        
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
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gangList.getSelectedValue() != null) {
					Gang selectedGang = (Gang)gangList.getSelectedValue();
					Fighter.Type fighterType = (Fighter.Type)fighterTypeComboBox.getSelectedItem();
					
					Fighter fighter = Fighter.getInstance(fighterType, fighterNameTextField.getText(), selectedGang);

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
					
					BasedModelImage image = (BasedModelImage)fighterImageSpinner.getModel().getValue();
					fighter.setFighterImage(image);
					
					selectedGang.addFighter(fighter);
					updateGangRating();
					
					((DefaultListModel)fighterList.getModel()).addElement(fighter);
					
					okButton.getAction().setEnabled(true);
					saveGangButton.setEnabled(true);
				}
			}
		});
		
		removeFighterButton = new JButton("Remove Fighter");
		removeFighterButton.setEnabled(false);
		removeFighterButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Gang selectedGang = (Gang)gangList.getSelectedValue(); 
				Object[] values = fighterList.getSelectedValues();
				DefaultListModel model = (DefaultListModel)fighterList.getModel();
				
				for (Object o : values) {
					model.removeElement(o);
					
					if (o instanceof Fighter) {
						selectedGang.removeFighter((Fighter)o);
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
	
	private int getNumberOfFighters() {
		int number = 0;
		
		DefaultListModel gangModel = (DefaultListModel)gangList.getModel();
		
		for (Object o : gangModel.toArray()) {
			Gang gang = (Gang)o;
			
			number += gang.getGangMembers().size();
		}
		
		return number;
	}
	
	private void setDefaultSize(JComponent component) {
		component.setPreferredSize(new Dimension(20, 30));
		component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
	}
	
	/*private void updateWeaponProfileLabel() {
		Weapon selectedWeapon = (Weapon)weaponComboBox.getSelectedItem();
		
		if (selectedWeapon != null) {
			weaponProfileStringLabel.setText(selectedWeapon.getProfileString());
		}
	}*/
	
	private void updateGangRating() {
		Gang selectedGang = (Gang)gangList.getSelectedValue();
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

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultListModel model = (DefaultListModel)gangList.getModel();
			Enumeration<?> gangs = model.elements();
			game.commitGeneratedGangs(gangs);
						
			final Necromunda3dProvider necromunda3dProvider = new Necromunda3dProvider(game);
			necromunda3dProvider.setInvertMouse(invertMouseCheckBox.isSelected());
			String materialIdentifier = game.getTerrainTextureMap().get(terrainTypeComboBox.getSelectedItem());
			necromunda3dProvider.setTerrainType(materialIdentifier);

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					necromunda3dProvider.start();
				}
				
			});
			
			thread.start();
			
			((JFrame)getTopLevelAncestor()).dispose();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		ItemSelectable itemSelectable = e.getItemSelectable();
		
		if ((itemSelectable == weaponComboBox) && (e.getStateChange() == ItemEvent.SELECTED)) {
			updateWeaponString();
		}
	}
	
	private void updateWeaponString() {
		Weapon selectedWeapon = (Weapon)weaponComboBox.getSelectedItem();
		weaponProfileStringLabel.setText(selectedWeapon.getProfileString());
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

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			DefaultListCellRenderer renderer = (DefaultListCellRenderer)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			ListModel model = list.getModel();
			
			if (model.getSize() > 0) {
				Fighter fighter = (Fighter)model.getElementAt(index);
				BasedModelImage image = fighter.getFighterImage();
				ImageIcon imageIcon = new ImageIcon(scaleImage(FIGHTER_ICON_SIZE_X, FIGHTER_ICON_SIZE_Y, fighterImagePanel.getImageMap().get(image)));
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
				ratio = (float)targetX / imageX;
			}
			else {
				ratio = (float)targetY / imageY;
			}
			
			int finalX = (int)(imageX * ratio);
			int finalY = (int)(imageY * ratio);
			
			return image.getScaledInstance(finalX, finalY, Image.SCALE_SMOOTH);
		}
	}
}
