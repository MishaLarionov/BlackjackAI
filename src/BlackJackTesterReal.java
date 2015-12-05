import java.io.IOException;
import java.util.ArrayList;

public class BlackJackTesterReal {

	int[] remainingCards = new int[13];
	Hand myHand = new Hand();
	Hand dHand = new Hand();
	final int ROUNDS = 2560;

	ArrayList<Double> underThreshes = new ArrayList<Double>();
	ArrayList<Double> bustThreshes = new ArrayList<Double>();
	ArrayList<Double> totWins = new ArrayList<Double>();
	ArrayList<Double> totLosses = new ArrayList<Double>();
	ArrayList<Double> totTies = new ArrayList<Double>();

	ActionSelector selector;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		try {
			new BlackJackTesterReal();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - start + " millis");
	}

	BlackJackTesterReal() throws IOException {
		for (double underThresh = 0.15; underThresh < 0.75; underThresh += 0.04) {
			ActionSelector.UNDER_THRESH = underThresh;
			for (double bustThresh = 0.15; bustThresh < 0.5; bustThresh += 0.04) {
				ActionSelector.BUST_THRESH = bustThresh;
				int[] temp = runSimulation();
				double total = temp[0] + temp[1] + temp[2];
				underThreshes.add(underThresh);
				bustThreshes.add(bustThresh);
				totWins.add(temp[0] / total);
				totLosses.add(temp[1] / total);
				totTies.add(temp[2] / total);
			}

		}

		for (int i = 0; i < underThreshes.size(); i++) {
			System.out.printf("%.3f ", underThreshes.get(i));
			System.out.printf("%.3f ", bustThreshes.get(i) );
			System.out.printf("%.5f ", totWins.get(i));
			System.out.printf("%.5f ", totLosses.get(i));
			System.out.printf("%.5f\n", totTies.get(i));
		}
	}

	int[] runSimulation() throws IOException {
		for (int i = 0; i < remainingCards.length; i++) {
			remainingCards[i] = ROUNDS * 4;
		}

		int wins = 0;
		int losses = 0;
		int draws = 0;

		selector = new ActionSelector();

		for (int roundNo = 0; roundNo < (int) (ROUNDS / 4); roundNo++) {
			selector.resetHand();
			for (int j = 0; j < 2; j++) {
				Card dealt = randomCard();
				myHand.add(dealt);
				selector.addToMyHand(dealt);
			}

			for (int j = 0; j < 2; j++) {
				Card dealt = randomCard();
				dHand.add(dealt);
			}
			selector.setDealerCard(dHand.get(0));

			char action = (char) -1;
			int actionInt = -1;

			ArrayList<Integer> myTotal = myHand.recalcTotals();
			System.out.println("Your cards are: " + myHand
					+ " and your possible totals are " + myTotal);

			System.out.println("The dealer is: " + dHand.get(0));
			System.out.println("What is your move? (h/s/d)");

			actionInt = selector.decideMove(true);
			switch (actionInt) {
			case ActionSelector.DOUBLE:
				action = 'd';
				break;
			case ActionSelector.HIT:
				action = 'h';
				break;
			case ActionSelector.STAND:
				action = 's';
				break;
			}
			System.out.println(action);

			while (action == 'h') {
				Card randomCard = randomCard();
				myHand.add(randomCard);
				selector.addToMyHand(randomCard);
				myTotal = myHand.recalcTotals();
				if (!totalsOverLimit(myTotal, 21)) {
					System.out.println("Your cards are: " + myHand
							+ " and your possible totals are " + myTotal);
					System.out.println("What is your next move?");

					actionInt = selector.decideMove(false);
					switch (actionInt) {
					case ActionSelector.DOUBLE:
						action = 'd';
						break;
					case ActionSelector.HIT:
						action = 'h';
						break;
					case ActionSelector.STAND:
						action = 's';
						break;
					}
					System.out.println(action);

				} else {
					action = 'b';
				}

			}

			if (action == 'd') {
				myHand.add(randomCard());
				// TODO add something for double down so that when you win, it's
				// counted as two wins
			}

			while (!anyOverLimit(dHand.recalcTotals(), 16)) {
				dHand.add(randomCard());
			}

			System.out.println("Dealer's cards are: " + dHand
					+ " and their possible totals are " + dHand.recalcTotals());
			System.out.println("Your cards are: " + myHand
					+ " and your possible totals are " + myHand.recalcTotals());

			ArrayList<Integer> dealerTotal = dHand.recalcTotals();
			myTotal = myHand.recalcTotals();
			if (dealerTotal.contains(21)) {
				System.out.println("dealer blackjacked.");
				losses++;
			} else if (myTotal.contains(21)) {
				System.out.println("You blackjacked.");
				wins++;
				if (action == 'd')
					wins++;
			} else if (action == 'b') {
				System.out.println("You busted.");
				if (!totalsOverLimit(dealerTotal, 21))
					losses++;
				else {
					System.out.println("Draw.");
					draws++;
				}
			} else if (totalsOverLimit(dealerTotal, 21)) {
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
				if (action == 'd')
					wins++;
			} else {
				System.out.println("Dealer wins.");
				losses++;
			}

			System.out.println("Wins: " + wins + " Losses: " + losses + "\n\n");

			myHand = new Hand();
			dHand = new Hand();

			if (roundNo % 10 == 0)
				selector.resetCardCounter();
		}

		return new int[] { wins, losses, draws };
	}

	Card randomCard() {
		int value = 0;
		do {
			value = (int) (Math.random() * remainingCards.length - 1) + 1;
		} while (remainingCards[value] <= 0);

		remainingCards[value]--;
		return new Card(value);
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
		ArrayList<Integer> myTotal = myHand.recalcTotals();
		ArrayList<Integer> dealerTotal = dHand.recalcTotals();

		for (int i = 0; i < myTotal.size(); i++) {
			if (myTotal.get(i) > myMax && myTotal.get(i) <= 21)
				myMax = myTotal.get(i);
		}
		int dMax = 0;
		for (int i = 0; i < dealerTotal.size(); i++) {
			if (dealerTotal.get(i) > dMax && dealerTotal.get(i) <= 21)
				dMax = dealerTotal.get(i);
		}
		if (dMax >= myMax)
			return false;
		else
			return true;
	}
}
