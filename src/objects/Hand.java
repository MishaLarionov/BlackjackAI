package objects;

import java.util.ArrayList;

/**
 * To hold the cards of the AI's hand
 * @author Vince, Felix, Iain
 *
 */
@SuppressWarnings("serial")
public class Hand extends ArrayList<Card> {

	/**
	 * Creates an empty hand
	 */
	public Hand() {
	}

	/**
	 * Adds a card to the hand
	 * @param card the new card to add
	 */
	void addCard(Card card) {
		this.add(card);
	}

	/**
	 * Calculates the totals of the hand
	 * @return
	 */
	public ArrayList<Integer> recalcTotals() {
		// Creates a possible totals
		ArrayList<Integer> possibleTotals = new ArrayList<Integer>();
		possibleTotals.add(0);
		// Changes the values as needed for face cards, and adds it over
		for (int i = 0; i < this.size(); i++) {
			int card = this.get(i).getValue();
			switch (card) {
			case 11:
				card = 10;
				break;
			case 12:
				card = 10;
				break;
			case 13:
				card = 10;
				break;

			default:
				break;
			}
			for (int ptIndex = 0; ptIndex < possibleTotals.size(); ptIndex++) {
				possibleTotals.set(ptIndex, possibleTotals.get(ptIndex) + card);
			}
			// Special case for aces
			if (card == 1) {
				for (int j = possibleTotals.size() - 1; j >= 0; j--) {
					possibleTotals.add(possibleTotals.get(j) + 10);
				}
			}
		}
		return possibleTotals;
	}

	public String toString() {
		// Creates the string of current cards
		String out = "";
		for (int i = 0; i < this.size() - 1; i++) {
			out += this.get(i) + ",";
		}
		out += this.get(this.size() - 1);
		return out;
	}
}
