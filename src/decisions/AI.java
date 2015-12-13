package decisions;

import gui.GUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JOptionPane;

import objects.Card;

/**
 * Does decision making and handles the server. Does the bulk of the work.
 * 
 * @author Vince, Iain, Felix
 *
 */
public class AI {

	// Server communications
	private Socket server;
	private BufferedReader sRead;
	private PrintWriter sWrite;

	// Internal required objects
	private ActionSelector2 decision;
	private GUI gui;

	// Game variables
	private final static String NAME = "VinceFelixIainAI";
	private String action;
	private int myPlayerNumber;
	private int myCoins = 1000;
	private int betAmount;

	// Counting wins/losses
	private int myWins = 0;
	private int myLosses = 0;

	// Counts outcomes of each round
	private int busts = 0;
	private int perfects = 0;
	private int under = 0;

	// DEBUG
	private static final boolean DEBUG = false;
	private static final boolean SHOW_ALL_NETWORK_IO = false;
	private static final boolean AUTO_IP = true;
	private static final boolean SHOW_STATS = false;

	public static void main(String[] args) {
		System.out.println("===AI===");
		new AI();
	}

	/**
	 * Creates a new AI, asking the user for details.
	 */
	public AI() {
		String ip = "127.0.0.1", port = "1234";
		try {
			// Sets up user input
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			// Gets the IP of server
			System.out.println("What is the IP of the server?");
			ip = br.readLine();
			if (AUTO_IP) {
				ip = "127.0.0.1";
				System.out.println("IP set to 127.0.0.1.");
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
			if (AUTO_IP) {
				port = "1234";
				System.out.println("Port set to 1234.");
			}
			while (!port.matches("[0-9]*")) {
				System.out
						.println("This doesn't look like a valid port number. Try again.");
				ip = br.readLine();
			}

			br.close();
			br = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		new AI(ip, Integer.parseInt(port), null);
	}

	/**
	 * Sets up the AI with predetermined IP and port #, used by the GUI class.
	 * 
	 * @param ip
	 *            the IP of server
	 * @param port
	 *            the port # of the game
	 * @param g
	 *            the GUI (used to reference)
	 */
	public AI(String ip, int port, GUI g) {
		this.gui = g;
		try {
			// Sets up connection
			server = new Socket(ip, port);

			sRead = new BufferedReader(new InputStreamReader(
					server.getInputStream()));
			sWrite = new PrintWriter(server.getOutputStream());
		} catch (ConnectException e) {
			// Cannot connect to server
			JOptionPane.showConfirmDialog(null,
					"Are you sure the server is running?",
					"Cannot connect to server", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// more setup
		decision = new ActionSelector2();

		// Sends init message to server
		sendMessage(NAME + "\nPLAY");

		// If not accepted into game, will quit.
		if (!getNextLine().equals("% ACCEPTED")) {
			if (DEBUG)
				System.err.println("Server denied connection");
			return;
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

		// Updates the player number (for testing and tracking which AI)
		if (gui != null)
			gui.setPlayerNumber(myPlayerNumber);

		// More server init
		sendMessage("READY");
		waitUntilMatching("% START");

		// Reads messages from server and does things accordingly.
		while (true) {
			if (!actOnMessage())
				break;
		}
	}

	/**
	 * Reads a message from the server and does actions accordingly.
	 * 
	 * @return Whether or not the game has ended
	 */
	private boolean actOnMessage() {
		// Gets input from server
		String message = getNextLine();

		char firstCharacter = message.charAt(0);
		switch (firstCharacter) {
		case '#':
			// Card is dealt
			cardDealt(message);
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
				if (DEBUG)
					System.err.println("Kicked off by exclamation mark");
				return false;
			}
			break;

		case '*':
			// Bankruptcy... check if me.
			if (Integer.parseInt(message.split(" ")[1]) == myPlayerNumber) {
				if (DEBUG)
					System.err.println("Kicked off by asterisk");
				return false;
			}
			break;

		case '%':
			// Server command
			if (message.startsWith("% NEWROUND")) {
				initalizeForNewRound();
			} else if (message.equals("% " + myPlayerNumber + " turn"))
				runMyTurn();
			else if (message.startsWith("% SHUFFLE")) {
				decision.resetCardCounter();
			} else if (message.equals("% FORMATERROR")) {
				if (DEBUG)
					System.err.println("FORMATERROR from server");
				return false;
			}
			break;

		case '+':
			// Updated number of coins at end of round
			String[] updateCoins = message.split(" ");
			boolean stillPlaying = false;
			short newCoins = 0;
			// Finds the place in the string that is my player's coin value
			for (int i = 1; i < updateCoins.length; i += 2) {
				if (Integer.parseInt(updateCoins[i]) == myPlayerNumber) {
					stillPlaying = true;
					try {
						// Update coins
						newCoins = Short.parseShort(updateCoins[i + 1]);
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
					break;
				}
			}

			// Check for winning or losing the round
			if (newCoins > myCoins) {
				myWins++;
				if (DEBUG)
					System.out.println("Wins++");
			} else {
				myLosses++;
				if (DEBUG)
					System.out.println("Losses++");
			}

			// Updates the GUI as needed
			if (gui != null) {
				gui.updateWinLoss(myWins, myLosses, myCoins);
				gui.updateResultsDist("Unders = " + under + " Busts = " + busts
						+ " Blackjacks = " + perfects);
			}

			// Update coins, and show stats
			myCoins = newCoins;
			showStats();

			// If I get kicked off for some reason...
			if (!stillPlaying) {
				if (DEBUG)
					System.err.println("Game ended for some reason");
				return false;
			}
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * Sends any message to server. Manages all output to server in entire
	 * program
	 * 
	 * @param message
	 *            message to send to server
	 */
	private void sendMessage(String message) {
		sWrite.println(message);
		sWrite.flush();

		// DEBUG
		if (SHOW_ALL_NETWORK_IO)
			System.out.println("Sent to server: " + message);
	}

	/**
	 * Reads the next line from the server
	 * 
	 * @return
	 */
	private String getNextLine() {
		String message = "";
		try {
			message = sRead.readLine();
		} catch (IOException e) {
			// Can't connect to server.
			if (DEBUG)
				System.err.println("Connection to server failed.");
		}
		if (SHOW_ALL_NETWORK_IO) {
			// DEBUG
			System.out.println("Received from server: " + message);
		}

		// Gets the result of any turn, and records it down for stats tracking
		String[] result = message.split(" ");
		if (result[0].charAt(0) == '&'
				&& Integer.parseInt(result[1]) == myPlayerNumber) {
			if (result[2].equals("blackjack"))
				perfects++;
			else if (result[2].equals("bust"))
				busts++;
			else if (result[2].equals("stand"))
				under++;
		}
		return message;
	}

	/**
	 * Keeps reading lines from server input until it matches the given "head"
	 * 
	 * @param start
	 *            the String to wait for
	 */
	private void waitUntilMatching(String start) {
		String msg = "";
		while (true) {
			try {
				// Sets up a checkpoint to return to later.
				sRead.mark(100);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Gets next line and does comparison
			msg = getNextLine();
			if (msg.startsWith(start)) {
				try {
					sRead.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

	/**
	 * Resets the card counting for another round, and sends a bet
	 */
	private void initalizeForNewRound() {
		decision.resetHand();

		// Sends a bet amount
		betAmount = 10;

		sendMessage(betAmount + "");
		// Updates GUI.
		if (gui != null)
			gui.updateBetAmount(betAmount);
	}

	/**
	 * Does decision making and runs a turn.
	 */
	private void runMyTurn() {
		int move;

		// Checks if possible to double down.
		if (myCoins > 2 * betAmount) {
			move = decision.decideMove(true);
			if (move == ActionSelector.DOUBLE) {
				action = "Double Down";
				sendMessage("doubledown");
				if (gui != null)
					gui.updateAction(action);
				return;
			}
		} else
			move = decision.decideMove(false);

		// Last move is never "hit", so gets all the hits out of the way
		while (move == ActionSelector.HIT) {
			action = "Hit";
			if (gui != null)
				gui.updateAction(action);
			sendMessage("hit");
			// Gets the card and adds it to my hand
			// This next one is guaranteed to be a card input (hopefully)
			actOnMessage();
			String[] nlSplit = getNextLine().split(" ");
			if ((Integer.parseInt(nlSplit[1]) == myPlayerNumber)
			// The next line isn't a card input because the server does weird
			// stuff.
					&& (nlSplit[2].equals("bust")
							|| nlSplit[2].equals("blackjack") || nlSplit[2]
								.equals("doubledown")))
				return;

			move = decision.decideMove(false);
		}

		// Either a double down or a stand must be the last move.
		if (move == ActionSelector.STAND) {
			action = "Stand";
			sendMessage("stand");
			if (gui != null)
				gui.updateAction(action);
			return;
		}
	}

	/**
	 * Card has been shown by dealer
	 * 
	 * @param input
	 */
	private void cardDealt(String input) {
		// Gets the info about the card
		String[] dCard = input.split(" ");
		if (dCard[2].charAt(0) != 'X') {
			Card dealtCard = new Card(dCard[2].charAt(0));
			// If it's been dealt to me
			if (Integer.parseInt(dCard[1]) == myPlayerNumber) {
				decision.addToMyHand(dealtCard);
				if (gui != null)
					gui.updateMyCards(decision.getMyHand());
			}
			// Dealer's new card
			else if (Integer.parseInt(dCard[1]) == 0) {
				decision.setDealerCard(dealtCard);
				if (gui != null)
					gui.updateDealerCard(dealtCard);
			}
			// Generic other player
			else
				decision.cardPlayed(dealtCard);
		}
	}

	/**
	 * Prints out stats about how the game is progressing in the console
	 */
	private void showStats() {
		if (SHOW_STATS)
			System.out.println("\nWins = " + myWins + " Losses = " + myLosses
					+ "\nUnders = " + under + " Busts = " + busts
					+ " Blackjacks = " + perfects + "\nCoins = " + myCoins
					+ "\n" + ActionSelector.getThresholds());
	}

	/**
	 * @return the number of wins
	 */
	public int getWins() {
		return myWins;
	}

	/**
	 * @return the number of losses
	 */
	public int getLosses() {
		return myLosses;
	}

	/**
	 * @return String describing the current action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @return the ActionSelector
	 */
	public ActionSelector2 getDecisionMaker() {
		return decision;
	}

	/**
	 * @return the current number of coins
	 */
	public int getCoins() {
		return myCoins;
	}
}