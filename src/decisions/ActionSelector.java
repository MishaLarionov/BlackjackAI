package decisions;

import java.util.ArrayList;
import java.util.Collections;

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

	// Probability of going under has to be greater than threshold to hit
	private static double UNDER_THRESH = 0.15;
	// Prob. of going bust must be less than thresh. to hit
	private static double BUST_THRESH = 0.485;
	// If blackjacking probability is greater than this, it'll play hit
	// regardless
	private final static double PERF_THRESH = 0.8;
	private final static double DOUBLE_THRESH = 0.15;

	// AI reference, cardcounter
	private CardCounter counter;

	// Variables in determining the move
	private ArrayList<Integer> totals;

	private Hand myHand;
	// Each index's possible value is 0-24
	private Card dealerFaceUp;

	private static final boolean DEBUG = false;

	/**
	 * Creates an ActionSelector
	 */
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

		int smallestTotal = Collections.min(totals);
		// The probability of busting at this point is zero, so we don't have to
		// take into account probabilities and such. Does actions based on the
		// pdf "cheat sheet"
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
			// Calculates probabilities for all the totals
			for (int i = 0; i < totals.size(); i++) {
				if (totals.get(i) <= 21) {
					double[] temp = counter.calculate(totals.get(i));
					underProb += temp[CardCounter.UNDER];
					perfectProb += temp[CardCounter.PERFECT];
					bustProb += temp[CardCounter.BUST];
					doubleProb += temp[CardCounter.DOUBLE];
				}
			}
			// Gets their average probability
			underProb /= totals.size();
			perfectProb /= totals.size();
			bustProb /= totals.size();
			doubleProb /= totals.size();

			// Debug.
			if (DEBUG) {
				System.out.println("The perfect prob is: " + perfectProb);
				System.out.println("The bust prob is: " + bustProb);
				System.out.println("The under prob is: " + underProb);
				System.out.println("The double prob is: " + doubleProb);
			}

			// Does an action based on the probabilities
			if ((doubleProb > DOUBLE_THRESH || perfectProb > PERF_THRESH)
					&& allowedToDouble)
				return DOUBLE;
			else if (underProb > UNDER_THRESH && bustProb < BUST_THRESH)
				return HIT;
			else
				return STAND;
		}
	}

	/**
	 * Adds a card to my current hand
	 * 
	 * @param newCard
	 *            the newly dealt card
	 */
	public void addToMyHand(Card newCard) {
		myHand.add(newCard);
	}

	/**
	 * Changes the dealer's card
	 * 
	 * @param newCard
	 *            the dealer's face up card
	 */
	public void setDealerCard(Card newCard) {
		dealerFaceUp = newCard;
		counter.newCard(newCard);
	}

	/**
	 * Adds a card to the card counting
	 * 
	 * @param playedCard
	 *            the card that was just played
	 */
	protected void cardPlayed(Card playedCard) {
		// Changes the index in the array
		counter.newCard(playedCard);
	}

	/**
	 * Removes all the other totals that are above 21 (because they're useless)
	 */
	private void cleanUpTotals() {
		for (int i = 0; i < totals.size(); i++) {
			if (totals.get(i) > 21)
				totals.remove(i);
		}
	}

	/**
	 * Resets the hand (for new rounds)
	 */
	public void resetHand() {
		myHand = new Hand();
		dealerFaceUp = null;
	}

	/**
	 * Used to calculate if the ArrayList contains any "high" numbers so it stands automatically
	 * @param targets targets to match to
	 * @return if the ArrayList contains any of these numbers.
	 */
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

	/**
	 * For when the dealer shuffles.
	 */
	public void resetCardCounter() {
		counter.resetCounter();
	}

	/**
	 * Gets the hand.
	 * @return myHand
	 */
	public Hand getMyHand() {
		return myHand;
	}

	/**
	 * Gets the thresholds 
	 * @return A string describing the thresholds
	 */
	public static String getThresholds() {
		return "BustT = " + BUST_THRESH + " UnderT = " + UNDER_THRESH;
	}

	/**
	 * Gets the dealer's face up card
	 * @return the dealer's face up card
	 */
	public Card getDealerFaceUp() {
		return dealerFaceUp;
	}

	/**
	 * Changes the under-threshold
	 * @param uNDER_THRESH new under-threshold
	 */
	public static void setUNDER_THRESH(double uNDER_THRESH) {
		UNDER_THRESH = uNDER_THRESH;
	}

	/**
	 * Changes the bust-threshold
	 * @param bUST_THRESH the new bust-threshold
	 */
	public static void setBUST_THRESH(double bUST_THRESH) {
		BUST_THRESH = bUST_THRESH;
	}
}
