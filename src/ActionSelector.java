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
	static double UNDER_THRESH = 0.19;
	// Prob. of going bust must be less than thresh. to hit
	static double BUST_THRESH = 0.43;
	// If blackjacking probability is greater than this, it'll play hit
	// regardless
	static double PERF_THRESH = 0.75;

	// AI reference, cardcounter
	private CardCounter counter;
	// protected static final double DEF_BUST = 30;
	// protected static final double OFF_BUST = 50;

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
		// int tempAction = NO_MOVE;

		// Factors in determining basic action
		totals = myHand.recalcTotals();
		cleanUpTotals();
		Collections.sort(totals);

		// if (totals.size() == 1)
		// hasAce = false;
		// else
		// hasAce = true;

		if (totalContainsAny(new int[] { 21, 20, 19, 18 }))
			return STAND;

		int smallestTotal = totals.get(0);
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

	/*
	 * // If Blackjack, then ignore everything else and return stand if
	 * (totals.contains(21)) return STAND;
	 * 
	 * // Covers cheat sheet with first turn algorithms if (myHand.size() == 2)
	 * { // Basic case with no aces or pairs if (!hasAce && isPair < 6) { //
	 * Guaranteed action if (totals < 9) return HIT;
	 * 
	 * // Ambiguous actions, cardcounter referenced later if (totals == 9) { if
	 * (dealerFaceUp.getValue() > 2 && dealerFaceUp.getValue() < 7) tempAction =
	 * DOUBLE; else tempAction = HIT; } if (totals == 10) { if
	 * (dealerFaceUp.getValue() > 1 && dealerFaceUp.getValue() < 10) tempAction
	 * = DOUBLE; else tempAction = HIT; } if (totals == 11) { if
	 * (dealerFaceUp.getValue() != 1) tempAction = DOUBLE; else tempAction =
	 * HIT; } if (totals == 12) { if (dealerFaceUp.getValue() > 3 &&
	 * dealerFaceUp.getValue() < 7) tempAction = STAND; else tempAction = HIT; }
	 * if (totals > 17) tempAction = STAND; }
	 * 
	 * // If an Ace was dealt, also covers ace pair as per the reference //
	 * "cheat sheet" else if (hasAce) { if (totals == 2) tempAction = HIT; else
	 * if (totals == 3 || totals == 4) { if (dealerFaceUp.getValue() == 5 ||
	 * dealerFaceUp.getValue() == 6) tempAction = DOUBLE; else tempAction = HIT;
	 * } else if (totals == 5 || totals == 6) { if (dealerFaceUp.getValue() > 3
	 * && dealerFaceUp.getValue() < 7) tempAction = DOUBLE; else tempAction =
	 * HIT; } else if (totals == 7) { if (dealerFaceUp.getValue() > 2 &&
	 * dealerFaceUp.getValue() < 7) tempAction = DOUBLE; else tempAction = HIT;
	 * } else if (totals == 8) { if (dealerFaceUp.getValue() > 2 &&
	 * dealerFaceUp.getValue() < 7) tempAction = DOUBLE; else if
	 * (dealerFaceUp.getValue() == 2 || dealerFaceUp.getValue() == 7 ||
	 * dealerFaceUp.getValue() == 8) tempAction = STAND; else tempAction = HIT;
	 * } else if (totals > 8) tempAction = STAND; }
	 * 
	 * // If a pair was dealt (non-aces) else if (isPair > 5) { if (isPair > 5
	 * && isPair < 9) tempAction = HIT; else if (isPair == 9) { if
	 * (dealerFaceUp.getValue() == 7 || dealerFaceUp.getValue() > 9) tempAction
	 * = STAND; else tempAction = HIT; } else return STAND; } }
	 * 
	 * // Only total of cards is used to determine action if not first action if
	 * (!firstTurn) { if (totals < 9) return HIT; if (totals < 12) tempAction =
	 * HIT; if (totals > 17) tempAction = STAND; }
	 * 
	 * // Passes cards to cardcounter // if (firstTurn) // firstCards();
	 * 
	 * // If an Ace is present, check for the highest possibility not going //
	 * over 21 and check for probabilities if (hasAce) { int maxValue =
	 * aceMax(); counter.calculate(maxValue);
	 * 
	 * // If max value and chance of not going over is less than certain //
	 * percentage, hit again if (counter.probabilities[2] < DEF_BUST &&
	 * tempAction == STAND) tempAction = HIT; } // If no ace, then only use
	 * probabilities else { counter.calculate(totals);
	 * 
	 * if (counter.probabilities[2] < DEF_BUST && tempAction == STAND)
	 * tempAction = HIT; else if (counter.probabilities[2] > DEF_BUST)
	 * tempAction = STAND; }
	 * 
	 * // Returns the decided action return tempAction; } // // // /** // * Gets
	 * the total of all the cards // * // * @return the total of all cards //
	 */
	// protected int getCardTotal() {
	// int total = 0;
	//
	// for (int card = 0; card < myHand.size(); card++)
	// total += CARD_VALUES[myHand.get(card).getValue()];
	//
	// return total;
	// }
	//
	// /**
	// * Determines max value in a hand with aces
	// *
	// * @return
	// */
	// protected int aceMax() {
	// // Gets number of aces
	// int noOfAce = 0;
	// for (int card = 0; card < myHand.size(); card++) {
	// if (myHand.get(card).getValue() == 1)
	// noOfAce++;
	// }
	//
	// int tempTotal = total;
	// tempTotal -= noOfAce;
	//
	// // Determines max value
	// if (tempTotal < 11)
	// tempTotal += 11;
	//
	// // If anything was changed, return tempTotal, else return total
	// if (tempTotal != total)
	// return tempTotal;
	// return total;
	// }

	protected void addToMyHand(Card newCard) {
		myHand.add(newCard);
		counter.newCard(newCard);
		totals = myHand.recalcTotals();
	}

	protected void setDealerCard(Card newCard) {
		dealerFaceUp = newCard;
	}

	protected void cardPlayed(Card playedCard) {
		playedCards[playedCard.getValue()]++;
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
		counter.reset();
	}
}
