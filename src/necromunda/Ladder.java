package necromunda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.system.JmeSystem;

public class Ladder {
	private Ladder peer;
	private Node lineNode;
	
	public Ladder(Vector3f origin) {
		lineNode = new Node("lineNode");
		lineNode.setLocalTranslation(origin);
	}
	
	public static List<Ladder> createLaddersFrom(String filename, Material material) {
		InputStream is = JmeSystem.class.getResourceAsStream(filename);
		List<Ladder> ladders = new ArrayList<Ladder>();

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
				
					Ladder ladder1 = new Ladder(new Vector3f(coordinates[0], coordinates[1], coordinates[2]));
					Ladder ladder2 = new Ladder(new Vector3f(coordinates[3], coordinates[4], coordinates[5]));
					
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

	public Ladder getPeer() {
		return peer;
	}

	public void setPeer(Ladder peer) {
		this.peer = peer;
	}
	
	public Vector3f getWorldStart() {
		return lineNode.getWorldTranslation();
	}
	
	public Vector3f getWorldEnd() {
		return lineNode.getWorldTranslation().add(Vector3f.UNIT_Y);
	}
	
	public float distance(Vector3f point) {
		necromunda.Line line = new necromunda.Line(getWorldStart(), getWorldEnd());
		return line.distance(point);
	}

	public Node getLineNode() {
		return lineNode;
	}
}