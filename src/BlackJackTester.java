import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BlackJackTester {

	int[] remainingCards = new int[13];
	ArrayList<Integer> myHand = new ArrayList<Integer>();
	ArrayList<Integer> dHand = new ArrayList<Integer>();
	int dealerFaceUp;
	final int DECKS = 12;
	final int GAMES = 1280;

	int totalWins = 0;
	int totalLosses = 0;

	ArrayList<Integer> myTotals;
	ArrayList<Integer> dealerTotals;

	VinceRandomTester random;

	static final boolean AI = true;

	public static void main(String[] args) {
		try {
			new BlackJackTester(AI);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	BlackJackTester(boolean ai) throws IOException {
		for (int i = 0; i < GAMES; i++) {
			int[] temp = runSimulation(ai);
			totalWins += temp[0];
			totalLosses += temp[1];
		}

		System.out.println(totalWins + " " + totalLosses);
	}

	int[] runSimulation(boolean ai) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		for (int i = 0; i < remainingCards.length; i++) {
			remainingCards[i] = DECKS * 4;
		}

		int wins = 0;
		int losses = 0;

		if (ai) {
			random = new VinceRandomTester();
		}

		for (int i = 0; i < DECKS; i++) {
			myHand.add(randomCard());
			myHand.add(randomCard());

			dHand.add(randomCard());
			dHand.add(randomCard());
			dealerFaceUp = dHand.get(0);

			char action = (char) -1;
			int move = -1;

			myTotals = recalcTotals(myHand);
			System.out.println("Your cards are: " + myHand
					+ " and your possible totals are " + myTotals);

			System.out.println("The dealer is: " + dHand.get(0));
			System.out.println("What is your move? (h/s/d)");
			if (!ai)
				action = Character.toLowerCase(br.readLine().charAt(0));
			else {
				action = random.pickAction(myTotals);
			}

			while (action == 'h') {
				myHand.add(randomCard());
				myTotals = recalcTotals(myHand);
				if (!totalsOverLimit(myTotals, 21)) {
					System.out.println("Your cards are: " + myHand
							+ " and your possible totals are " + myTotals);
					System.out.println("What is your next move?");
					if (!ai)
						action = Character.toLowerCase(br.readLine().charAt(0));
					else
						action = random.pickAction(myTotals);
				} else {
					action = 'b';
				}

			}

			if (action == 'd') {
				myHand.add(randomCard());
				myTotals = recalcTotals(myHand);
			}
			dealerTotals = recalcTotals(dHand);

			while (!anyOverLimit(dealerTotals, 16)) {
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
				if (!totalsOverLimit(dealerTotals, 21))
					losses++;
				else
					System.out.println("Draw.");
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

		return new int[] { wins, losses };
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
					possibleTotals.add(possibleTotals.get(j) + 10);
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

	boolean anyOverLimit(ArrayList<Integer> arrayL, int limit) {
		for (int i = 0; i < arrayL.size(); i++) {
			if (arrayL.get(i) > limit)
				return true;
		}
		return false;
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
