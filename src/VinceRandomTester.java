import java.util.ArrayList;

class VinceRandomTester {

	final static char STAND = 's';
	final static char HIT = 'h';
	
	final static int THRESHOLD = 14;
	/*
	 * THRESHOLD (win, loss):
	 * 1- 46.8, 53.1
	 * 2- 46.7, 53.2
	 * 3- 46.7, 53.2
	 * 4- 46.6, 53.3
	 * 5- 46.8, 53.1
	 * 6- 46.8, 53.1
	 * 7- 46.9, 53.0
	 * 8- 47.4, 52.5
	 * 9- 49.2, 50.7
	 * 10- 49.8, 50.1
	 * 11- 52.2, 47.7
	 * 12- 53.4, 46.5
	 * 13- 55.1, 44.8
	 * 14- 55.4, 44.5
	 * 15- 54.3, 45.6
	 * 16- 53.3, 46.6
	 * 17- 51.4, 48.5
	 * 18- 49.2, 50.7
	 * 19- 44.5, 55.4
	 * 20- 36.5, 63.4
	 * 21- 24.3, 75.6
	 */

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
		if (average >= THRESHOLD)
			return STAND;
		else
			return HIT;
	}
}