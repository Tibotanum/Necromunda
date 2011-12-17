package necromunda;

import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

public class Utils {
	public static int rollD6() {
		int roll = FastMath.nextRandomInt(1, 6);
		
		return roll;
	}
	
	public static int rollD(int numberOfSides) {
		int roll = FastMath.nextRandomInt(1, numberOfSides);
		
		return roll;
	}
	
	public static int rollArtilleryDice() {
		Random random = new Random();
		int roll = random.nextInt(6) * 2;
		
		return roll;
	}
	
	public static float rollScatterDice() {
		Random random = new Random();
		int roll = random.nextInt(6);
		
		if (roll > 1) {
			return getRandomAngle(); 
		}
		else {
			return -1;
		}
	}
	
	public static float getRandomAngle() {
		return FastMath.nextRandomFloat() * 2 * FastMath.PI;
	}
	
	public static ImageIcon createImageIcon(String imageFilePath, String description) {
		java.net.URL imageURL = Necromunda.class.getResource(imageFilePath);
		
		if (imageURL != null) {
			return new ImageIcon(imageURL, description);
		}
		else {
			System.err.println("Couldn't find file: " + imageFilePath);
			return null;
		}
	}
	
	public static CollisionResult getNearestCollisionFrom(Vector3f origin, Vector3f direction, List<Collidable> collidables) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(origin, direction.normalize());

		for (Collidable collidable : collidables) {
			collidable.collideWith(ray, results);
		}

		return results.getClosestCollision();
	}
}
