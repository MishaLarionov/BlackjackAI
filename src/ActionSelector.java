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

	// AI reference, cardcounter
	private CardCounter counter;
	protected static final double DEF_BUST = 30;
	protected static final double OFF_BUST = 50;

	// Variables in determining the move
	protected int[] aceTotal;
	protected ArrayList<Integer> total;
	protected boolean isAce;
	protected int isPair = 0;

	// 1 is Ace, 11 is Jack, 12 is Queen, 13 is King
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
	public int decideMove() {
		int tempAction = NO_MOVE;

		// Factors in determining basic action
		// isPair = 1 is irrelevant, case is covered in ace section
		total = myHand.recalcTotals();
		if (total.size() == 1)
			isAce = false;
		else
			isAce = true;
		// TODO put this in an "if" bracket that only executes on first deal condition
		isPair = isPair();

		// If BlackJack, then ignore everything else and return stand
		if (total == 21 || (total == 11 && isAce))
			return STAND;

		// Covers cheat sheet with first turn algorithms
		if (myHand.size() == 2) {
			// Basic case with no aces or pairs
			if (!isAce && isPair < 6) {
				// Guaranteed action
				if (total < 9)
					return HIT;

				// Ambiguous actions, cardcounter referenced later
				if (total == 9) {
					if (dealerFaceUp.getValue() > 2
							&& dealerFaceUp.getValue() < 7)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				if (total == 10) {
					if (dealerFaceUp.getValue() > 1
							&& dealerFaceUp.getValue() < 10)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				if (total == 11) {
					if (dealerFaceUp.getValue() != 1)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				if (total == 12) {
					if (dealerFaceUp.getValue() > 3
							&& dealerFaceUp.getValue() < 7)
						tempAction = STAND;
					else
						tempAction = HIT;
				}
				if (total > 17)
					tempAction = STAND;
			}

			// If an Ace was dealt, also covers ace pair as per the reference
			// "cheat sheet"
			else if (isAce) {
				if (total == 2)
					tempAction = HIT;
				else if (total == 3 || total == 4) {
					if (dealerFaceUp.getValue() == 5
							|| dealerFaceUp.getValue() == 6)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				} else if (total == 5 || total == 6) {
					if (dealerFaceUp.getValue() > 3
							&& dealerFaceUp.getValue() < 7)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				} else if (total == 7) {
					if (dealerFaceUp.getValue() > 2
							&& dealerFaceUp.getValue() < 7)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				} else if (total == 8) {
					if (dealerFaceUp.getValue() > 2
							&& dealerFaceUp.getValue() < 7)
						tempAction = DOUBLE;
					else if (dealerFaceUp.getValue() == 2
							|| dealerFaceUp.getValue() == 7
							|| dealerFaceUp.getValue() == 8)
						tempAction = STAND;
					else
						tempAction = HIT;
				} else if (total > 8)
					tempAction = STAND;
			}

			// If a pair was dealt (non-aces)
			else if (isPair > 5) {
				if (isPair > 5 && isPair < 9)
					tempAction = HIT;
				else if (isPair == 9) {
					if (dealerFaceUp.getValue() == 7
							|| dealerFaceUp.getValue() > 9)
						tempAction = STAND;
					else
						tempAction = HIT;
				} else
					return STAND;
			}
		}

		// Only total of cards is used to determine action if not first action
		if (!firstTurn) {
			if (total < 9)
				return HIT;
			if (total < 12)
				tempAction = HIT;
			if (total > 17)
				tempAction = STAND;
		}

		// Passes cards to cardcounter
		if (firstTurn)
			firstCards();

		// If an Ace is present, check for the highest possibility not going
		// over 21 and check for probabilities
		if (isAce) {
			int maxValue = aceMax();
			counter.calculate(maxValue);

			// If max value and chance of not going over is less than certain
			// percentage, hit again
			if (counter.probabilities[2] < DEF_BUST && tempAction == STAND)
				tempAction = HIT;
		}
		// If no ace, then only use probabilities
		else {
			counter.calculate(total);

			if (counter.probabilities[2] < DEF_BUST && tempAction == STAND)
				tempAction = HIT;
			else if (counter.probabilities[2] > DEF_BUST)
				tempAction = STAND;
		}

		// Returns the decided action
		return tempAction;
	}

	/**
	 * Gets the total of all the cards
	 * 
	 * @return the total of all cards
	 */
	protected int getCardTotal() {
		int total = 0;

		for (int card = 0; card < myHand.size(); card++)
			total += CARD_VALUES[myHand.get(card).getValue()];

		return total;
	}
	
	/**
	 * Determines if dealt a pair
	 * 
	 * @return if dealt a pair
	 */
	protected int isPair() {
		boolean[] oneCard = new boolean[14];

		for (int card = 0; card < myHand.size(); card++) {
			int cardAt = myHand.get(card).getValue();

			if (oneCard[cardAt])
				return cardAt;

			oneCard[cardAt] = true;
		}

		// If no pair, return 0
		return 0;
	}

	/**
	 * Determines max value in a hand with aces
	 * 
	 * @return
	 */
	protected int aceMax() {
		// Gets number of aces
		int noOfAce = 0;
		for (int card = 0; card < myHand.size(); card++) {
			if (myHand.get(card).getValue() == 1)
				noOfAce++;
		}

		int tempTotal = total;
		tempTotal -= noOfAce;

		// Determines max value
		if (tempTotal < 11)
			tempTotal += 11;

		// If anything was changed, return tempTotal, else return total
		if (tempTotal != total)
			return tempTotal;
		return total;
	}

	private void firstCards() {
		for (int cardIndex = 0; cardIndex < myHand.size(); cardIndex++)
			counter.newCard(myHand.get(cardIndex));
	}

	/**
	 * Sends a dealt "hit" card to the counter to recalculate
	 * 
	 * @param cardDealt
	 */
	protected void cardDealt(Card cardDealt) {
		counter.newCard(cardDealt);
		addToMyHand(cardDealt);
	}

	protected void addToMyHand(Card newCard) {
		myHand.add(newCard);
	}

	protected void setDealerCard(Card newCard) {
		dealerFaceUp = newCard;
	}

	protected void cardPlayed(Card playedCard) {
		playedCards[playedCard.getValue()]++;
	}
}
