package necromunda;

public class ScatterDiceRollResult {
	private boolean hit;
	private float angle;
	
	public ScatterDiceRollResult(boolean hit, float angle) {
		this.hit = hit;
		this.angle = angle;
	}

	public boolean isHit() {
		return hit;
	}

	public float getAngle() {
		return angle;
	}
}
