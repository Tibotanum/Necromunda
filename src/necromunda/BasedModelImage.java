package necromunda;

import java.io.Serializable;

public class BasedModelImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 744252569745462346L;
	private String imageFileName;
	private int offset;
	private int baseWidth;

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
	
	public String getImageFileName() {
		int index = imageFileName.lastIndexOf('/');
		return imageFileName.substring(index + 1);
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
}
