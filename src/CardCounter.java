/**
 * Calculates the probability of going bust, getting 21, or staying under 21
 * @author Felix, Iain, Vince
 *
 */
public class CardCounter
{
	// Initializes variables, important ones including the total number of
	// cards, as well as an array to store cards still in play
	private int totalCards = 312;
	private int[] availableCards = new int[13];
	private AI ai;
	private ActionSelector actionSelector;
	protected double[] probabilities;
	private final int UNDER = 0;
	private final int PERFECT = 1;
	private final int BUST = 2;

	/**
	 * The constructor for the CardCounter object, fills up the array of cards
	 * in play with 24 cards for each unique numbered card
	 * @param ai the AI object that controls the bot
	 * @param actionSelector the ActionSeletor object to receive information
	 *            from;
	 */
	protected CardCounter(AI ai, ActionSelector actionSelector)
	{
		// Instantiates variables, fills up the availableCards array with 24 in
		// each index
		this.ai = ai;
		this.actionSelector = actionSelector;
		probabilities = new double[3];
		for (int card = 0; card < 13; card++)
		{
			availableCards[card] = 24;
		}
	}

	/**
	 * Updates the availableCards array with cards dealt by the dealer
	 * @param card the number of the card
	 */
	protected void newCard(int card)
	{
		availableCards[card - 1]--;
	}

	/**
	 * A method for calculating the probability of staying under 21, getting 21,
	 * and going bust (over 21)
	 */
	protected void calculate(int totalPoints)
	{
		// Finds the leeway that is available
		int leeway = 21 - totalPoints;
		// Should the leeway be greater that 11, the bot can never go bust or
		// get 21, and will always stay under (100%)
		if (leeway > 11)
		{
			probabilities[UNDER] = 100;
		}
		else
		{
			// If the leeway is exactly 11, the probability of getting 21 is
			// determined by the probability of an Ace, it is impossible to go
			// bust, and the probability of staying under is 100%
			if (leeway == 11)
			{
				probabilities[PERFECT] = (availableCards[0] / (totalCards * 1.0)) * 100;
				probabilities[UNDER] = 100;
			}
			else
			{
				// Calculates the average probability of staying under 21, by
				// adding up the probabilities of each card that would allow the
				// bot to stay under 21, and then averaging them out
				double averageUnder = 0;
				for (int card = 0; card < leeway - 1; card++)
				{
					averageUnder += availableCards[card] / (totalCards * 1.0);
				}
				// Sets the probability of staying under to the calculated
				// average probability
				probabilities[UNDER] = (averageUnder / leeway - 1) * 100;
				// Should the leeway be 10, the probability of going bust is 0%
				// (Aces will not be counted as 11's for logical reasons), and
				// the probability of getting 21 is the probability of getting a
				// ten or face card
				if (leeway == 10)
				{
					int totalTens = 0;
					for (int count = 9; count < 13; count++)
					{
						totalTens += availableCards[count];
					}
					probabilities[PERFECT] = (totalTens / (totalCards * 1.0)) * 100;
				}
				// Otherwise, the probability of getting 21 is the chance of
				// getting the card with the same value as the leeway
				else
				{
					probabilities[PERFECT] = (availableCards[leeway - 1] / (totalCards * 1.0)) * 100;
				}
				// Calculates the average probability of going bust (over 21),
				// by adding up the probabilities of each card that would cause
				// the bot to go bust, then averaging them
				if (leeway < 10)
				{
					double averageBust = 0;
					for (int card = leeway; card < availableCards.length; card++)
					{
						averageBust += availableCards[card]
								/ (totalCards * 1.0);
					}
					// Sets the probability of going bust to the calculated
					// average
					// probability
					probabilities[BUST] = (averageBust / availableCards.length - leeway) * 100;
				}
			}
		}

	}

	/**
	 * A method that allows the total number of cards and the availableCards
	 * array to be rest to their original values
	 */
	protected void reset()
	{
		totalCards = 312;
		for (int card = 0; card < 13; card++)
		{
			availableCards[card] = 24;
		}
	}

}
