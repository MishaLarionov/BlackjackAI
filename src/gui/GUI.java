package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import objects.Card;
import objects.Hand;
import decisions.AI;

/**
 * Creates a UI to show details and stats of the game
 * 
 * @author Vince, Felix, Iain
 */

@SuppressWarnings("serial")
public class GUI extends JFrame {

	private StatsPanel statsPanel;
	// For debugging
	private static final boolean RUN_AI = true;

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new GUI();
	}

	/**
	 * Creates a GUI window
	 */
	public GUI() {
		super("Vince-Felix-Iain-AI");
		// Sets up the frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(350, 650));
		this.setResizable(true);
		this.setLocation(100, 75);
		ImageIcon icon = new ImageIcon("cardIcon.png");
		this.setIconImage(icon.getImage());

		// Adds things
		statsPanel = new StatsPanel();
		this.add(statsPanel);
		statsPanel.setThresholds();
		this.pack();
		this.setVisible(true);

		// Gets the IP address of server
		String ip = JOptionPane.showInputDialog(
				"Please enter the IP of the server", "127.0.0.1");
		try {
			while (!ip
					.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
				ip = JOptionPane.showInputDialog(
						"That doesn't look like a proper IPv4 address",
						"127.0.0.1");
			}
		} catch (NullPointerException e) {
			System.err.println("Can't find IP");
			return;
		}

		// Gets the port of the server
		String port = JOptionPane.showInputDialog(
				"Please enter the port of the server", "1234");
		while (!port.matches("[0-9]*"))
			port = JOptionPane.showInputDialog(
					"That doesn't look like a valid port", "1234");

		// Runs the AI
		if (RUN_AI) {
			new AI(ip, Integer.parseInt(port), GUI.this);
		}
	}

	/**
	 * Updates my cards in the StatsPanel
	 * 
	 * @param myHand
	 */
	public void updateMyCards(Hand myHand) {
		statsPanel.redrawMyCards(myHand);
	}

	/**
	 * Updates the dealer's cards in the StatsPanel
	 * 
	 * @param dealerFU
	 */
	public void updateDealerCard(Card dealerFU) {
		statsPanel.redrawDealerCard(dealerFU);
	}

	/**
	 * Update my action in the StatsPanel
	 * 
	 * @param action
	 */
	public void updateAction(String action) {
		statsPanel.redrawMyAction(action);
	}

	/**
	 * Update the results distribution (bust/blackjack/under, etc.) in the
	 * StatsPanel
	 * 
	 * @param resultsDist
	 */
	public void updateResultsDist(String resultsDist) {
		statsPanel.updateResultsDist(resultsDist);
	}

	/**
	 * Updates the Wins/Losses/Coins in the StatsPanel
	 * 
	 * @param wins
	 *            current total of wins
	 * @param losses
	 *            current total of losses
	 * @param coins
	 *            current total of coins
	 */
	public void updateWinLoss(int wins, int losses, int coins) {
		statsPanel.updateAtEndOfRound(wins, losses, coins);
	}

	/**
	 * Updates the amount that's being bet in the StatsPanel
	 * 
	 * @param betAmount
	 */
	public void updateBetAmount(int betAmount) {
		statsPanel.updateBetAmount(betAmount);
	}

	/**
	 * Updates the player No in the StatsPanel
	 * 
	 * @param number
	 */
	public void setPlayerNumber(int number) {
		statsPanel.updatePlayerNumber(number);
	}
}
