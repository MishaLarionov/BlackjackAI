import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

class AI {

	private Socket server;
	private BufferedReader sRead;
	private PrintWriter sWrite;

	private ActionSelector decision;

	private final static String NAME = "VinceFelixIainAI";
	private int myPlayerNumber;
	private int myCoins = 1000;
	private int betAmount;

	private int wins = 0;
	private int losses = 0;
	
	private static final boolean DEBUG = true;

	public static void main(String[] args) {
		System.out.println("===AI===");
		new AI();
	}

	public AI() {
		// Sets up user input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String ip;
		String port;
		try {
			// Gets the IP of server
			System.out.println("What is the IP of the server?");
			ip = br.readLine();
			if (DEBUG) {
				ip = "127.0.0.1";
			}
			while (!ip
					.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
				System.out
						.println("This doesn't look like a valid IPv4 adddress. Try again.");
				ip = br.readLine();
			}

			// Gets the port # of server
			System.out.println("\nWhat is the port number?");
			port = br.readLine();
			while (!port.matches("[0-9]*")) {
				System.out
						.println("This doesn't look like a valid port number. Try again.");
				ip = br.readLine();
			}

			br.close();
			br = null;

			// Sets up connection
			server = new Socket(ip, Integer.parseInt(port));

			sRead = new BufferedReader(new InputStreamReader(
					server.getInputStream()));
			sWrite = new PrintWriter(server.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// more setup
		decision = new ActionSelector();

		// Sends init message to server
		sendMessage(NAME + "\nPLAY");

		// If not accepted into game, will quit.
		if (!getNextLine().equals("% ACCEPTED")) {
			System.out
					.println("Server denied connection (possibly full room?). AI quitting.");
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}

		// waitUntilMatching("@");
		try {
			myPlayerNumber = Integer.parseInt(getNextLine().split(" ")[1]
					.trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		if (DEBUG) {
			System.out.println(myPlayerNumber);
		}

		sendMessage("READY");
		waitUntilMatching("% START");

		while (true) {
			actOnMessage();
		}
	}

	private void actOnMessage() {
		String message = getNextLine();

		char firstCharacter = message.charAt(0);
		switch (firstCharacter) {
		case '#':
			// Card is dealt
			cardDealt(message);
//			System.out.println("Card dealt");
			break;

		/*
		 * case '$': // Do nothing (other players have bet) break;
		 * 
		 * case '&': // Do nothing (results for each player after their turn)
		 * break;
		 * 
		 * case '@': // New player... safely ignore break;
		 */

		case '!':
			// Time out, check if it's me.
			if (Integer.parseInt(message.split(" ")[1]) == myPlayerNumber) {
				stopAI("Kicked off");
			}
			break;

		case '*':
			// Bankruptcy... check if me?
			if (Integer.parseInt(message.split(" ")[1]) == myPlayerNumber) {
				stopAI("Bankrupt.");
			}
			break;

		case '%':
			// Server command
			if (message.startsWith("% NEWROUND")) {
				resetForNewRound();
//				System.out.println("New round started");
			} else if (message.equals("% " + myPlayerNumber + " turn"))
				runMyTurn();
			else if (message.startsWith("% SHUFFLE")) {
				decision.resetCardCounter();
//				System.out.println("Shuffling");
			} else if (message.equals("% FORMATERROR")) {
				stopAI("Error from server");
			}
			break;

		case '+':
			// Updated number of coins at end of round
			String[] updateCoins = message.split(" ");
			boolean stillPlaying = false;
			for (int i = 1; i < updateCoins.length; i += 2) {
				if (Integer.parseInt(updateCoins[i]) == myPlayerNumber) {
					stillPlaying = true;
					short newCoins = 0;
					try {
						newCoins = Short.parseShort(updateCoins[i + 1]);
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
					if (newCoins > myCoins)
						wins++;
					else
						losses++;
					myCoins = newCoins;
				}
			}
			if (DEBUG)
				System.out.println("Coins: " + myCoins);

			if (!stillPlaying) {
				stopAI("Game ended for some reason");
			}
			break;

		default:
			break;
		}
	}

	private void sendMessage(String message) {
		sWrite.println(message);
		sWrite.flush();

		if (DEBUG)
			System.out.println("Sent to server: " + message);
	}

	private String getNextLine() {
		String message = "";
		try {
			message = sRead.readLine();
		} catch (SocketException e) {
			stopAI("Connection to server failed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (DEBUG) {
			System.out.println("Received from server: " + message);
//			System.out.println("\t"
//					+ Thread.currentThread().getStackTrace()[2].toString());
		}
		return message;
	}

	private boolean waitUntilMatching(String start) {
		String msg = "";
		while (true) {
			try {
				sRead.mark(100);
			} catch (IOException e) {
				e.printStackTrace();
			}
			msg = getNextLine();
			if (msg.startsWith(start)) {
				try {
					sRead.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
	}

	private void resetForNewRound() {
		decision.resetHand();
		betAmount = (int) ((1000 - myCoins) / 3.2);

		if (betAmount >= myCoins) {
			if (myCoins < 100)
			betAmount = (int) (myCoins / 2);
			else
				betAmount = (int) (myCoins / 7);
		}
		if (betAmount < 10) {
			betAmount = 10;
		}
		sendMessage(betAmount + "");
	}

	private void runMyTurn() {
		int move;

		// If our number of coins is less than double the bet amount, don't
		// allow doubling down.
		if (myCoins > 2 * betAmount)
			move = decision.decideMove(true);
		else
			move = decision.decideMove(false);

		// Last move is never "hit", so gets all the hits out of the way
		while (move == ActionSelector.HIT) {
			sendMessage("hit");
			// Gets the card and adds it to my hand
			// This next one is guaranteed to be a card input (hopefully)
			actOnMessage();
			String[] nlSplit = getNextLine().split(" ");
			if (Integer.parseInt(nlSplit[1]) == myPlayerNumber
					&& (nlSplit[2].equals("bust") || nlSplit[2].equals("blackjack")))
				return;
			move = decision.decideMove(false);
		}

		// Either a double down or a stand must be the last move.
		if (move == ActionSelector.DOUBLE) {
			sendMessage("doubledown");
		} else if (move == ActionSelector.STAND) {
			sendMessage("stand");
		}
	}

	private void cardDealt(String input) {
		String[] dCard = input.split(" ");
		if (dCard[2].charAt(0) != 'X')
			decision.cardPlayed(new Card(dCard[2].charAt(0)));
	}

	private void stopAI(String message) {
		System.err.println(message + "\nWins = " + wins + "\nLosses = "
				+ losses);
		try {
			sRead.close();
			sWrite.close();
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}