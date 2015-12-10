package decisions;
import java.util.Arrays;

import objects.Card;

/**
 * Calculates the probability of going bust, getting 21, or staying under 21
 * 
 * @author Felix, Iain, Vince
 *
 */
public class CardCounter {
	// Initializes variables, important ones including the total number of
	// cards, as well as an array to store cards still in play
	private final static int DECKS = 6;
	private int totalCards = 52 * DECKS;
	private int[] availableCards = new int[13];
	private double[] probabilities;
	
	// Positions in the probabilities array for each value
	public final static int UNDER = 0;
	public final static int PERFECT = 1;
	public final static int BUST = 2;
	public final static int DOUBLE = 3;
	
	private final static int DOUBLE_LEEWAY = 2;

	/**
	 * The constructor for the CardCounter object, fills up the array of cards
	 * in play with 24 cards for each unique numbered card
	 */
	protected CardCounter() {
		// Instantiates variables, fills up the availableCards array with 24 in
		// each index
		probabilities = new double[4];
		resetCounter();
	}

	/**
	 * Updates the availableCards array with cards dealt by the dealer
	 * @param card the card
	 */
	protected void newCard(Card card) {
		availableCards[card.getValue() - 1]--;
	}

	/**
	 * A method for calculating the probability of staying under 21, getting 21,
	 * and going bust (over 21)
	 */
	protected double[] calculate(int currTotal) {
		// Resets array
		probabilities[UNDER] = 0;
		probabilities[PERFECT] = 0;
		probabilities[BUST] = 0;
		probabilities[DOUBLE] = 0;
		
		// Calculates the probabilites of a double
		int doubleCards = 0;
		for (int card = 21 - DOUBLE_LEEWAY; card < availableCards.length; card++) {
			doubleCards += availableCards[card];
		}
		probabilities[DOUBLE] = doubleCards / (totalCards * 1.0);
		
		// Finds the leeway that is available
		int leeway = 21 - currTotal;

		// Should the leeway be greater that 11, the bot can never go bust or
		// get 21, and will always stay under (100%)
		if (leeway > 11) {
			probabilities[UNDER] = 1;
		}
		// If the leeway is exactly 11, the probability of getting 21 is
		// determined by the probability of an Ace
		else if (leeway == 11) {

			probabilities[PERFECT] = availableCards[0] / (totalCards * 1.0);
			probabilities[UNDER] = 1 - probabilities[PERFECT];
		}
		// If the leeway is 10, the probability of going bust is 0%, the
		// probability of bust is zero (aces are treated as ones)
		else if (leeway == 10) {
			int totalTens = 0;
			for (int count = 9; count < 13; count++)
				totalTens += availableCards[count];
			probabilities[PERFECT] = totalTens / (totalCards * 1.0);
			probabilities[UNDER] = 1 - probabilities[PERFECT];
		}
		// All the other possibilities
		else {
			// Calculates the average probability of staying under
			int cardsUnder = 0;
			for (int card = 0; card < leeway - 1; card++)
				cardsUnder += availableCards[card];
			probabilities[UNDER] = cardsUnder / (totalCards * 1.0);

			// Calculates chance of blackjack
			probabilities[PERFECT] = availableCards[leeway - 1]
					/ (totalCards * 1.0);

			// Calculates the average probability of going bust
			int cardsOver = 0;
			for (int card = leeway; card < availableCards.length; card++)
				cardsOver += availableCards[card];
			probabilities[BUST] = cardsOver / (totalCards * 1.0);
		}
		// Returns a copy of the array (so that things don't get messed up)
		return Arrays.copyOf(probabilities, probabilities.length);
	}

	/**
	 * A method that allows the total number of cards and the availableCards
	 * array to be rest to their original values
	 */
	protected void resetCounter() {
		totalCards = DECKS * 52;

		for (int card = 0; card < 13; card++)
			availableCards[card] = 24;
	}
}
