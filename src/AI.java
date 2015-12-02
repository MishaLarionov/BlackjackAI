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
	protected ArrayList<Integer> myCards = new ArrayList<Integer>();
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

		// Sets up connection

		server = new Socket(ip, Integer.parseInt(port));

		serverRead = new BufferedReader(new InputStreamReader(
				server.getInputStream()));
		serverWrite = new PrintWriter(server.getOutputStream());

		decision = new ActionSelector(this);

		// Init connection w/ server
		serverWrite.println("PLAY");
		serverWrite.flush();
		while (!serverRead.readLine().equals("% ACCEPTED")) {
			serverWrite.println("PLAY");
			serverWrite.flush();
			try {
				Thread.sleep(999);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		serverWrite.print(USER_NAME);
		serverWrite.flush();

		// Gets info about current player
		String introInfo = "";

		introInfo = serverRead.readLine();

		myPlayerNumber = Integer.parseInt(introInfo.split(" ")[1]);
		
		// Waits for game to start
		// They broadcast a bunch of useless stuff like
		// "this other player joined, etc."

		serverWrite.println("READY");
		while (!serverRead.readLine().startsWith("% ST"))
			System.out
					.println("waiting for game to start, other players are joining");
		System.out.println("Game has started.");
		myCoins = 1000;

		// First round starts, init
		// TODO add support for multiple rounds
		while (true) {
			while (!serverRead.readLine().equals("% NEWROUND"))
				System.out.println("Waiting for new round to start");
			System.out.println("Round has started");
			serverWrite.println(BET_AMOUNT);
			while (serverRead.readLine().startsWith("#"))
				serverRead.readLine();

			// Takes all the cards that the server deals to all players
			String dealString = serverRead.readLine();
			while (!dealString.equals("") && dealString != null) {
				// Gets the actual information
				String[] cardDeal = dealString.split(" ");
				int playerNum = Integer.parseInt(cardDeal[1]);
				char cardChar = cardDeal[2].charAt(0);
				int cardNum;

				// Protection against 'X' for the dealer's face down
				if (cardChar != 'X') {
					// Special cases for cards
					if (cardChar == 'A')
						cardNum = 1;
					else if (cardChar == 'J')
						cardNum = 11;
					else if (cardChar == 'Q')
						cardNum = 12;
					else if (cardChar == 'K')
						cardNum = 13;
					else
						cardNum = (int) cardChar;

					// Adds it to my own hand of cards
					if (playerNum == myPlayerNumber)
						myCards.add(cardNum);
					else if (playerNum == 0)
						dealerFaceUp = cardNum;

					// Adds it to the played cards for counting purposes
					playedCards[cardNum]++;
				}
				dealString = serverRead.readLine();
			}

			while (!serverRead.readLine().equals(
					"% " + myPlayerNumber + " turn"))
				System.out.println("Waiting for turn");

			// get the action to do here

			// Gets the face-down card and other cards that the dealer pulls
			String remainingCards = serverRead.readLine();
			while (remainingCards.startsWith("#")) {
				String[] rCards = remainingCards.split(" ");
				playedCards[Integer.parseInt(rCards[2])]++;

				remainingCards = serverRead.readLine();
			}

			// More stuff?
		}

	}
}