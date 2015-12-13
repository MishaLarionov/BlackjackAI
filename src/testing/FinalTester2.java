package testing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import decisions.AI;
import decisions.ActionSelector2;

public class FinalTester2 {

	private static final double START_THRESH = 15;
	private static final double END_THRESH = 18;
	private static final double THRESH_DELTA = 0.25;

	private static final int GAMES = 25;

	public static void main(String[] args) throws IOException {
		FileWriter fw = new FileWriter(new File("output.txt"));

		for (double thresh = START_THRESH; thresh <= END_THRESH; thresh += THRESH_DELTA) {
			double wins = 0, losses = 0, rounds = 0;
			for (int i = 0; i < GAMES; i++) {
				ActionSelector2.setTHRESHOLD(thresh);
				AI ai = new AI("127.0.0.1", 1234, null);
				wins += ai.getWins();
				losses += ai.getLosses();
				rounds += wins + losses;
				System.out.println("finished game " + i + " of " + thresh);
			}
			wins /= GAMES;
			losses /= GAMES;
			rounds /= GAMES;
			String out = String.format("%.2f\t%.4f\t%.1f", thresh,
					(wins / losses), rounds);
			fw.write(out);
			System.out.println(out);
		}
		fw.close();

	}
}
