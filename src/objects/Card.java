package objects;

public class Card implements Comparable<Card> {

	/* 1 is Ace, 2-10 are as normal, 11 is Jack, 12 is Queen, 13 is King */
	private int value;

	private final static boolean DEBUG = false;

	public Card(int value) {
		this.value = value;
	}

	public Card(char cardVal) {
		cardVal = Character.toUpperCase(cardVal);
		// Special cases for "face" cards
		switch (cardVal) {
		case 'A':
			if (DEBUG)
				System.out.println("Ace intercepted");
			value = 1;
			break;
		case 'T':
			if (DEBUG)
				System.out.println("Ten intercepted");
			value = 10;
			break;
		case 'J':
			if (DEBUG)
				System.out.println("Jack intercepted");
			value = 11;
			break;
		case 'Q':
			if (DEBUG)
				System.out.println("Queen intercepted");
			value = 12;
			break;
		case 'K':
			if (DEBUG)
				System.out.println("King intercepted");
			value = 13;
			break;
		// Not face cards
		default:
			if (DEBUG)
				System.out.println("Number card intercepted");
			value = Integer.parseInt(cardVal + "");
			break;
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		return value + "";
	}

	public int compareTo(Card otherCard) {
		return value - otherCard.getValue();
	}
}
