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
	protected static final int[] CARD_VALUES = new int[] { 0, 1, 2, 3, 4, 5, 6,
			7, 8, 9, 10, 10, 10, 10 };

	// AI reference, cardcounter
	private AI ai;
	private CardCounter counter;

	// Variables in determining the move
	protected int[] aceTotal;
	protected int total;
	protected boolean isAce;
	protected int isPair = 0;

	protected ActionSelector(AI ai)
	{
		this.ai = ai;
		counter = new CardCounter(ai, this);
	}

	/**
	 * Decides move based on the total value of the cards, references Felix's
	 * part when probability is relevant
	 */
	public int decideFirstMove()
	{
		int tempAction = -1;

		// Factors in determining basic action
		// isPair = 1 is irrelevant, case is covered in ace section
		total = getCardTotal();
		isAce = isAce();
		isPair = isPair();

		// Basic case with no aces or pairs above 5 (otherwise same actions as
		// no split command is available)
		if (!isAce && isPair < 6)
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
				}
				else
				{
					tempAction = HIT;
				}
			}
			if (total == 10)
			{
				if (ai.dealerFaceUp > 1 && ai.dealerFaceUp < 10)
				{
					tempAction = DOUBLE;
				}
				else
				{
					tempAction = HIT;
				}
			}
			if (total == 11)
			{
				if (ai.dealerFaceUp != 1)
				{
					tempAction = DOUBLE;
				}
				else
				{
					tempAction = HIT;
				}
			}
			if (total == 12)
			{
				if (ai.dealerFaceUp > 3 && ai.dealerFaceUp < 7)
				{
					tempAction = STAND;
				}
				else
				{
					tempAction = HIT;
				}
			}
			if (total > 12 && total < 17)
			{
				if (ai.dealerFaceUp > 7)
				{
					tempAction = HIT;
				}
				else
				{
					tempAction = STAND;
				}
			}
			if (total > 17)
			{
				tempAction = STAND;
			}
		}

		// If an Ace was dealt
		else if (isAce)
		{

		}

		// If a pair was dealt
		else if (isPair > 1)
		{

		}

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
		{
			total += CARD_VALUES[ai.myHand.get(card).intValue()];
		}

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
	 * Sends a dealt "hit" card to the counter to recalculate
	 * @param cardDealt
	 */
	protected void cardDealt(int cardDealt)
	{
		counter.newCard(cardDealt);
	}
}
