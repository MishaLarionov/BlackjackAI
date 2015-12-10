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

public class MonitorPanel extends JPanel {

	private double winLossRatio;
	private double winPercent;
	private double lossPercent;
	private double noOfRounds;

	private JPanel textInfo;
	private JScrollPane textScrollable;

	private NumbersGraph coinG;

	private CustomTextArea[] textBoxes = new CustomTextArea[11];

	private static final int TITLE = 0;
	private static final int CURRCARDS = 1;
	private static final int CURRACTION = 2;
	private static final int DEALERCARDS = 3;
	private static final int WINLOSSRATIO = 4;
	private static final int WINLOSSPERCENT = 5;
	private static final int THRESHOLDS = 6;
	private static final int ROUNDNO = 7;
	private static final int COINS = 8;
	private static final int RESULTDIST = 9;
	private static final int BETAMOUNT = 10;

	private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);

	public MonitorPanel() {
		super();
		this.setBackground(Color.WHITE);
		this.setLayout(new GridLayout(1, 2, 8, 0));

		// Create the text boxes and adds it to the panel
		textInfo = new JPanel();
		BoxLayout layout = new BoxLayout(textInfo, BoxLayout.Y_AXIS);
		textInfo.setLayout(layout);
		for (int i = 0; i < textBoxes.length; i++) {
			textBoxes[i] = new CustomTextArea();
			textInfo.add(textBoxes[i]);
		}
		textBoxes[TITLE].setText("VINCE-FELIX-IAIN AI");

		textScrollable = new JScrollPane(textInfo);
		this.add(textScrollable);

		coinG = new NumbersGraph();
		this.add(coinG);
	}

	protected void recalcWinLoss(int wins, int losses, int coins) {
		if (losses != 0)
			winLossRatio = wins / losses;

		noOfRounds = wins + losses;
		winPercent = Math.round(wins / noOfRounds * 10000) / 100.0;
		lossPercent = Math.round(losses / noOfRounds * 10000) / 100.0;
	}

	protected void redrawMyCards(Hand myHand) {
		textBoxes[CURRCARDS].setText("My hand is: " + myHand.toString());
	}

	protected void redrawDealerCard(Card dealerCard) {
		textBoxes[DEALERCARDS].setText("Dealer's face-up card is: "
				+ dealerCard.toString());
	}

	protected void redrawMyAction(String action) {
		textBoxes[CURRACTION].setText("Planned action is: " + action);
	}

	public void updateAtEndOfRound(int wins, int losses, int coins) {
		recalcWinLoss(wins, losses, coins);

		textBoxes[WINLOSSRATIO].setText("Win/Loss Ratio: " + winLossRatio);
		textBoxes[WINLOSSPERCENT].setText("Win Percentage: " + winPercent
				+ "%\nLoss Percentage: " + lossPercent + "%");
		textBoxes[ROUNDNO].setText("Rounds: " + (wins + losses));
		textBoxes[COINS].setText("Coins: " + coins);

		coinG.updateValues(coins*1.0);
	}

	public void setThresholds() {
		// Update the thresholds text
		textBoxes[THRESHOLDS].setText(ActionSelector.getThresholds());
	}

	public void updateResultsDist(String resultsDist) {
		textBoxes[RESULTDIST].setText(resultsDist);
	}

	public void updateBetAmount(int betAmount) {
		textBoxes[BETAMOUNT].setText("Current bet: " + betAmount);
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
