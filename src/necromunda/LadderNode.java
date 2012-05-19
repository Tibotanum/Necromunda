package necromunda;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.JmeSystem;

public class LadderNode extends Node {
	public final static float MAX_LADDER_DISTANCE = 0.5f;
	
	private LadderNode peer;
	
	public LadderNode(String name) {
		super(name);
	}
	
	public static List<LadderNode> createLadders(String filename) {
		InputStream is = JmeSystem.class.getResourceAsStream(filename);
		List<LadderNode> ladders = new ArrayList<LadderNode>();

		if (is != null) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
				
				String line = bufferedReader.readLine();
				
				while (line != null) {
					StringTokenizer tokenizer = new StringTokenizer(line, ",");
					int index = 0;
					float[] coordinates = new float[6];
					
					while (tokenizer.hasMoreTokens()) {
						String token = tokenizer.nextToken();
						coordinates[index] = Float.parseFloat(token);
						index++;
					}
				
					LadderNode ladder1 = new LadderNode("ladder");
					ladder1.setLocalTranslation(coordinates[0], coordinates[1], coordinates[2]);
					LadderNode ladder2 = new LadderNode("ladder");
					ladder2.setLocalTranslation(coordinates[3], coordinates[4], coordinates[5]);
					
					ladder1.setPeer(ladder2);
					ladder2.setPeer(ladder1);
					
					ladders.add(ladder1);
					ladders.add(ladder2);
					
					line = bufferedReader.readLine();
				}
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ladders;
	}
	
	public float distance(Vector3f point) {
		necromunda.Line line = new necromunda.Line(getWorldStart(), getWorldEnd());
		return line.distance(point);
	}
	
	public Vector3f getWorldStart() {
		return getWorldTranslation();
	}
	
	public Vector3f getWorldEnd() {
		return getWorldTranslation().add(Vector3f.UNIT_Y);
	}

	public LadderNode getPeer() {
		return peer;
	}

	public void setPeer(LadderNode peer) {
		this.peer = peer;
	}
}
