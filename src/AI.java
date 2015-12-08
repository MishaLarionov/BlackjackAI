import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * AI that plays Blackjack against a networked server
 * 
 * @author Vince, Iain, Felix
 */
public class AI {

	// Server communications
	private Socket server;
	private PrintWriter serverWrite;
	private BufferedReader serverRead;

	// Keeps track of coins & wins
	private short myCoins;
	private short betAmount;
	private int wins;
	private int losses;

	// Blackjack playing stuff
	private ActionSelector decision;
	private int myPlayerNumber;
	private String USER_NAME = "VinceIainFelixAI";

	final static boolean DEBUG = true;

	public static void main(String[] args) {
		System.out.println("===AI===");
		try {
			new AI();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The instantiatable AI.
	 * 
	 * @throws IOException
	 */
	public AI() throws IOException {
		// Asks user for IP of server, etc.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("What is the server's IP address? ");
		String ip = "";
		ip = br.readLine();
		System.out.println("");
		if (DEBUG) {
			ip = "127.0.0.1";
		}
		while (!ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
			System.out
					.print("That doesn't look like a valid IPv4 address.\nTry again: ");
			ip = br.readLine();
			System.out.println("");
		}

		// Gets server's port
		System.out.print("What port is the server operating on? ");
		String port = "";
		port = br.readLine();
		if (DEBUG) {
			port = "1234";
		}
		while (!port.matches("[0-9]*")) {
			System.out
					.println("That doesn't look like a valid port number.\nTry again: ");
			port = br.readLine();
		}
		br.close();
		br = null;

		// Sets up connection and Selectors.
		server = new Socket(ip, Integer.parseInt(port));
		serverRead = new BufferedReader(new InputStreamReader(
				server.getInputStream()));
		serverWrite = new PrintWriter(server.getOutputStream());
		decision = new ActionSelector();

		// Send name and intentions to server
		sendMessage(USER_NAME + "\n" + "PLAY");

		// Checks connection
		try {
			if (!getMessage().equals("% ACCEPTED")) {
				System.out.println("Failed to accept request to server.");
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		} catch (NewRoundException e) {
			System.err.println("Something went wrong");
		}

		// Gets my player number (to keep track for when the game alternates
		// turns later
		try {
			myPlayerNumber = Integer.parseInt(getMessage("@").split(" ")[1]
					.trim());
		} catch (NumberFormatException | NewRoundException e) {
			System.err.println("Something went wrong");
		}

		// Waits for game to start, initializes stuff.
		sendMessage("READY");
		try {
			getMessage("% START");
		} catch (NewRoundException e) {
			System.err.println("Something went wrong");
		}
		myCoins = 1000;

		// TODO add support for multiple rounds
		// Each iteration of the while loop is one round.
		// We don't exactly know when the game ends because the server group
		// hasn't decided on that yet.
		while (true) {
			// Runs the round, and determines what to do accordingly.
			betAmount = (short) (1010 - myCoins);
			if (betAmount < 10 || betAmount >= myCoins) {
				betAmount = 15;
			}

			serverRead.mark(75);
			String msg = "";
			try {
				msg = getMessage();
			} catch (NewRoundException e) {
				System.out.println("New round!");
			}
			if (msg.equals("% SHUFFLE"))
				decision.resetCardCounter();
			else
				serverRead.reset();

			runRound();
		}
	}

	/**
	 * Runs the functions for one round of Blackjack
	 */
	private void runRound() {
		// Waits for the server to start a new round
		try {
			getMessage("% NEWROUND");
		} catch (NewRoundException e4) {
			System.out.println("New round started");
		}
		decision.resetHand();

		// Tell server bet amount, and
		sendMessage(betAmount + "");

		// Server will broadcast bets by other players, so ignore that.

		// Takes all the cards that the server deals to all players
		ArrayList<String> bets;
		try {
			bets = getAllMessages("#");
		} catch (NewRoundException e3) {
			return;
		}
		for (int i = 0; i < bets.size(); i++) {
			// Gets the actual information about cards
			String[] cardDeal = bets.get(i).split(" ");
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
			try {
				message = getMessage();
			} catch (NewRoundException e) {
				System.err.println("Whoops new round!");
				return;
			}
			// If it's a card, intercept it, and add it to the card counting.
			if (message.startsWith("#"))
				decision.cardPlayed(new Card(parseCard(message.split(" ")[2]
						.charAt(0))));
		}

		// Runs my decision making process
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
			String newCard;
			try {
				newCard = getMessage();
			} catch (NewRoundException e) {
				return;
			}
			decision.addToMyHand(new Card(parseCard(newCard.split(" ")[2]
					.charAt(0))));
			move = decision.decideMove(false);
		}

		// Either a double down or a stand must be the last move.
		if (move == ActionSelector.DOUBLE) {
			sendMessage("doubledown");
		} else if (move == ActionSelector.STAND) {
			sendMessage("stand");
		}
		// Waits until the dealer reveals their cards
		while (!message.startsWith("# 0")) {
			try {
				message = getMessage();
			} catch (NewRoundException e) {
				return;
			}
			if (message.startsWith("#"))
				decision.cardPlayed(new Card(parseCard(message.split(" ")[2]
						.charAt(0))));
		}

		// Intentional "overrun" of counter to read over useless line.

		// Adds dealer's cards to the card counting database
		try {
			serverRead.mark(75);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String dCardsTemp;
		try {
			dCardsTemp = getMessage();
		} catch (NewRoundException e2) {
			return;
		}
		if (dCardsTemp.startsWith("#")) {
			try {
				serverRead.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<String> dealerCards;
			try {
				dealerCards = getAllMessages("#");
			} catch (NewRoundException e) {
				return;
			}
			for (int i = 0; i < dealerCards.size(); i++) {
				decision.cardPlayed(new Card(parseCard(dealerCards.get(i)
						.split(" ")[2].charAt(0))));
			}
		} else
			try {
				serverRead.reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		// Updates the current amount of coins.
		String[] updateCoins;
		try {
			updateCoins = getMessage("+").split(" ");
		} catch (NewRoundException e1) {
			return;
		}
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
		if (!stillPlaying) {
			System.out.println("Game has ended for some reason.");
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	/**
	 * Takes the "server character" of a card and turns it into a numerical
	 * value (1-13)
	 * 
	 * @param cardChar
	 *            the original card code by the server
	 * @return the numerical value of the card
	 */
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

		// Not face cards
		default:
			cardNum = Integer.parseInt(cardChar + "");
			break;
		}
		return cardNum;
	}

	/**
	 * Ignores all other messages and gets the first message that starts with
	 * the regular expression
	 * 
	 * @param regex
	 *            the regex that the target string starts with
	 * @return the target string
	 */
	private String getMessage(String regex) throws NewRoundException {
		String message = "";

		// Keeps iterating through the stream until it reaches the desired
		// message
		while (!message.startsWith(regex)) {
			message = getMessage();
		}
		// Returns the message
		return message;
	}

	/**
	 * Gets the next string in the buffer
	 * 
	 * @return the target string
	 * @throws NewRoundException
	 */
	private String getMessage() throws NewRoundException {
		String message = "";
		// Gets the message
		try {
			message = serverRead.readLine();
		} catch (SocketException e) {
			System.err.println("Connection to server failed. Quitting.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (DEBUG) {
			System.out.println("Response from server: " + message);
		}

		if (message.equals("! " + myPlayerNumber)) {
			System.err
					.println("You have likely lost or been disconnected. Quitting.");
			System.exit(0);
		} else if (message.equals("% NEWROUND")) {
			throw new NewRoundException(
					Thread.currentThread().getStackTrace()[2].toString());
		}
		return message;
	}

	/**
	 * Ignores all strings until the first one is found starting with the regex,
	 * Then returns an ArrayList of all the Strings following it that also start
	 * with the regex
	 * 
	 * @param regex
	 *            the regex the target starts with
	 * @return ArrayList of strings that starts with the regex
	 */
	private ArrayList<String> getAllMessages(String regex)
			throws NewRoundException {
		ArrayList<String> messages = new ArrayList<String>();
		// For IOExceptions.
		try {
			String message = "";
			// Skips all other strings before the target
			while (!message.startsWith(regex)) {
				message = getMessage();
			}
			// Gets all target strings
			while (message.startsWith(regex)) {
				messages.add(message);
				serverRead.mark(100);
				message = getMessage();
			}
			// Moves the cursor back to the position before that line
			serverRead.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Duh.
		return messages;
	}

	/**
	 * Sends a string to the server. Saves two lines of print, then flush.
	 * 
	 * @param message
	 */
	private void sendMessage(String message) {
		// Pretty self explanatory
		try {
			serverWrite.println(message);
			serverWrite.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("Sent message to server: " + message);
	}

	class NewRoundException extends Exception {
		public NewRoundException(String methodName) {
			System.out.println(methodName);
		}
	}
}