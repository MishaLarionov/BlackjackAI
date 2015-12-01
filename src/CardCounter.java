/**
 * Calculates the probability of going bust, getting 21, or staying under 21
 * @author Felix, Iain, Vince
 *
 */
public class CardCounter
{
	private int totalCards = 312;
	private int[] availableCards = new int[13];
	private AI ai;
	private ActionSelector actionSelector;
	private boolean hasAces;

	public CardCounter(AI ai, ActionSelector actionSelector)
	{
		this.ai = ai;
		this.actionSelector=actionSelector;
		for (int card = 0; card < 13; card++)
		{
			availableCards[card] = 24;
		}
		hasAces =false;
	}

	protected void calculate()
	{
		for (int card = 0; card < 13; card++)
		{
			availableCards[card] -= ai.playedCards[card];
			totalCards-=ai.playedCards[card];
		}
		int totalPoints=actionSelector.getCardTotal();
		int leeway = 21-
		
	}

	protected void reset()
	{
		totalCards = 312;
		for (int card = 0; card < 13; card++)
		{
			availableCards[card] = 24;
		}
	}

}
