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
	final int[] CARD_VALUES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10,
			10, 10 };

	// AI reference, cardcounter
	private AI ai;
	private CardCounter counter;

	// Variables in determining the move
	protected int total;
	boolean isAce;
	boolean isPair;

	protected ActionSelector(AI ai)
	{
		this.ai = ai;
		counter = new CardCounter(ai, this);
	}

	/**
	 * Decides move based on the total value of the cards, references Felix's
	 * part when probability is relevant
	 */
	public int decideMove()
	{
		int tempAction = -1;

		// Temp, not needed in final code
		boolean cardCountHere = true;

		total = getCardTotal();
		// isAce = isAce();

		// Basic case with no aces or pairs
		if (!isAce && !isPair)
		{
			// Guaranteed action
			if (total < 9)
				return HIT;

			// Combos using CardCounter object to perform calculations
			if (total == 9)
			{
				if (ai.dealerFaceUp > 2 && ai.dealerFaceUp < 7)
				{
					tempAction = DOUBLE;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
				else
				{
					tempAction = HIT;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
			}
			if (total == 10)
			{
				if (ai.dealerFaceUp > 1 && ai.dealerFaceUp < 10)
				{
					tempAction = DOUBLE;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
				else
				{
					tempAction = HIT;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
			}
			if (total == 11)
			{
				if (ai.dealerFaceUp != 1)
				{
					tempAction = DOUBLE;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
				else
				{
					tempAction = HIT;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
			}
			if (total == 12)
			{
				if (ai.dealerFaceUp > 3 && ai.dealerFaceUp < 7)
				{
					tempAction = STAND;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
				else
				{
					tempAction = HIT;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
			}
			if (total > 12 && total < 17)
			{
				if (ai.dealerFaceUp > 7)
				{
					tempAction = HIT;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
				else
				{
					tempAction = STAND;

					// Call probability, if above certain threshold for certain
					// possibilities, then change decided action
					cardCountHere = true;
				}
			}
			if (total > 17)
			{
				tempAction = STAND;

				// Higher threshold of probability needed for this situation to
				// change decision
				cardCountHere = true;
			}
		}

		// If an Ace was dealt

		// If a pair was dealt

		return tempAction;
	}

	/**
	 * Gets the total of all the cards
	 * @return the total of all cards
	 */
	protected int getCardTotal()
	{
		int total = 0;

		for (int card = 0; card < ai.myCards.size(); card++)
		{
			total += CARD_VALUES[ai.myCards.get(card).intValue()];
		}

		return total;
	}

	/**
	 * Determines if there is an ace in the dealt cards
	 * @return if there is an ace in the dealt cards
	 */
	protected boolean isAce()
	{
		for (int card = 0; card < ai.myCards.size(); card++)
			if (ai.myCards.get(card).intValue() == 1)
				return true;

		return false;
	}

	/**
	 * Determines if dealt a pair
	 * @return if dealt a pair
	 */
	protected boolean isPair()
	{
		boolean[] oneCard = new boolean[14];

		for (int card = 0; card < ai.myCards.size(); card++)
		{
			int cardAt = ai.myCards.get(card).intValue();

			// if (oneCard[])
		}

		return false;
	}
}
