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
	
	public FighterImagePanel(SpinnerListModel spinnerModel) {
		this.spinnerModel = spinnerModel;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Image image = ((BasedModelImage)spinnerModel.getValue()).getImage();
		
		int xPosition = (this.getWidth() / 2) - (image.getWidth(this) / 2);
		int yPosition = (this.getHeight() / 2) - (image.getHeight(this) / 2);
		
		g.drawImage(image, xPosition, yPosition, this);
	}

	public void stateChanged(ChangeEvent e) {
		repaint();
	}
}
