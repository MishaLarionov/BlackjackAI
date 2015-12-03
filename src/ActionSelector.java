/**
 * Chooses the move to make
 * @author Felix, Iain, Vince
 *
 */
public class ActionSelector
{
	// Constants for returning decided action
	protected static final int HIT = 0;
	protected static final int STAND = 1;
	protected static final int DOUBLE = 2;
	protected static final int NO_MOVE = -1;
	protected static final int[] CARD_VALUES = new int[] { 0, 1, 2, 3, 4, 5, 6,
			7, 8, 9, 10, 10, 10, 10 };

	// AI reference, cardcounter
	private AI ai;
	private CardCounter counter;
	protected static final double DEF_BUST = 30;
	protected static final double OFF_BUST = 50;

	// Variables in determining the move
	protected int[] aceTotal;
	protected int total;
	protected boolean isAce;
	protected int isPair = 0;

	protected ActionSelector(AI ai)
	{
		this.ai = ai;
		counter = new CardCounter();
	}

	/**
	 * Decides move based on the total value of the cards, references Felix's
	 * part when probability is relevant
	 */
	public int decideMove(boolean firstTurn)
	{
		int tempAction = NO_MOVE;

		// Factors in determining basic action
		// isPair = 1 is irrelevant, case is covered in ace section
		total = getCardTotal();
		isAce = isAce();
		isPair = isPair();

		// If BlackJack, then ignore everything else and return stand
		if (total == 21 || (total == 11 && isAce))
			return STAND;

		// Covers cheat sheet with first turn algorithms
		if (firstTurn)
		{
			// Basic case with no aces or pairs
			if (!isAce && isPair < 6)
			{
				// Guaranteed action
				if (total < 9)
					return HIT;

				// Ambiguous actions, cardcounter referenced later
				if (total == 9)
				{
					if (ai.dealerFaceUp > 2 && ai.dealerFaceUp < 7)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				if (total == 10)
				{
					if (ai.dealerFaceUp > 1 && ai.dealerFaceUp < 10)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				if (total == 11)
				{
					if (ai.dealerFaceUp != 1)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				if (total == 12)
				{
					if (ai.dealerFaceUp > 3 && ai.dealerFaceUp < 7)
						tempAction = STAND;
					else
						tempAction = HIT;
				}
				if (total > 17)
					tempAction = STAND;
			}

			// If an Ace was dealt, also covers ace pair as per the reference
			// "cheat sheet"
			else if (isAce)
			{
				if (total == 2)
					tempAction = HIT;
				else if (total == 3 || total == 4)
				{
					if (ai.dealerFaceUp == 5 || ai.dealerFaceUp == 6)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				else if (total == 5 || total == 6)
				{
					if (ai.dealerFaceUp > 3 && ai.dealerFaceUp < 7)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				else if (total == 7)
				{
					if (ai.dealerFaceUp > 2 && ai.dealerFaceUp < 7)
						tempAction = DOUBLE;
					else
						tempAction = HIT;
				}
				else if (total == 8)
				{
					if (ai.dealerFaceUp > 2 && ai.dealerFaceUp < 7)
						tempAction = DOUBLE;
					else if (ai.dealerFaceUp == 2 || ai.dealerFaceUp == 7
							|| ai.dealerFaceUp == 8)
						tempAction = STAND;
					else
						tempAction = HIT;
				}
				else if (total > 8)
					tempAction = STAND;
			}

			// If a pair was dealt (non-aces)
			else if (isPair > 5)
			{
				if (isPair > 5 && isPair < 9)
					tempAction = HIT;
				else if (isPair == 9)
				{
					if (ai.dealerFaceUp == 7 || ai.dealerFaceUp > 9)
						tempAction = STAND;
					else
						tempAction = HIT;
				}
				else
					return STAND;
			}
		}

		// Only total of cards is used to determine action if not first action
		if (!firstTurn)
		{
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
		if (isAce)
		{
			int maxValue = aceMax();
			counter.calculate(maxValue);

			// TODO add algorithm for changing actions based on probabilities
		}
		// If no ace, then only use probabilities
		else
		{
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
	 * @return the total of all cards
	 */
	protected int getCardTotal()
	{
		int total = 0;

		for (int card = 0; card < ai.myHand.size(); card++)
			total += CARD_VALUES[ai.myHand.get(card).intValue()];

		return total;
	}

	/**
	 * Determines if there is an ace in the dealt cards
	 * @return if there is an ace in the dealt cards
	 */
	protected boolean isAce()
	{
		for (int card = 0; card < ai.myHand.size(); card++)
			if (ai.myHand.get(card).intValue() == 1)
				return true;

		return false;
	}

	/**
	 * Determines if dealt a pair
	 * @return if dealt a pair
	 */
	protected int isPair()
	{
		boolean[] oneCard = new boolean[14];

		for (int card = 0; card < ai.myHand.size(); card++)
		{
			int cardAt = ai.myHand.get(card).intValue();

			if (oneCard[cardAt])
				return cardAt;

			oneCard[cardAt] = true;
		}

		// If no pair, return 0
		return 0;
	}

	/**
	 * Determines max value in a hand with aces
	 * @return
	 */
	protected int aceMax()
	{
		// Gets number of aces
		int noOfAce = 0;
		for (int card = 0; card < ai.myHand.size(); card++)
		{
			if (ai.myHand.get(card).intValue() == 1)
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

	private void firstCards()
	{
		for (int card = 0; card < ai.myHand.size(); card++)
			counter.newCard(ai.myHand.get(card).intValue());
	}

	/**
	 * Sends a dealt "hit" card to the counter to recalculate
	 * @param cardDealt
	 */
	private void cardDealt(int cardDealt)
	{
		counter.newCard(cardDealt);
	}
}
