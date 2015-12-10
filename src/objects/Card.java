package objects;

/**
 * A card beind used in the game
 * @author Vince, Felix, Iain
 *
 */
public class Card implements Comparable<Card> {

	/* 1 is Ace, 2-10 are as normal, 11 is Jack, 12 is Queen, 13 is King */
	private int value;

	private final static boolean DEBUG = false;

	/**
	 * Creates a card based on a numerical value
	 * @param value
	 */
	public Card(int value) {
		this.value = value;
	}

	/**
	 * Creates a card based on the character sent by the server
	 * @param cardVal
	 */
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
			
		// Not face cards (number)
		default:
			if (DEBUG)
				System.out.println("Number card intercepted");
			value = Integer.parseInt(cardVal + "");
			break;
		}
	}

	/**
	 * @return gets the value of the card
	 */
	public int getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value + "";
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Card otherCard) {
		return value - otherCard.getValue();
	}
}
