package gui;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class TextInfoPanel extends JPanel {

	protected TextInfoPanel() {
		this.setBackground(Color.WHITE);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
}
