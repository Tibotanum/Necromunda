package necromunda;

import java.awt.*;
import java.io.File;
import java.nio.*;
import java.util.*;
import java.util.List;

import javax.swing.ImageIcon;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

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
		int roll = FastMath.nextRandomInt(0, 5) * 2;
		
		return roll;
	}
	
	public static ScatterDiceRollResult rollScatterDice() {
		int hitRoll = FastMath.nextRandomInt(1, 6);
		boolean hit = true;
		
		if (hitRoll > 2) {
			hit = false;
		}
		
		return new ScatterDiceRollResult(hit, getRandomAngle());
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
	
	public static Image loadImage(BasedModelImage basedModelImage) {
		Image image = null;
		File imageFile = new File(basedModelImage.getRelativeImageFileName());
		
		/*try {
			image = ImageIO.read(imageFile);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		image = Toolkit.getDefaultToolkit().getImage(Utils.class.getResource(basedModelImage.getRelativeImageFileName()));
		
		return image;
	}
	
	public static CollisionResult getClosestCollision(Vector3f origin, Vector3f direction, List<Collidable> collidables) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(origin, direction);

		for (Collidable collidable : collidables) {
			collidable.collideWith(ray, results);
		}

		return results.getClosestCollision();
	}
	
	public static boolean intersect(Geometry geometry1, Geometry geometry2) {
		geometry1 = localToWorld(geometry1);
		geometry2 = localToWorld(geometry2);
		
		Mesh mesh1 = geometry1.getMesh();
		Mesh mesh2 = geometry2.getMesh();
		
		List<Vector3f> points1 = points(mesh1);
		List<Vector3f> points2 = points(mesh2);
		
		/*for (Vector3f point : points1) {
			System.out.println(point);
		}*/
		
		/*for (Vector3f point : points2) {
			System.out.println(point);
		}*/
		
		for (int i = 0; i < mesh1.getTriangleCount(); i++) {
			Triangle triangle = new Triangle();
			mesh1.getTriangle(i, triangle);
			triangle.calculateNormal();
			
			if (whichSide(points2, triangle.getNormal(), triangle.get1()) > 0) {
				return false;
			}
		}
		
		for (int i = 0; i < mesh2.getTriangleCount(); i++) {
			Triangle triangle = new Triangle();
			mesh2.getTriangle(i, triangle);
			triangle.calculateNormal();
			
			if (whichSide(points1, triangle.getNormal(), triangle.get1()) > 0) {
				return false;
			}
		}
		
		List<Edge> edges1 = new ArrayList<Edge>(edges(mesh1));
		List<Edge> edges2 = new ArrayList<Edge>(edges(mesh2));
		
		for (Edge edge1 : edges1) {
			for (Edge edge2 : edges2) {
				Vector3f cross = edge1.vector().cross(edge2.vector());
				
				int side0 = whichSide(points1, cross, edge1.getVector0());
					
				if (side0 == 0) {
					continue;
				}
				
				int side1 = whichSide(points2, cross, edge1.getVector0());
				
				if (side1 == 0) {
					continue;
				}
				
				if ((side0 * side1) < 0) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private static List<Vector3f> points(Mesh mesh) {
		List<Vector3f> points = new ArrayList<Vector3f>();
		
		ShortBuffer indexBuffer = mesh.getShortBuffer(Type.Index);
		FloatBuffer positionBuffer = mesh.getFloatBuffer(Type.Position);
		
		while (indexBuffer.hasRemaining()) {
			Vector3f point = new Vector3f();
			
			int index = indexBuffer.get() * 3;
			
			point.x = positionBuffer.get(index);
			point.y = positionBuffer.get(index + 1);
			point.z = positionBuffer.get(index + 2);
			
			points.add(point);
		}
		
		return points;
	}
	
	private static Set<Edge> edges(Mesh mesh) {
		Set<Edge> edges = new HashSet<Edge>();
		
		for (int i = 0; i < mesh.getTriangleCount(); i++) {
			Triangle triangle = new Triangle();
			mesh.getTriangle(i, triangle);
			
			edges.add(new Edge(triangle.get2(), triangle.get1()));
			edges.add(new Edge(triangle.get3(), triangle.get2()));
			edges.add(new Edge(triangle.get1(), triangle.get3()));
		}
		
		return edges;
	}
	
	private static Geometry localToWorld(Geometry geometry) {
		Mesh mesh = geometry.getMesh();
		
		ShortBuffer indexBuffer = mesh.getShortBuffer(Type.Index);
		ShortBuffer newIndexBuffer = BufferUtils.clone(indexBuffer);
		
		newIndexBuffer.rewind();
		
		FloatBuffer positionBuffer = mesh.getFloatBuffer(Type.Position);
		FloatBuffer newPositionBuffer = BufferUtils.clone(positionBuffer);
		
		positionBuffer.rewind();
		newPositionBuffer.rewind();
		
		while (positionBuffer.hasRemaining()) {
			float x = positionBuffer.get();
			float y = positionBuffer.get();
			float z = positionBuffer.get();
			
			Vector3f point = new Vector3f(x, y, z);
			geometry.localToWorld(point, point);
			
			newPositionBuffer.put(point.x);
			newPositionBuffer.put(point.y);
			newPositionBuffer.put(point.z);
		}
		
		newPositionBuffer.rewind();
		
		Mesh newMesh = new Mesh();
		newMesh.setBuffer(Type.Index, 3, newIndexBuffer);
		newMesh.setBuffer(Type.Position, 3, newPositionBuffer);
		
		return new Geometry(geometry.getName(), newMesh);
	}
	
	private static int whichSide(List<Vector3f> points, Vector3f pointA, Vector3f pointB) {
		int positive = 0;
		int negative = 0;
		
		for (Vector3f point : points) {
			float dot = pointA.dot(point.subtract(pointB));
			
			if (dot > 0) {
				positive++;
			}
			else if (dot < 0) {
				negative++;
			}
			
			if ((positive > 0) && (negative > 0)) {
				return 0;
			}
		}
		
		return ((positive > 0) ? 1 : -1);
	}
	
	private static class Edge {
		private Vector3f vector0;
		private Vector3f vector1;
		
		public Edge(Vector3f vector0, Vector3f vector1) {
			this.vector0 = vector0;
			this.vector1 = vector1;
		}

		public Vector3f getVector0() {
			return vector0;
		}

		public Vector3f getVector1() {
			return vector1;
		}
		
		public Vector3f vector() {
			return vector1.subtract(vector0);
		}

		@Override
		public boolean equals(Object o) {
	        if (!(o instanceof Edge)) {
	        	return false;
	        }

	        if (this == o) {
	        	return true;
	        }

	        Edge comp = (Edge) o;
	        
	        if (vector0.equals(comp.getVector0()) && vector1.equals(comp.getVector1())) {
	        	return true;
	        }
	        else {
	        	return false;
	        }
	    }

		@Override
		public int hashCode() {
			return vector0.hashCode() + vector1.hashCode();
		}
	}
}
