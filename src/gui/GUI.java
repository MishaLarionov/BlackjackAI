package gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import objects.Hand;
import decisions.AI;

/**
 * Start-up input: IP Address, Port, Thresholds in use
 * 
 * Display during game: Current cards/action, Win/loss ratio, Percent win/loss
 * Percent deviation from observed average, Round number, Coins
 */

public class GUI extends JFrame {
	private AI ai;

	private Hand myHand;
	private MonitorPanel mPanel;
	private static final boolean RUN_AI = false;

	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		super("Vince-Felix-Iain-AI");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(300, 200));
		this.setResizable(false);
		mPanel = new MonitorPanel(ai);
		this.add(mPanel);
		mPanel.setThresholds();
		this.pack();
		this.setVisible(true);

		String ip = JOptionPane.showInputDialog(
				"Please enter the IP of the server", "127.0.0.1");
		while (!ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
			ip = JOptionPane
					.showInputDialog(
							"That doesn't look like a proper IPv4 address",
							"127.0.0.1");
		}

		String port = JOptionPane.showInputDialog(
				"Please enter the port of the server", "1234");
		while (!port.matches("[0-9]*"))
			port = JOptionPane.showInputDialog(
					"That doesn't look like a valid port", "1234");

		if (RUN_AI) {
			ai = new AI(ip, Integer.parseInt(port), this);
			myHand = ai.getDecisionMaker().getMyHand();
		}
	}

	public void updateMyCards() {
		mPanel.redrawMyCards(myHand);
	}
	
	public void updateDealerCard() {
		mPanel.redrawDealerCard(ai.getDecisionMaker().getDealerFaceUp());
	}

	public void updateAction() {
		mPanel.redrawMyAction(ai.getAction());
	}

	public void updateWinLoss() {
		mPanel.updateWinLoss();
	}
}
