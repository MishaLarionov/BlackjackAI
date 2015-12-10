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
	private int dotRadius = 3;

	/**
	 * Creates a number graph based on a supplied ArrayList
	 * 
	 * @param values
	 *            ArrayList to create a graph.
	 */
	public NumbersGraph(ArrayList<Double> values) {
		this.values = values;
		minVal = Collections.min(values);
		maxVal = Collections.max(values);
		repaint();
	}

	/**
	 * Creates an empty graph
	 */
	public NumbersGraph() {
	}

	/**
	 * Changes the dot radius
	 * 
	 * @param radius
	 *            the new dot radius
	 */
	public void setDotRadius(int radius) {
		this.dotRadius = radius;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Resets things
		this.removeAll();
		this.updateUI();

		// Background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (values.size() < 1)
			return;

		// Gets the amount of pixels per unit
		double xPixPerRound, yPixPerValue;
		xPixPerRound = this.getSize().getWidth() / values.size();
		try {
			yPixPerValue = this.getSize().getHeight() / (maxVal - minVal);
		} catch (ArithmeticException e) {
			return;
		}

		// Calculates each point and stores it in an array
		DoublePoint[] graphPoints = new DoublePoint[values.size()];
		for (int i = 0; i < graphPoints.length; i++) {
			double x = xPixPerRound * i;
			double y = (yPixPerValue * (values.get(i) - minVal)) * -1
					+ this.getHeight();
			graphPoints[i] = new DoublePoint(x, y);
		}

		// Goes through the array and draws the points and their connecting
		// lines
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

		// Draws a net change line
		g.setColor(Color.YELLOW);
		g.drawLine((int) (graphPoints[0].getX()),
				(int) (graphPoints[0].getY()),
				(int) (graphPoints[graphPoints.length - 1].getX()),
				(int) (graphPoints[graphPoints.length - 1].getY()));

		g.setColor(Color.MAGENTA);
		g.drawLine((int) (graphPoints[0].getX()),
				(int) (graphPoints[0].getY()),
				(int) (this.getSize().getWidth()),
				(int) (graphPoints[0].getY()));
	}

	/**
	 * Adds a new value to the graph (at the end)
	 * 
	 * @param newVal
	 *            the new value
	 */
	public void addValue(double newVal) {
		values.add(newVal);

		// Changes the min and max only when this happens, so it avoids having
		// to recalculate every single time
		if (newVal > maxVal) {
			maxVal = newVal;
		}
		if (newVal < minVal) {
			minVal = newVal;
		}

		// Refresh
		this.repaint();
	}

	/**
	 * Used to store a point in double precision
	 * 
	 * @author Vince, Iain, Felix
	 *
	 */
	private class DoublePoint {

		// To store the position
		private double x;
		private double y;

		private DoublePoint() {
			x = 0;
			y = 0;
		}

		/**
		 * Creates a DoublePoint with given x and y values
		 * 
		 * @param x
		 * @param y
		 */
		private DoublePoint(double x, double y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Gets the x
		 * 
		 * @return the X value
		 */
		private double getX() {
			return x;
		}

		/**
		 * Gets the y
		 * 
		 * @return the Y value
		 */
		private double getY() {
			return y;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return x + " " + y;
		}
	}
}
