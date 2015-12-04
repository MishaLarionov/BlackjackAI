import java.util.ArrayList;

@SuppressWarnings("serial")
public class Hand extends ArrayList<Card> {

	Hand() {
	}

	void addCard(Card card) {
		this.add(card);
	}
}
