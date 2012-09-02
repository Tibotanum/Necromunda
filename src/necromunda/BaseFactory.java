package necromunda;

import java.nio.FloatBuffer;
import java.util.Arrays;

import necromunda.MaterialFactory.MaterialIdentifier;

import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.shape.Cylinder;

public class BaseFactory {
	public static final float BASE_HEIGHT = 17f / 132f;
	public static final float TOP_BOTTOM_RADIUS_DIFFERENCE = 10f / 132f / 2f;
	
	private MaterialFactory materialFactory;
	
	public BaseFactory(MaterialFactory materialFactory) {
		this.materialFactory = materialFactory;
	}
	
	public Geometry createRoundBase(float radius, ColorRGBA colour, String textureFileName) {
		int radialSamples = 20;
		float upperBaseRadius = calculateUpperBaseRadius(radius);

		Cylinder cylinder = new Cylinder(4, radialSamples, radius, upperBaseRadius, BASE_HEIGHT, true, false);

		FloatBuffer texBuffer = cylinder.getFloatBuffer(VertexBuffer.Type.TexCoord);
		texBuffer.rewind();

		float[] array = new float[texBuffer.capacity()];
		Arrays.fill(array, -10f);
		texBuffer.put(array);
		texBuffer.position(texBuffer.capacity() - 4);
		texBuffer.put(0.0f + 0.5f).put(0.0f + 0.5f);

		FloatBuffer posBuffer = cylinder.getFloatBuffer(VertexBuffer.Type.Position);

		posBuffer.rewind();
		texBuffer.rewind();

		for (int i = 0; i < radialSamples + 1; i++) {
			texBuffer.put(posBuffer.get() + 0.5f).put(posBuffer.get() + 0.5f);
			posBuffer.get();
		}
		
		Geometry roundBase = new Geometry("base", cylinder);
		roundBase.setMaterial(materialFactory.createTextureMaterial("Images/Textures/Base/BaseGrass.png", new ColorRGBA(64 / 255f, 147 / 255f, 91 / 255f, 1.0f)));
		roundBase.rotate(FastMath.HALF_PI, 0, 0);
		roundBase.move(0, BaseFactory.BASE_HEIGHT / 2, 0);

		return roundBase;
	}
	
	public float calculateUpperBaseRadius(float radius) {
		return radius - TOP_BOTTOM_RADIUS_DIFFERENCE;
	}
}
