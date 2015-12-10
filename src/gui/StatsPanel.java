package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.Card;
import objects.Hand;
import decisions.ActionSelector;

/**
 * Shows statistics of the current game
 * 
 * @author Vince, Felix, Iain
 *
 */
public class StatsPanel extends JPanel {

	// Numerical info
	private double winLossRatio;
	private double winPercent;
	private double lossPercent;
	private double noOfRounds;

	// Panels
	private JPanel textInfo;
	private JScrollPane textScrollable;
	private NumbersGraph coinG;

	// A list of the text areas that are used to display information.
	private CustomTextArea[] textBoxes = new CustomTextArea[11];
	private static final int TITLE = 0;
	private static final int CURR_CARDS = 1;
	private static final int CURR_ACTION = 2;
	private static final int DEALER_CARDS = 3;
	private static final int WINLOSS_RATIO = 4;
	private static final int WINLOSS_NUMBERS = 5;
	private static final int THRESHOLDS = 6;
	private static final int ROUND_NO = 7;
	private static final int COINS = 8;
	private static final int RESULT_DIST = 9;
	private static final int BET_AMOUNT = 10;

	private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);

	/**
	 * Creates and initializes the panel
	 */
	public StatsPanel() {
		super();
		// Set up
		this.setBackground(Color.WHITE);
		this.setLayout(new GridLayout(2, 1, 0, 8));

		// Create the text boxes and adds it to the panel
		textInfo = new JPanel();
		BoxLayout layout = new BoxLayout(textInfo, BoxLayout.Y_AXIS);
		textInfo.setLayout(layout);
		textInfo.setBackground(Color.WHITE);
		for (int i = 0; i < textBoxes.length; i++) {
			textBoxes[i] = new CustomTextArea();
			textInfo.add(textBoxes[i]);
		}
		textBoxes[TITLE].setText("VINCE-FELIX-IAIN AI");
		textBoxes[TITLE].setFont(new Font("Arial", Font.BOLD, 16));

		// Makes it scrollable
		textScrollable = new JScrollPane(textInfo);
		this.add(textScrollable);

		// Adds the graph
		coinG = new NumbersGraph();
		this.add(coinG);

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				// nothing
			}

			@Override
			public void componentResized(ComponentEvent e) {
				if (StatsPanel.this.getSize().getWidth()
						/ StatsPanel.this.getSize().getHeight() < 1) {
					StatsPanel.this.setLayout(new GridLayout(2, 1, 0, 8));
				} else {
					StatsPanel.this.setLayout(new GridLayout(1, 2, 8, 0));
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// nothing
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// nothing
			}
		});
	}

	/**
	 * Updates the win and loss statistics
	 * 
	 * @param wins
	 *            the current number of wins
	 * @param losses
	 *            the current number of losses
	 * @param coins
	 *            the current number of coins
	 */
	protected void recalcWinLoss(int wins, int losses, int coins) {
		// Avoiding division by 0
		if (losses != 0) {
			winLossRatio = Math.round((double) (wins) / (double) (losses)
					* 1000.0) / 1000.0;
		}

		// Does percentages
		noOfRounds = wins + losses;
		winPercent = Math.round(wins / noOfRounds * 100000) / 1000.0;
		lossPercent = Math.round(losses / noOfRounds * 100000) / 1000.0;
	}

	/**
	 * Refreshes the display for cards
	 * 
	 * @param myHand
	 *            the current hand
	 */
	protected void redrawMyCards(Hand myHand) {
		textBoxes[CURR_CARDS].setText("My hand is: " + myHand.toString());
	}

	/**
	 * Refreshes the display for the dealer's card
	 * 
	 * @param dealerCard
	 *            the dealer's card
	 */
	protected void redrawDealerCard(Card dealerCard) {
		textBoxes[DEALER_CARDS].setText("Dealer's face-up card is: "
				+ dealerCard.toString());
	}

	/**
	 * Refreshes the display for the current action
	 * 
	 * @param action
	 *            A string representing the current action
	 */
	protected void redrawMyAction(String action) {
		textBoxes[CURR_ACTION].setText("Planned action is: " + action);
	}

	/**
	 * Updates information at the end of a round for wins, losses, ratios,
	 * coins, and round number.
	 * 
	 * @param wins
	 *            the number of wins
	 * @param losses
	 *            the number of losses
	 * @param coins
	 *            the number of coins
	 */
	public void updateAtEndOfRound(int wins, int losses, int coins) {
		// Updates the numbers
		recalcWinLoss(wins, losses, coins);

		// Updates the text fields
		textBoxes[WINLOSS_RATIO].setText("Win/Loss Ratio: " + winLossRatio);
		textBoxes[WINLOSS_NUMBERS].setText("Win Percentage: " + winPercent
				+ "%\nLoss Percentage: " + lossPercent + "%\nWins: " + wins
				+ "\nLosses: " + losses);
		textBoxes[ROUND_NO].setText("Rounds: " + (wins + losses));
		textBoxes[COINS].setText("Coins: " + coins);

		// Updates the graph with new value
		coinG.newValue(coins * 1.0);
	}

	/**
	 * Sets the display for the thresholds
	 */
	public void setThresholds() {
		// Update the thresholds text
		textBoxes[THRESHOLDS].setText(ActionSelector.getThresholds());
	}

	/**
	 * Refreshes the display for the results distribution
	 * 
	 * @param resultsDist
	 *            A string describing the current results distribution
	 */
	public void updateResultsDist(String resultsDist) {
		textBoxes[RESULT_DIST].setText(resultsDist);
	}

	/**
	 * Refreshes the display for the bet amount
	 * 
	 * @param betAmount
	 *            the current bet amount
	 */
	public void updateBetAmount(int betAmount) {
		textBoxes[BET_AMOUNT].setText("Current bet: " + betAmount);
	}

	/**
	 * Refreshes the display for the player number (only done at the beginning
	 * of the game)
	 * 
	 * @param number
	 *            the current player number
	 */
	public void updatePlayerNumber(int number) {
		textBoxes[TITLE].setText("VINCE-FELIX-IAIN AI (" + number + ")");
	}

	/**
	 * The custom text area to save lot of configuration
	 * 
	 * @author Vince, Felix, Iain
	 *
	 */
	class CustomTextArea extends JTextArea {
		CustomTextArea() {
			// Initializes and sets up the thing
			super();
			this.setMaximumSize(new Dimension(450, Short.MAX_VALUE));
			this.setFont(CONTENT_FONT);
			this.setWrapStyleWord(true);
			this.setEditable(false);
			this.setLineWrap(true);
			this.setForeground(Color.BLACK);
			this.setOpaque(false);
			this.setBackground(Color.WHITE);
			this.setFocusable(false);
			this.setAlignmentX(LEFT_ALIGNMENT);
		}

		/**
		 * Creates a Custom Text Area with a text
		 * 
		 * @param text
		 *            the text to set it
		 */
		CustomTextArea(String text) {
			new CustomTextArea();
			setText(text);
		}

		/**
		 * Sets the text for the text box
		 * 
		 * @param text
		 *            the new text
		 */
		public void setText(String text) {
			super.setText(text);
			this.revalidate();
			this.repaint();
		}
	}
}
