package necromunda;

public class VisibilityInfo {
	private int numberOfVisiblePoints;
	private int numberOfPoints;
	private float visiblePercentage;
	
	public VisibilityInfo(int numberOfVisiblePoints, int numberOfPoints) {
		this.numberOfVisiblePoints = numberOfVisiblePoints;
		this.numberOfPoints = numberOfPoints;
		visiblePercentage = (float)numberOfVisiblePoints / numberOfPoints;
	}
	
	public int getNumberOfVisiblePoints() {
		return numberOfVisiblePoints;
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public float getVisiblePercentage() {
		return visiblePercentage;
	}	
}
