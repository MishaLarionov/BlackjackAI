package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.Card;
import objects.Hand;
import decisions.ActionSelector;

public class StatsPanel extends JPanel {

	private double winLossRatio;
	private double winPercent;
	private double lossPercent;
	private double noOfRounds;

	private JPanel textInfo;
	private JScrollPane textScrollable;

	private NumbersGraph coinG;

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

	public StatsPanel() {
		super();
		this.setBackground(Color.WHITE);
		this.setLayout(new GridLayout(1, 2, 8, 0));

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

		textScrollable = new JScrollPane(textInfo);
		this.add(textScrollable);

		coinG = new NumbersGraph();
		this.add(coinG);
	}

	protected void recalcWinLoss(int wins, int losses, int coins) {
		if (losses != 0) {
			winLossRatio = Math.round((double) (wins) / (double) (losses)
					* 1000.0) / 1000.0;
		}

		noOfRounds = wins + losses;
		winPercent = Math.round(wins / noOfRounds * 100000) / 1000.0;
		lossPercent = Math.round(losses / noOfRounds * 100000) / 1000.0;
	}

	protected void redrawMyCards(Hand myHand) {
		textBoxes[CURR_CARDS].setText("My hand is: " + myHand.toString());
	}

	protected void redrawDealerCard(Card dealerCard) {
		textBoxes[DEALER_CARDS].setText("Dealer's face-up card is: "
				+ dealerCard.toString());
	}

	protected void redrawMyAction(String action) {
		textBoxes[CURR_ACTION].setText("Planned action is: " + action);
	}

	public void updateAtEndOfRound(int wins, int losses, int coins) {
		recalcWinLoss(wins, losses, coins);

		textBoxes[WINLOSS_RATIO].setText("Win/Loss Ratio: " + winLossRatio);
		textBoxes[WINLOSS_NUMBERS].setText("Win Percentage: " + winPercent
				+ "%\nLoss Percentage: " + lossPercent + "%\nWins: " + wins
				+ "\nLosses: " + losses);
		textBoxes[ROUND_NO].setText("Rounds: " + (wins + losses));
		textBoxes[COINS].setText("Coins: " + coins);

		coinG.updateValues(coins * 1.0);
	}

	public void setThresholds() {
		// Update the thresholds text
		textBoxes[THRESHOLDS].setText(ActionSelector.getThresholds());
	}

	public void updateResultsDist(String resultsDist) {
		textBoxes[RESULT_DIST].setText(resultsDist);
	}

	public void updateBetAmount(int betAmount) {
		textBoxes[BET_AMOUNT].setText("Current bet: " + betAmount);
	}

	public void updatePlayerNumber(int number) {
		textBoxes[TITLE].setText("VINCE-FELIX-IAIN AI (" + number + ")");
	}

	class CustomTextArea extends JTextArea {
		CustomTextArea() {
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

		CustomTextArea(String text) {
			new CustomTextArea();
			setText(text);
		}

		public void setText(String text) {
			super.setText(text);
			this.revalidate();
			this.repaint();
		}
	}
}
