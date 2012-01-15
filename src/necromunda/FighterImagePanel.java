package necromunda;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FighterImagePanel extends JPanel implements ChangeListener {
	private SpinnerListModel spinnerModel;
	private Map<BasedModelImage, Image> imageMap = new HashMap<BasedModelImage, Image>();
	
	public FighterImagePanel(SpinnerListModel spinnerModel) {
		this.spinnerModel = spinnerModel;
		
		List<?> basedModelImages = spinnerModel.getList();
		
		for (Object basedModelImage : basedModelImages) {
			addImage((BasedModelImage)basedModelImage);
		}
	}
	
	private void addImage(BasedModelImage basedModelImage) {
		Image image = loadImage(basedModelImage);
		imageMap.put(basedModelImage, image);
	}
	
	private Image loadImage(BasedModelImage basedModelImage) {
		Image image = null;
		File imageFile = new File(basedModelImage.getRelativeImageFileName());
		
		/*try {
			image = ImageIO.read(imageFile);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(basedModelImage.getRelativeImageFileName()));
		
		return image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		BasedModelImage basedModelImage = (BasedModelImage)spinnerModel.getValue();
		Image image = imageMap.get(basedModelImage);
		
		int xPosition = (this.getWidth() / 2) - (image.getWidth(this) / 2);
		int yPosition = (this.getHeight() / 2) - (image.getHeight(this) / 2);
		
		g.drawImage(image, xPosition, yPosition, this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		repaint();
	}

	public Map<BasedModelImage, Image> getImageMap() {
		return imageMap;
	}
}
