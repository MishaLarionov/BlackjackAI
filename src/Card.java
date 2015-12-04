public class Card {

	/* 1 is Ace, 2-10 are as normal, 11 is Jack, 12 is Queen, 13 is King */
	private int value;

	Card(int value) {
		this.value = value;
	}

	int getValue() {
		return value;
	}

	public String toString() {
		return value + "";
	}
}
