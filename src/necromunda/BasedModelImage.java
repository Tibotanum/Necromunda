package necromunda;

import java.awt.Image;
import java.io.Serializable;

public class BasedModelImage implements Serializable {
	private String imageFileName;
	private int offset;
	private int baseWidth;
	private Image image;

	public BasedModelImage(String imageFileName, int offset, int baseWidth) {
		this.imageFileName = imageFileName;
		this.offset = offset;
		this.baseWidth = baseWidth;
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
}
