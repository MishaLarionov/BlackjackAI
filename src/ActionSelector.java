/**
 * Chooses the move to make
 * @author Felix, Iain, Vince
 *
 */
public class ActionSelector
{
	// Constants for returning decided action
	final int STAND = 1;
	final int HIT = 0;
	final int DOUBLE = 2;

	// AI reference
	private AI ai;

	// Variables in determining the move
	protected int total;
	boolean isAce;
	boolean isPair;

	protected ActionSelector(AI ai)
	{
		this.ai = ai;
	}

	/**
	 * Decides move based on the total value of the cards, references Felix's
	 * part when probability is relevant
	 */
	public int decideMove()
	{
		total = getCardTotal();
		isAce = isAce();
		if (total < 8)
			return HIT;

		if (total > 16)
			return STAND;
		return 0;
	}

	/**
	 * Gets the total of all the cards
	 * @return the total of all cards
	 */
	protected int getCardTotal()
	{
		int total = 0;

		for (int card = 0; card < ai.myCards.size(); card++)
			total += ai.myCards.get(card).intValue();

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
		for (int card = 0; card < ai.myCards.size();card++)
		{
			
		}
		
		return false;
	}
}
