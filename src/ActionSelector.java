/**
 * Chooses the move to make
 * @author Felix, Iain, Vince
 *
 */
public class ActionSelector
{
	// Reference to the AI
	private AI ai;
	protected int total;
	final int STAND = 1;
	final int HIT = 0;
	final int DOUBLE = 2;

	protected ActionSelector(AI ai)
	{
		total = getCardTotal();
		this.ai = ai;
		decideMove();
	}
	
	/**
	 * Decides move based on the total value of the cards
	 */
	public int decideMove()
	{
		if (total < 8)
			return HIT;
		if (total > 16)
			return STAND;
		return 0;
	}
	
	
	/**
	 * Gets the total of all the cards
	 * @return
	 */
	protected int getCardTotal()
	{
		int total = 0;

		for (int card = 0; card < ai.myCards.size(); card++)
			total+= ai.myCards.get(card).intValue();

		return total;
	}
}
