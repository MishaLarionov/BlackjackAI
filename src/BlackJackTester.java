import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BlackJackTester {

	int[] remainingCards = new int[13];
	ArrayList<Integer> myHand = new ArrayList<Integer>();
	ArrayList<Integer> dHand = new ArrayList<Integer>();
	int dealerFaceUp;
	final int DECKS = 2560;

	ArrayList<Double> totWins = new ArrayList<Double>();
	ArrayList<Double> totLosses = new ArrayList<Double>();
	ArrayList<Double> totTies = new ArrayList<Double>();

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
		for (int thresh = 1; thresh < 21; thresh++) {
			VinceRandomTester.THRESHOLD = thresh;
			int[] temp = runSimulation(ai);
			double total = temp[0] + temp[1] + temp[2];
			totWins.add(temp[0] / total);
			totLosses.add(temp[1] / total);
			totTies.add(temp[2] / total);
		}

		for (int i = 0; i < totWins.size(); i++) {
			System.out.print((i + 1) + " ");
			System.out.print(totWins.get(i) + " ");
			System.out.print(totLosses.get(i) + " ");
			System.out.println(totTies.get(i));
		}
	}

	int[] runSimulation(boolean ai) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		for (int i = 0; i < remainingCards.length; i++) {
			remainingCards[i] = DECKS * 4;
		}

		int wins = 0;
		int losses = 0;
		int draws = 0;

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
				System.out.println(action);
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
					else {
						action = random.pickAction(myTotals);
						System.out.println(action);
					}
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
				else {
					System.out.println("Draw.");
					draws++;
				}
			} else if (totalsOverLimit(dealerTotals, 21)) {
				System.out.print("Dealer busted.");
				if (action == 'b') {
					System.out.println("Draw.");
					draws++;
				} else {
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

		return new int[] { wins, losses, draws };
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
		int myMax = 0;
		for (int i = 0; i < myTotals.size(); i++) {
			if (myTotals.get(i) > myMax && myTotals.get(i) <= 21)
				myMax = myTotals.get(i);
		}
		int dMax = 0;
		for (int i = 0; i < dealerTotals.size(); i++) {
			if (dealerTotals.get(i) > dMax && dealerTotals.get(i) <= 21)
				dMax = dealerTotals.get(i);
		}
		if (dMax >= myMax)
			return false;
		else
			return true;
	}
}
