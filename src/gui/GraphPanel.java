package gui;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

class GraphPanel extends JPanel {

	private ArrayList<Double> winLossHistory = new ArrayList<Double>();
	private double minWinLoss = Integer.MAX_VALUE;
	private double maxWinLoss = Integer.MIN_VALUE;

	protected GraphPanel() {
		// Does nothing, I don't think.
	}

	@Override
	public void paintComponent(Graphics g) {

	}

	protected void updateWinLoss(double winLoss) {
		winLossHistory.add(winLoss);
		
		if (winLoss > maxWinLoss)
			maxWinLoss = winLoss;
		if (winLoss < minWinLoss)
			minWinLoss = winLoss;
		
		this.repaint();
	}
}
