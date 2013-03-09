Introduction
------------
This game is a partial implementation of the tabletop game Necromunda created by Games Workshop. The parts currently implemented are movement and shooting. Close-combat, recovery and campaign related stuff is missing.

Start
-----
To start the game simply double-click on the provided JAR file.

You must have at least the Java 6 JRE installed.

Controls
--------
The game can be controlled as follows:

Left mouse button:      Select model, end movement, shoot, place building
Right mouse button:     Abort movement, hold when placing a building to turn it
W, A, S, D:             Move camera
K:                      Skip building during building deployment, skip fighter during fighter deployment
B:                      Attempt to break a web pistol web
M:                      Start regular movement
R:                      Start run movement
C:                      Climb a ladder (during regular or run movement; press repeatedly to cycle through ladders if more than one is in reach)
I:                      Hide
Y:                      Cycle weapons, "Yes"
O:                      Cycle weapon mode
P:                      Cycle number of sustained fire dice
H:                      Start shooting
N:                      Cycle to next phase, "No"
E:                      End current turn, end building deployment

Note: When climbing you first have to start a regular or run movement. If you are standing close enough to a ladder you can press 'C'. After this you have to stop the movement first by clicking the left mouse button. If you have not yet moved all of the allowed distance you can continue moving or running by pressing 'M' or 'R' again.

Copyright notice
----------------
This is an unofficial implementation of Necromunda. The copyright of Necromunda and all its related artwork including the ganger pictures contained in this software is owned by Games Workshop Limited and is used without permission.

The BSD License for the included jMonkeyEngine libraries is held in "jMonkeyBSDLicense.txt".

Eclipse Setup
-------------
First you have to download and install the newest version of the jME3 SDK from "http://jmonkeyengine.org/downloads".
Next you have to download the Quickhull 3D library from "http://www.cs.ubc.ca/~lloyd/java/quickhull3d.html". Choose the JAR file download.

Clone the project from "https://github.com/Tibotanum/Necromunda.git".

Start Eclipse.
Select "File - New - Java Project".
Deselect "Use default location".
Click "Browse".
Select the folder of the cloned project.
Select "Use default location".
Click "Next".
Select the "Source" tab.
Select "assets".
Click "Add folder 'assets' to build path".
Click "Finish".
Right-click on the project in the Package Explorer.
Select "Properties".
Select the "Libraries" tab.
Select "Add library...".
Select "User Library".
Click "Next".
Select "User Libraries...".
Select "New...".
Enter "jME3" as the name.
Click "OK".
Select the new library.
Click "Add External JARs...".
Navigate to your jME installation folder.
Navigate to the "lib" folder.
Select all JARs within this folder.
Click "Open".
Click "OK".
Tick the newly created user library.
Click "Finish".
Click "OK".

Put the "quickhull3d.jar" into the "lib" folder of the project.
Right-click on the project in the Package Explorer.
Select "Properties".
Select the "Libraries" tab.
Select "Add JARs...".
Navigate to the "lib" folder of the project.
Select "quickhull3d.jar".
Click "OK".
Click "OK".

The project is set up now. The main class is in "Necromunda.java". Just open this class and select "Run".

Contributors
------------
Tibotanum (source code, building models)
Skurcey (building models)
Wobbles909 (source code, fighter models)