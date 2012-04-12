package necromunda;

import java.awt.Image;
import java.io.Serializable;

public class BasedModelImage implements Serializable {
	private House house;
	private Class<? extends Fighter> fighterClass;
	private String imageFileName;
	private int offset;
	private int baseWidth;
	private Image image;

	public BasedModelImage(String imageFileName, int offset, int baseWidth, House house, Class<? extends Fighter> fighterClass) {
		this.imageFileName = imageFileName;
		this.offset = offset;
		this.baseWidth = baseWidth;
		this.house = house;
		this.fighterClass = fighterClass;
	}

	public int getOffset() {
		return offset;
	}

	public int getBaseWidth() {
		return baseWidth;
	}

	public String getRelativeImageFileName() {
		return imageFileName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj instanceof BasedModelImage) {
			BasedModelImage image = (BasedModelImage)obj;
			String fileName = image.getRelativeImageFileName();
			
			if (fileName.equals(imageFileName)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return imageFileName.hashCode();
	}
	
	public Image getImage() {
		if (image == null) {
			image = Utils.loadImage(this);
		}
		
		return image;
	}

	public House getHouse() {
		return house;
	}

	public Class<? extends Fighter> getFighterClass() {
		return fighterClass;
	}
}
