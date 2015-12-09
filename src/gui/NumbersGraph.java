package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class NumbersGraph extends JPanel {

	private ArrayList<Double> values = new ArrayList<Double>();
	private double minVal = Double.MAX_VALUE;
	private double maxVal = Double.MIN_VALUE;

	private double horizPadding = 10;
	private double vertPadding = 10;
	private int dotRadius = 6;

	public NumbersGraph(ArrayList<Double> values) {
		this.values = values;
	}

	public NumbersGraph() {
	}

	public void setHorizPadding(double padding) {
		this.horizPadding = padding;
	}

	public void setVertPadding(double padding) {
		this.vertPadding = padding;
	}
	
	public void setDotRadius(int radius){
		this.dotRadius = radius;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (values.size() < 1)
			return;

		double xPixPerRound = this.getSize().getWidth() - 2 * horizPadding;
		double yPixPerValue = this.getSize().getHeight() - 2 * vertPadding;

		DoublePoint[] graphPoints = new DoublePoint[values.size()];
		for (int round = 0; round < graphPoints.length; round++) {
			graphPoints[round] = new DoublePoint(xPixPerRound * round
					+ horizPadding, values.get(round) * yPixPerValue
					+ vertPadding);
		}

		g.setColor(Color.RED);
		g.fillOval((int) (graphPoints[0].getX()),
				(int) (graphPoints[0].getY()), dotRadius, dotRadius);
		for (int i = 1; i < graphPoints.length; i++) {
			g.setColor(Color.GREEN);
			g.drawLine((int) (graphPoints[i - 1].getX()),
					(int) (graphPoints[i - 1].getY()),
					(int) (graphPoints[i].getX()),
					(int) (graphPoints[i].getY()));
			g.setColor(Color.RED);
			g.fillOval((int) (graphPoints[i].getX()),
					(int) (graphPoints[i].getY()), dotRadius, dotRadius);
		}
	}

	public void updateValues(double newVal) {
		values.add(newVal);

		if (newVal > maxVal) {
			maxVal = newVal;
		}
		if (newVal < minVal) {
			minVal = newVal;
		}

		this.repaint();
	}

	private class DoublePoint {

		private double x;
		private double y;

		private DoublePoint() {
			x = 0;
			y = 0;
		}

		private DoublePoint(double x, double y) {
			this.x = x;
			this.y = y;
		}

		private double getX() {
			return x;
		}

		private double getY() {
			return y;
		}
	}
}
