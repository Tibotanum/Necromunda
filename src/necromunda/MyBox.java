package necromunda;

import java.nio.*;

import com.jme3.math.Vector3f;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;

public class MyBox extends Box {
	public MyBox(float x, float y, float z) {
		super(x, y, z);
	}

	public MyBox(Vector3f center, float x, float y, float z) {
		super(center, x, y, z);
	}

	@Override
	protected void duUpdateGeometryIndices() {
		ShortBuffer sib = BufferUtils.createShortBuffer(36);
        sib.put(new short[] {
        		0, 3, 2,
        		2, 1, 0,
        		1, 2, 6,
        		6, 4, 1,
        		5, 4, 6,
        		6, 7, 5,
        		7, 3, 0,
        		0, 5, 7,
        		6, 2, 3,
        		3, 7, 6,
        		5, 0, 1,
        		1, 4, 5
        });
        setBuffer(Type.Index, 3, sib);
	}

	@Override
	protected void duUpdateGeometryNormals() {
		FloatBuffer fpb = BufferUtils.createVector3Buffer(8);
        fpb.put(new float[] {
        		-1, -1, -1,
        		1, -1, -1,
        		1, 1, -1,
        		-1, 1, -1,
        		1, -1, 1,
        		-1, -1, 1,
        		1, 1, 1,
        		-1, 1, 1
        });
        setBuffer(Type.Normal, 3, fpb);
	}

	@Override
	protected void duUpdateGeometryVertices() {
        FloatBuffer fpb = BufferUtils.createVector3Buffer(8);
        Vector3f[] v = computeVertices();
        fpb.put(new float[] {
                v[0].x, v[0].y, v[0].z,
                v[1].x, v[1].y, v[1].z,
                v[2].x, v[2].y, v[2].z,
                v[3].x, v[3].y, v[3].z,
                v[4].x, v[4].y, v[4].z,
                v[5].x, v[5].y, v[5].z,
                v[6].x, v[6].y, v[6].z,
                v[7].x, v[7].y, v[7].z
        });
        setBuffer(Type.Position, 3, fpb);
        updateBound();
    }
}
