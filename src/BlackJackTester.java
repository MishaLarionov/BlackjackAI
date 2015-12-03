import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BlackJackTester {

	int[] remainingCards = new int[13];
	ArrayList<Integer> myHand = new ArrayList<Integer>();
	ArrayList<Integer> dHand = new ArrayList<Integer>();
	int dealerFaceUp;

	int wins = 0;
	int losses = 0;

	ArrayList<Integer> myTotals;
	ArrayList<Integer> dealerTotals;

	static final boolean AI = false;

	public static void main(String[] args) {
		try {
			new BlackJackTester(AI);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	BlackJackTester(boolean user) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		for (int i = 0; i < remainingCards.length; i++) {
			remainingCards[i] = 24;
		}

		while (true) {

			myHand.add(randomCard());
			myHand.add(randomCard());

			dHand.add(randomCard());
			dHand.add(randomCard());
			dealerFaceUp = dHand.get(0);

			char action = (char) -1;
			int move = -1;

			// if (user) {
			myTotals = recalcTotals(myHand);
			System.out.println("Your cards are: " + myHand
					+ " and your possible totals are " + myTotals);

			System.out.println("The dealer is: " + dHand.get(0));
			System.out.println("What is your move? (h/s/d)");
			action = Character.toLowerCase(br.readLine().charAt(0));

			while (action == 'h') {
				myHand.add(randomCard());
				myTotals = recalcTotals(myHand);
				if (!totalsOverLimit(myTotals, 20)) {
					System.out.println("Your cards are: " + myHand
							+ " and your possible totals are " + myTotals);
					System.out.println("What is your next move?");
					action = Character.toLowerCase(br.readLine().charAt(0));
				} else {
					action = 'b';
				}

			}

			if (action == 'd') {
				myHand.add(randomCard());
				myTotals = recalcTotals(myHand);
			}
			// } else {
			// ActionSelector decision = new ActionSelector();
			// move = decision.decideMove(true);
			// while (move == ActionSelector.HIT) {
			// myHand.add(randomCard());
			// if (totalOfHand(myHand) <= 23) {
			// move = decision.decideMove(false);
			// }
			// }
			//
			// if (move == ActionSelector.DOUBLE) {
			// myHand.add(randomCard());
			// }
			// }

			// myTotals = recalcTotals(myHand);
			dealerTotals = recalcTotals(dHand);

			while (!totalsOverLimit(dealerTotals, 16)) {
				dHand.add(randomCard());
				dealerTotals = recalcTotals(dHand);
			}

			System.out.println("Dealer's cards are: " + dHand
					+ " and their possible totals are " + dealerTotals);
			System.out.println("Your cards are: " + myHand
					+ " and your possible totals are " + myTotals);

			if (dealerTotals.contains(21)) {
				System.out.println("dealer blackjacked.");
				losses++;
			} else if (myTotals.contains(21)) {
				System.out.println("You blackjacked.");
				wins++;
				if (action == 'd' || move == ActionSelector.DOUBLE)
					wins++;
			} else if (action == 'b') {
				System.out.println("You busted.");
				losses++;
			} else if (totalsOverLimit(dealerTotals, 21)) {
				System.out.print("Dealer busted.");
				if (action == 'b')
					System.out.println("Draw.");
				else {
					System.out.println("You win.");
					wins++;
				}
			} else if (playerWin()) {
				System.out
						.println("You have a higher value than dealer. You win.");
				wins++;
				if (action == 'd' || move == ActionSelector.DOUBLE)
					wins++;
			} else {
				System.out.println("Dealer wins.");
				losses++;
			}

			System.out.println("Wins: " + wins + " Losses: " + losses + "\n\n");

			myHand = new ArrayList<Integer>();
			dHand = new ArrayList<Integer>();
		}
	}

	int randomCard() {
		int card = 0;
		do {
			card = (int) (Math.random() * remainingCards.length - 1) + 1;
		} while (remainingCards[card] <= 0);

		remainingCards[card]--;
		return card;
	}

	ArrayList<Integer> recalcTotals(ArrayList<Integer> hand) {
		ArrayList<Integer> possibleTotals = new ArrayList<Integer>();
		possibleTotals.add(0);
		for (int i = 0; i < hand.size(); i++) {
			int card = hand.get(i);
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
					possibleTotals.add(possibleTotals.get(j) + 11);
				}
			}
		}
		return possibleTotals;
	}

	boolean totalsOverLimit(ArrayList<Integer> arrayL, int limit) {
		for (int i = 0; i < arrayL.size(); i++) {
			if (arrayL.get(i) < limit)
				return false;
		}
		return true;
	}

	boolean playerWin() {
		for (int i = 0; i < myTotals.size(); i++) {
			int myTot = myTotals.get(i);

			for (int j = 0; j < dealerTotals.size(); j++) {
				int dTot = dealerTotals.get(j);

				if (dTot < myTot && myTot <= 21) {
					return true;
				}
			}
		}
		return false;
	}
}
