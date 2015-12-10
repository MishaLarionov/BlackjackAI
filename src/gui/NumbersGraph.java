package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

/**
 * Graph of the coins as they dwindle over time
 * 
 * @author Vince, Felix, Iain
 *
 */
public class NumbersGraph extends JPanel {

	// Values for the graph and drawing
	private ArrayList<Double> values = new ArrayList<Double>();
	private double minVal = Double.MAX_VALUE;
	private double maxVal = Double.MIN_VALUE;

	// Radius of the dots
	private int dotRadius = 5;

	/**
	 * Creates a number graph based on a supplied ArrayList
	 * @param values ArrayList to create a graph.
	 */
	public NumbersGraph(ArrayList<Double> values) {
		this.values = values;
		repaint();
	}

	public NumbersGraph() {
	}

	public void setDotRadius(int radius) {
		this.dotRadius = radius;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.removeAll();
		this.updateUI();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (values.size() < 1)
			return;

		double xPixPerRound, yPixPerValue;
		xPixPerRound = this.getSize().getWidth() / values.size();
		try {
			yPixPerValue = this.getSize().getHeight() / (maxVal - minVal);
		} catch (ArithmeticException e) {
			return;
		}

		DoublePoint[] graphPoints = new DoublePoint[values.size()];
		for (int i = 0; i < graphPoints.length; i++) {
			double x = xPixPerRound * i;
			double y = (yPixPerValue * (values.get(i) - minVal)) * -1
					+ this.getHeight();
			graphPoints[i] = new DoublePoint(x, y);
		}

		g.setColor(Color.RED);
		g.fillOval((int) (Math.round(graphPoints[0].getX())),
				(int) (Math.round(graphPoints[0].getY())), dotRadius, dotRadius);
		for (int i = 1; i < graphPoints.length; i++) {
			g.setColor(Color.GREEN);
			g.drawLine((int) (graphPoints[i - 1].getX()),
					(int) (graphPoints[i - 1].getY()),
					(int) (graphPoints[i].getX()),
					(int) (graphPoints[i].getY()));
			g.setColor(Color.RED);
			g.fillOval((int) (Math.round(graphPoints[i].getX())),
					(int) (Math.round(graphPoints[i].getY())), dotRadius,
					dotRadius);
		}
		g.setColor(Color.YELLOW);
		g.drawLine((int) (graphPoints[0].getX()),
				(int) (graphPoints[0].getY()),
				(int) (graphPoints[graphPoints.length - 1].getX()),
				(int) (graphPoints[graphPoints.length - 1].getY()));
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

		public String toString() {
			return x + " " + y;
		}
	}
}
