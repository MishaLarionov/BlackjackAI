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

	private GraphPanel graph;

	private CustomTextArea[] textBoxes = new CustomTextArea[9];
	// 0 title
	// 1 currCards
	// 2 currAction
	// 3 dealerCards
	// 4 winLossRatio
	// 5 winLossPercent
	// 6 thresholds
	// 7 roundNo
	// 8 coins

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
		textBoxes[0].setText("VINCE-FELIX-IAIN AI");

		textScrollable = new JScrollPane(textInfo);
		this.add(textScrollable);

		graph = new GraphPanel();
		this.add(graph);
	}

	protected void recalcWinLoss(int wins, int losses, int coins) {
		winLossRatio = wins * 1.0 / losses;

		noOfRounds = wins + losses;
		winPercent = wins / noOfRounds;
		lossPercent = losses / noOfRounds;
	}

	protected void redrawMyCards(Hand myHand) {
		textBoxes[1].setText(myHand.toString());
	}

	protected void redrawDealerCard(Card dealerCard) {
		textBoxes[3].setText(dealerCard.toString());
	}

	protected void redrawMyAction(String action) {
		textBoxes[2].setText(action);
	}

	public void updateAtEndOfRound(int wins, int losses, int coins) {
		recalcWinLoss(wins, losses, coins);

		textBoxes[4].setText(winLossRatio + "");
		textBoxes[5].setText("Win Percentage: " + winPercent
				+ "\nLoss Percentage: " + lossPercent);
		textBoxes[7].setText((wins + losses) + "");
		textBoxes[8].setText(coins + "");

		graph.updateWinLoss(winLossRatio);
	}

	public void setThresholds() {
		// Update the thresholds text
		textBoxes[6].setText(ActionSelector.getThresholds());
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
