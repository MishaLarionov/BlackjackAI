package gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import objects.Card;
import objects.Hand;
import decisions.AI;
import decisions.ActionSelector;

public class MonitorPanel extends JPanel {

	private double winLoss;
	private double winPercent;
	private double lossPercent;
	private double noOfRounds;
	
	private JPanel textInfo;
	private JScrollPane textScrollable;

	private AI ai;

	public MonitorPanel(AI ai) {
		super();
		this.ai = ai;
		this.setBackground(Color.WHITE);
		this.setLayout(new GridLayout(1, 2, 8, 0));
		
	}

	protected void recalcWinLoss() {
		winLoss = ai.getWins() / ai.getLosses();

		noOfRounds = ai.getWins() + ai.getLosses();
		winPercent = ai.getWins() / noOfRounds;
		lossPercent = ai.getLosses() / noOfRounds;
	}

	protected void redrawMyCards(Hand myHand) {
		// redraw my cards
	}
	
	protected void redrawDealerCard(Card dealerCard) {
		// redraw the dealer's card
	}

	protected void redrawMyAction(String action) {
		// redraw the actions
	}

	public void updateWinLoss() {
		recalcWinLoss();
		// get coins as well
		// Update win losses graph and update the percentages' text
	}

	public void setThresholds() {
		// Update the thresholds text
		String thresholds = ActionSelector.getThresholds();
	}
}
