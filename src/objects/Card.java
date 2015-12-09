package objects;
public class Card implements Comparable<Card> {

	/* 1 is Ace, 2-10 are as normal, 11 is Jack, 12 is Queen, 13 is King */
	private int value;

	public Card(int value) {
		this.value = value;
	}
	
	Card(char cardVal){
		cardVal = Character.toUpperCase(cardVal);		
		// Special cases for "face" cards		
		switch (cardVal) {		
		case 'A':		
			value = 1;		
			break;		
		case 'T':		
			value = 10;		
			break;		
		case 'J':		
			value = 11;		
			break;		
		case 'Q':		
			value = 12;		
			break;		
		case 'K':		
			value = 13;		
			break;		
			// Not face cards		
		default:		
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
