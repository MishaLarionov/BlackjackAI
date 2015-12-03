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

	// 1 is Ace, 11 is Jack, 12 is Queen, 13 is King
	protected ArrayList<Integer> myHand = new ArrayList<Integer>();
	// Each index's possible value is 0-24
	protected int[] playedCards = new int[13];
	protected short myCoins;
	protected int dealerFaceUp;

	private ActionSelector decision;
	private int BET_AMOUNT = 1;
	private int myPlayerNumber;
	private String USER_NAME = "VinceFe%20lix\n\\#$ !*%@IainAI";

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

		decision = new ActionSelector(this);

		// Init connection w/ server
		serverWrite.println("PLAY");
		serverWrite.flush();

		// So apparently you can spam the server with requests to play every
		// second if you're not accepted right away. So that's what we're gonig
		// to do.
		while (!serverRead.readLine().equals("% ACCEPTED")) {
			serverWrite.println("PLAY");
			serverWrite.flush();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// When finally accepted into the lobby, gives them our name.
		serverWrite.print(USER_NAME);
		serverWrite.flush();

		// Gets info about current player from server
		myPlayerNumber = Integer.parseInt(serverRead.readLine().split(" ")[1]
				.trim());

		// Waits for game to start
		// They broadcast a bunch of useless stuff like
		// "this other player joined, etc."
		serverWrite.println("READY");
		serverWrite.flush();
		while (!serverRead.readLine().startsWith("% ST"))
			System.out
					.println("waiting for game to start, other players are joining");
		System.out.println("Game has started.");
		myCoins = 1000;

		// TODO add support for multiple rounds
		while (true) {
			runRound();
		}
	}

	private void runRound() throws IOException {
		// Waits for the server to start a new round
		while (!serverRead.readLine().equals("% NEWROUND"))
			System.out.println("Waiting for new round to start");
		System.out.println("Round has started");

		// Tell server bet amount, and
		serverWrite.println(BET_AMOUNT);
		serverWrite.flush();

		// Server will broadcast bets by other players, so ignore that.
		String bet = serverRead.readLine();
		while (bet.startsWith("$")) {
			System.out.println("Another bet placed by a player,"
					+ "still waiting for dealing to begin.");
			// The last "bet" that we read is actually a card deal, so we need
			// to accomodate for that.
			bet = serverRead.readLine();
		}

		// Takes all the cards that the server deals to all players
		String dealString = bet;
		while (dealString.charAt(0) == '#') {
			// Gets the actual information
			String[] cardDeal = dealString.split(" ");
			int playerNum = Integer.parseInt(cardDeal[1]);
			char cardChar = cardDeal[2].charAt(0);
			int cardNum;

			// Protection against 'X' for the dealer's face down
			if (cardChar != 'X') {
				cardNum = parseCard(cardChar);

				// If it's mine, add it to my own hand of cards
				if (playerNum == myPlayerNumber)
					myHand.add(cardNum);
				else if (playerNum == 0)
					dealerFaceUp = cardNum;

				// Adds it to the played cards for counting purposes
				decision.cardDealt(cardNum);
			}

			// Gets the next deal from server
			while (!serverRead.ready())
				System.out
						.println("Waiting for server to send out another deal");
			dealString = serverRead.readLine();
		}

		// Waits for my turn
		String turnStr = dealString;
		while (!turnStr.equals("% " + myPlayerNumber + " turn")) {
			System.out.println("Waiting for turn");
			turnStr = serverRead.readLine();
		}

		runMyTurn();

		while (!turnStr.startsWith("# 0")) {
			turnStr = serverRead.readLine();
			System.out.println("Waiting for the server to reveal their cards");
		}
		// TODO add padding between my move and the server's card revealing

		// Gets the face-down card and other cards that the dealer pulls
		String remainingCards = serverRead.readLine();
		while (remainingCards.startsWith("#")) {
			String[] rCards = remainingCards.split(" ");
			playedCards[Integer.parseInt(rCards[2])]++;

			if (serverRead.ready())
				remainingCards = serverRead.readLine();
		}

		String updateStr = turnStr;
		while (!updateStr.startsWith("+")) {
			updateStr = serverRead.readLine();
		}
		String[] updateCoins = updateStr.split(" ");
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

	private void runMyTurn() throws IOException {
		// "Hit" is never the last move; something always follows it
		int move = decision.decideFirstMove();
		while (move == ActionSelector.HIT) {
			serverWrite.println("hit");
			serverWrite.flush();
			System.out.println("Sent hit to server");

			decision.cardDealt(parseCard(serverRead.readLine().split(" ")[2]
					.charAt(0)));

			move = decision.decideFirstMove();
		}
		// Either a double down or a stand must be the last move.
		if (move == ActionSelector.DOUBLE) {
			serverWrite.println("doubledown");
			System.out.println("Sent doubledown to server");
		} else if (move == ActionSelector.STAND) {
			serverWrite.println("stand");
			System.out.println("Sent stand to server");
		}
		serverWrite.flush();
	}
}