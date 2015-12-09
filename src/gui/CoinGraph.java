package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

public class CoinGraph extends JPanel {

	private ArrayList<Integer> coinsHistory = new ArrayList<Integer>();
	private double minCoins = Integer.MAX_VALUE;
	private double maxCoins = Integer.MIN_VALUE;

	protected CoinGraph() {
		this.setBackground(Color.BLACK);
	}

	@Override
	public void paintComponent(Graphics g) {
		double deltaXForRound = this.getWidth() / coinsHistory.size();
		double deltaYForCoin = this.getHeight() / (maxCoins - minCoins);

		Point[] points = new Point[coinsHistory.size()];
		for (int i = 0; i < points.length; i++) {
			points[i].setLocation(deltaXForRound * i,
					(coinsHistory.get(i) - minCoins) * deltaYForCoin);
		}

		g.setColor(Color.YELLOW);
		for (int i = 1; i < points.length; i++) {
			g.drawLine((int) (points[i - 1].getX()),
					(int) (points[i - 1].getY()), (int) (points[i].getX()),
					(int) (points[i].getY()));
		}
	}

	protected void updateCoins(int coins) {
		coinsHistory.add(coins);

		if (coins > maxCoins)
			maxCoins = coins;
		if (coins < minCoins)
			minCoins = coins;

		this.repaint();
	}
}
