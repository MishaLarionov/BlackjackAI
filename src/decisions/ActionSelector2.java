package decisions;

import java.util.ArrayList;

import objects.Card;
import objects.Hand;

public class ActionSelector2 {

	private Hand myHand;
	private int THRESHOLD = 17;

	public ActionSelector2() {
		return;
	}

	protected void resetCardCounter() {
		return;
	}

	protected int decideMove(boolean doubleDownAllowed) {
		ArrayList<Integer> totals = removeIrrelevant(myHand.recalcTotals());
		double average = averageOfTotal(totals);
		if (totals.contains(21))
			return ActionSelector.STAND;

		if (average < THRESHOLD)
			return ActionSelector.HIT;
		else
			return ActionSelector.STAND;
	}

	protected void resetHand() {
		myHand = new Hand();
	}

	protected void addToMyHand(Card newCard) {
		myHand.add(newCard);
	}

	protected Hand getMyHand() {
		return myHand;
	}

	protected void setDealerCard(Card newCard) {
		return;
	}

	protected void cardPlayed(Card newCard) {
		return;
	}

	private ArrayList<Integer> removeIrrelevant(ArrayList<Integer> totals) {
		for (int i = 0; i < totals.size(); i++) {
			if (totals.get(i) >= 21)
				totals.remove(i);
		}
		return totals;
	}

	private double averageOfTotal(ArrayList<Integer> totals) {
		double total = 0;
		for (Integer integer : totals) {
			total += integer;
		}
		total /= totals.size();
		return total;
	}
}
