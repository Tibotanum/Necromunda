package necromunda;

import java.awt.Image;
import java.io.Serializable;

public class BasedModelImage implements Serializable {
	private House house;
	private Fighter.Type fighterType;
	private String basePath;
	private String imageFileName;
	private int offset;
	private int baseWidth;
	private transient Image image;

	public BasedModelImage(String basePath, String imageFileName, int offset, int baseWidth, House house, Fighter.Type fighterType) {
	    this.basePath = basePath;
		this.imageFileName = imageFileName;
		this.offset = offset;
		this.baseWidth = baseWidth;
		this.house = house;
		this.fighterType = fighterType;
	}

	public int getOffset() {
		return offset;
	}

	public int getBaseWidth() {
		return baseWidth;
	}

	public String getImageFileName() {
        return imageFileName;
    }

    public String getRelativeImageFileName() {
		return basePath + "/" + imageFileName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj instanceof BasedModelImage) {
			BasedModelImage image = (BasedModelImage)obj;
			String fileName = image.getImageFileName();
			
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

	public Fighter.Type getFighterType() {
		return fighterType;
	}
}
