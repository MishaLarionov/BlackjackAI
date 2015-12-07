import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class AI {

	private Socket server;
	private PrintWriter serverWrite;
	private BufferedReader serverRead;

	protected short myCoins;

	private ActionSelector decision;
	private int myPlayerNumber;
	private String USER_NAME = "VinceIainFelixAI";

	private final static boolean DEBUG = true;

	public static void main(String[] args) {
		try {
			new AI();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AI() throws IOException {
		// Asks user for IP of server, etc.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("What is the server's IP address? ");
		String ip = "";

		ip = br.readLine();
		if (DEBUG)
			ip = "10.242.166.206";
		System.out.println("");

		while (!ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
			System.out
					.print("That doesn't look like a valid IPv4 address.\nTry again: ");
			ip = br.readLine();
			System.out.println("");
		}

		// Gets port of game
		System.out.print("What port is the server operating on? ");
		String port = "";

		port = br.readLine();
		while (!port.matches("[0-9]*")) {
			System.out
					.println("That doesn't look like a valid port number.\nTry again: ");
			port = br.readLine();
		}

		br.close();
		br = null;

		// Sets up connection
		server = new Socket(ip, Integer.parseInt(port));

		serverRead = new BufferedReader(new InputStreamReader(
				server.getInputStream()));
		serverWrite = new PrintWriter(server.getOutputStream());

		decision = new ActionSelector();

		// Send name to server
		sendMessage(USER_NAME);

		// Tell server we want to be a player
		sendMessage("PLAY");

		// So apparently you can spam the server with requests to play every
		// second if you're not accepted right away. So that's what we're gonig
		// to do.
		/*
		 * // So apparently the following is just a feature request. Delete when
		 * done. while (!serverRead.readLine().equals("% ACCEPTED")) {
		 * serverWrite.println("PLAY"); serverWrite.flush(); try {
		 * Thread.sleep(1000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } }
		 */

		// Gets info about current player from server
		if (!getMessage().equals("% ACCEPTED")) {
			System.out.println("Failed to accept request to server.");
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}

		myPlayerNumber = Integer.parseInt(getMessage("@").split(" ")[1].trim());

		// Waits for game to start
		// They broadcast a bunch of useless stuff like
		// "this other player joined, etc."
		sendMessage("READY");
		getMessage("% START");
		myCoins = 1000;

		// TODO add support for multiple rounds
		while (true) {
			runRound(1001 - myCoins);
			if (getMessage().equals("% SHUFFLE"))
				decision.resetCardCounter();
		}
	}

	private void runRound(int betAmount) {
		// Waits for the server to start a new round
		getMessage("% NEWROUND");
		System.out.println("Round has started");
		decision.resetHand();

		// Tell server bet amount, and
		sendMessage(betAmount + "");

		// Server will broadcast bets by other players, so ignore that.

		// Takes all the cards that the server deals to all players
		ArrayList<String> bets = getAllMessages("#");
		for (int i = 0; i < bets.size(); i++) {
			// Gets the actual information
			String dealString = bets.get(i);
			String[] cardDeal = dealString.split(" ");
			int playerNum = Integer.parseInt(cardDeal[1]);
			char cardChar = cardDeal[2].charAt(0);
			int cardNum;

			// Protection against 'X' for the dealer's face down
			if (cardChar != 'X') {
				cardNum = parseCard(cardChar);

				// If it's mine, add it to my own hand of cards
				if (playerNum == myPlayerNumber) {
					decision.addToMyHand(new Card(cardNum));
				} else if (playerNum == 0) {
					decision.setDealerCard(new Card(cardNum));
				}

				// Adds it to the played cards for counting purposes
				decision.cardPlayed(new Card(cardNum));
			}
		}

		// Waits until my turn, collecting info about all cards in the process.
		String message = "";
		while (!message.equals("% " + myPlayerNumber + " turn")) {
			message = getMessage();
			if (message.startsWith("#"))
				decision.cardPlayed(new Card(parseCard(message.split(" ")[2]
						.charAt(0))));
		}

		runMyTurn();

		while (!message.startsWith("# 0")) {
			message = getMessage();
			if (message.startsWith("#"))
				decision.cardPlayed(new Card(parseCard(message.split(" ")[2]
						.charAt(0))));
		}

		ArrayList<String> dealerCards = getAllMessages("#");

		for (int i = 0; i < dealerCards.size(); i++) {
			decision.cardPlayed(new Card(Integer.parseInt(dealerCards.get(i)
					.split(" ")[2])));
		}

		String[] updateCoins = getMessage("+").split(" ");
		for (int i = 1; i < updateCoins.length; i += 2) {
			if (Integer.parseInt(updateCoins[i]) == myPlayerNumber) {
				try {
					myCoins = Short.parseShort(updateCoins[i + 1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private int parseCard(char cardChar) {
		int cardNum;
		cardChar = Character.toUpperCase(cardChar);
		// Special cases for "face" cards
		switch (cardChar) {
		case 'A':
			cardNum = 1;
			break;
		case 'T':
			cardNum = 10;
			break;
		case 'J':
			cardNum = 11;
			break;
		case 'Q':
			cardNum = 12;
			break;
		case 'K':
			cardNum = 13;
			break;
		default:
			cardNum = Integer.parseInt(cardChar + "");
			break;
		}
		return cardNum;
	}

	private void runMyTurn() {
		// "Hit" is never the last move; something always follows it
		int move = decision.decideMove(true);
		while (move == ActionSelector.HIT) {
			sendMessage("hit");

			decision.addToMyHand(new Card(parseCard(getMessage().split(" ")[2]
					.charAt(0))));

			move = decision.decideMove(false);
		}
		// Either a double down or a stand must be the last move.
		if (move == ActionSelector.DOUBLE) {
			sendMessage("doubledown");
		} else if (move == ActionSelector.STAND) {
			sendMessage("stand");
		}
	}

	private String getMessage(String regex) {
		String message = "";
		while (!message.startsWith(regex)) {
			try {
				message = serverRead.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (DEBUG) {
				System.out.println("Response from server: " + message);
			}
		}
		return message;
	}

	private String getMessage() {
		String message = "";
		try {
			message = serverRead.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (DEBUG) {
			System.out.println("Response from server: " + message);
		}
		return message;
	}

	private ArrayList<String> getAllMessages(String regex) {
		ArrayList<String> messages = new ArrayList<String>();
		String message = "";
		while (!message.startsWith(regex)) {
			message = getMessage();
		}
		while (message.startsWith(regex)) {
			messages.add(message);
			message = getMessage();
			// TODO find a way to get all the lines that starts with a certain
			// regex, but not the one after that.
		}
		return messages;
	}

	// TODO check if double down is possible based on amount of coins

	private void sendMessage(String message) {
		try {
			serverWrite.println(message);
			serverWrite.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("Sent message to server: " + message);
	}
}