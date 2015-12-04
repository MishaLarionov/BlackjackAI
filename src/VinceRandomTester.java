import java.util.ArrayList;

class VinceRandomTester {

	final static char STAND = 's';
	final static char HIT = 'h';

	VinceRandomTester() {

	}

	char pickAction(ArrayList<Integer> possTotals) {
		double average = 0;
		int values = 0;

		for (int i = 0; i < possTotals.size(); i++) {
			if (possTotals.get(i) == 21)
				return STAND;
			else if (possTotals.get(i) < 21) {
				average += possTotals.get(i);
				values++;
			}
		}

		average /= values;
		if (average >= 17)
			return STAND;
		else
			return HIT;
	}
}