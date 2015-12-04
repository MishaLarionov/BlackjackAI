import java.util.ArrayList;

@SuppressWarnings("serial")
public class Hand extends ArrayList<Card> {

	Hand() {
	}

	void addCard(Card card) {
		this.add(card);
	}
	
	ArrayList<Integer> recalcTotals() {
		ArrayList<Integer> possibleTotals = new ArrayList<Integer>();
		possibleTotals.add(0);
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
}
