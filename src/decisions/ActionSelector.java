package decisions;

import java.util.ArrayList;

import objects.Card;
import objects.Hand;

/**
 * Chooses the move to make
 * 
 * @author Felix, Iain, Vince
 *
 */
public class ActionSelector {
	// Constants for returning decided action
	public static final int HIT = 0;
	public static final int STAND = 1;
	public static final int DOUBLE = 2;
	// private static final int NO_MOVE = -1;
	private static final int[] CARD_VALUES = new int[] { 0, 1, 2, 3, 4, 5, 6,
			7, 8, 9, 10, 10, 10, 10 };

	// Probability of going under has to be greater than threshold to hit
	private final static double UNDER_THRESH = 0.15;
	// Prob. of going bust must be less than thresh. to hit
	private final static double BUST_THRESH = 0.475;
	// If blackjacking probability is greater than this, it'll play hit
	// regardless
	private final static double PERF_THRESH = 0.8;
	private final static double DOUBLE_THRESH = 0.17;

	// AI reference, cardcounter
	private CardCounter counter;

	// Variables in determining the move
	private ArrayList<Integer> totals;
	private boolean hasAce;

	private Hand myHand;
	// Each index's possible value is 0-24
	private int[] playedCards = new int[13];
	private Card dealerFaceUp;

	private static final boolean DEBUG = false;

	public ActionSelector() {
		counter = new CardCounter();
		myHand = new Hand();
	}

	/**
	 * Decides move based on the total value of the cards, references Felix's
	 * part when probability is relevant
	 */
	public int decideMove(boolean allowedToDouble) {

		// Factors in determining basic action
		totals = myHand.recalcTotals();
		cleanUpTotals();

		if (totalContainsAny(new int[] { 21, 20, 19 }))
			return STAND;

		int smallestTotal = minOfArrayList(totals);
		// The probability of busting at this point is zero, so we don't have to
		// take into account probabilities and such.
		if (smallestTotal <= 11) {
			if (smallestTotal <= 8) {
				return HIT;
			} else if (smallestTotal == 9) {
				if (dealerFaceUp.getValue() >= 3
						&& dealerFaceUp.getValue() <= 6 && allowedToDouble) {
					return DOUBLE;
				} else
					return HIT;
			} else if (smallestTotal == 10) {
				if (dealerFaceUp.getValue() >= 2
						&& dealerFaceUp.getValue() <= 9 && allowedToDouble) {
					return DOUBLE;
				} else
					return HIT;
			} else {
				if (dealerFaceUp.getValue() == 1 && !allowedToDouble)
					return HIT;
				else if (allowedToDouble)
					return DOUBLE;
				else
					return STAND;
			}
		}
		// At this point, the minimum total is 11, and the probability of
		// busting is not zero.
		else {
			double underProb = 0;
			double perfectProb = 0;
			double bustProb = 0;
			double doubleProb = 0;
			for (int i = 0; i < totals.size(); i++) {
				if (totals.get(i) <= 21) {
					double[] temp = counter.calculate(totals.get(i));
					underProb += temp[CardCounter.UNDER];
					perfectProb += temp[CardCounter.PERFECT];
					bustProb += temp[CardCounter.BUST];
					doubleProb += temp[CardCounter.DOUBLE];
				}
			}
			underProb /= totals.size();
			perfectProb /= totals.size();
			bustProb /= totals.size();
			doubleProb /= totals.size();

			if (DEBUG) {
				System.out.println("The perfect prob is: " + perfectProb);
				System.out.println("The bust prob is: " + bustProb);
				System.out.println("The under prob is: " + underProb);
				System.out.println("The double prob is: " + doubleProb);
			}
			if ((doubleProb > DOUBLE_THRESH || perfectProb > PERF_THRESH)
					&& allowedToDouble)
				return DOUBLE;
			else if (underProb > UNDER_THRESH && bustProb < BUST_THRESH)
				return HIT;
			else
				return STAND;
		}
	}

	public void addToMyHand(Card newCard) {
		myHand.add(newCard);
	}

	public void setDealerCard(Card newCard) {
		dealerFaceUp = newCard;
		counter.newCard(newCard);
	}

	protected void cardPlayed(Card playedCard) {
		playedCards[playedCard.getValue() - 1]++;
		counter.newCard(playedCard);
	}

	private void cleanUpTotals() {
		for (int i = 0; i < totals.size(); i++) {
			if (totals.get(i) > 21)
				totals.remove(i);
		}
	}

	public void resetHand() {
		myHand = new Hand();
		dealerFaceUp = null;
	}

	private boolean totalContainsAny(int[] targets) {
		for (int i = 0; i < totals.size(); i++) {
			for (int q : targets) {
				if (totals.get(i) == q) {
					return true;
				}
			}
		}
		return false;
	}

	public void resetCardCounter() {
		counter.resetCounter();
	}

	private int minOfArrayList(ArrayList<Integer> array) {
		int min = Integer.MAX_VALUE;
		for (int i : array) {
			if (i < min)
				min = i;
		}
		return min;
	}

	public Hand getMyHand() {
		return myHand;
	}

	public static String getThresholds() {
		return "BustT = " + BUST_THRESH + " PerfT = " + PERF_THRESH
				+ " UnderT = " + UNDER_THRESH + " DoubleT = " + DOUBLE_THRESH;
	}

	public Card getDealerFaceUp() {
		return dealerFaceUp;
	}
}
