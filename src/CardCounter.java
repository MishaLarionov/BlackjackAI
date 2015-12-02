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
	private double[] probabilities;
	private final int UNDER = 0;
	private final int PERFECT = 1;
	private final int BUST = 2;

	public CardCounter(AI ai, ActionSelector actionSelector)
	{
		this.ai = ai;
		this.actionSelector = actionSelector;
		probabilities = new double[3];
		for (int card = 0; card < 13; card++)
		{
			availableCards[card] = 24;
		}
	}

	protected double[] calculate()
	{
		int totalPoints = actionSelector.getCardTotal();
		int leeway = 21 - totalPoints;
		if (leeway > 11)
		{
			probabilities[UNDER] = 100;
			return probabilities;
		}
		else
		{
			for (int card = 0; card < 13; card++)
			{
				availableCards[card] -= ai.playedCards[card];
				totalCards -= ai.playedCards[card];
			}
			if (leeway == 11)
			{
				probabilities[PERFECT] = (availableCards[0] / (totalCards * 1.0)) * 100;
				probabilities[UNDER] = 100;
				return probabilities;
			}
			else
			{
				double averageUnder = 0;
				for (int card = 0; card < leeway - 1; card++)
				{
					averageUnder += availableCards[card] / (totalCards * 1.0);
				}
				probabilities[UNDER] = (averageUnder / leeway - 1) * 100;
				if (leeway == 10)
				{
					int totalTens = 0;
					for (int count = 9; count < 13; count++)
					{
						totalTens += availableCards[count];
					}
					probabilities[PERFECT] = (totalTens / (totalCards * 1.0)) * 100;
					return probabilities;
				}
				else
				{
					probabilities[PERFECT] = (availableCards[leeway - 1] / (totalCards * 1.0)) * 100;
				}
				double averageBust = 0;
				for (int card = leeway; card < availableCards.length; card++)
				{
					averageBust += availableCards[card] / (totalCards * 1.0);
				}
				probabilities[BUST] = (averageBust / availableCards.length - leeway) * 100;
				return probabilities;
			}
		}

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
