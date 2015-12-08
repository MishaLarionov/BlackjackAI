import java.util.ArrayList;
import java.util.Collections;

/**
 * Chooses the move to make
 * 
 * @author Felix, Iain, Vince
 *
 */
public class ActionSelector {
	// Constants for returning decided action
	protected static final int HIT = 0;
	protected static final int STAND = 1;
	protected static final int DOUBLE = 2;
	protected static final int NO_MOVE = -1;
	protected static final int[] CARD_VALUES = new int[] { 0, 1, 2, 3, 4, 5, 6,
			7, 8, 9, 10, 10, 10, 10 };

	// Probability of going under has to be greater than threshold to hit
	static double UNDER_THRESH = 0.175;
	// Prob. of going bust must be less than thresh. to hit
	static double BUST_THRESH = 0.475;
	// If blackjacking probability is greater than this, it'll play hit
	// regardless
	static double PERF_THRESH = 0.75;

	// AI reference, cardcounter
	private CardCounter counter;

	// Variables in determining the move
	protected ArrayList<Integer> totals;
	protected boolean hasAce;

	protected Hand myHand;
	// Each index's possible value is 0-24
	protected int[] playedCards = new int[13];
	private Card dealerFaceUp;

	protected ActionSelector() {
		counter = new CardCounter();
		myHand = new Hand();
	}

	/**
	 * Decides move based on the total value of the cards, references Felix's
	 * part when probability is relevant
	 */
	public int decideMove(boolean firstMove) {

		// Factors in determining basic action
		totals = myHand.recalcTotals();
		cleanUpTotals();

		if (totalContainsAny(new int[] { 21, 20, 19, 18 }))
			return STAND;

		int smallestTotal = minOfArrayList(totals);
		// The probability of busting at this point is zero, so we don't have to
		// take into account probabilities and such.
		if (smallestTotal <= 11) {
			if (smallestTotal <= 8) {
				return HIT;
			} else if (smallestTotal == 9) {
				if (dealerFaceUp.getValue() >= 3
						&& dealerFaceUp.getValue() <= 6 && firstMove) {
					return DOUBLE;
				} else
					return HIT;
			} else if (smallestTotal == 10) {
				if (dealerFaceUp.getValue() >= 2
						&& dealerFaceUp.getValue() <= 9 && firstMove) {
					return DOUBLE;
				} else
					return HIT;
			} else {
				if (dealerFaceUp.getValue() == 1 && !firstMove)
					return HIT;
				else
					return DOUBLE;
			}
		}
		// At this point, the minimum total is 11, and the probability of
		// busting is not zero.
		else {
			double underProb = 0;
			double perfectProb = 0;
			double bustProb = 0;
			for (int i = 0; i < totals.size(); i++) {
				if (totals.get(i) <= 21) {
					double[] temp = counter.calculate(totals.get(i));
					underProb += temp[CardCounter.UNDER];
					perfectProb += temp[CardCounter.PERFECT];
					bustProb += temp[CardCounter.BUST];
				}
			}
			underProb /= totals.size();
			perfectProb /= totals.size();
			bustProb /= totals.size();

			if (BlackJackTesterReal.SHOW_DEBUG_TEXT) {
				System.out.println("The perfect prob is: " + perfectProb);
				System.out.println("The bust prob is: " + bustProb);
				System.out.println("The under prob is: " + underProb);
			}
			if (perfectProb > PERF_THRESH
					|| (underProb > UNDER_THRESH && bustProb < BUST_THRESH))
				return HIT;
			else
				return STAND;
		}
	}

	protected void addToMyHand(Card newCard) {
		myHand.add(newCard);
		counter.newCard(newCard);
	}

	protected void setDealerCard(Card newCard) {
		dealerFaceUp = newCard;
	}

	protected void cardPlayed(Card playedCard) {
		playedCards[playedCard.getValue() - 1]++;
	}

	private void cleanUpTotals() {
		for (int i = 0; i < totals.size(); i++) {
			if (totals.get(i) > 21)
				totals.remove(i);
		}
	}

	protected void resetHand() {
		myHand = new Hand();
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

	protected void resetCardCounter() {
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
}
