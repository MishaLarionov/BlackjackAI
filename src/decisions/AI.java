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

public class AI {

	private Socket server;
	private BufferedReader sRead;
	private PrintWriter sWrite;

	private ActionSelector decision;
	private GUI gui;

	private final static String NAME = "VinceFelixIainAI";
	private String action;
	private int myPlayerNumber;
	private int myCoins = 1000;
	private int betAmount;

	private int myWins = 0;
	private int myLosses = 0;

	private int busts = 0;
	private int perfects = 0;
	private int under = 0;

	private static final boolean DEBUG = false;
	private static final boolean SHOW_ALL_NETWORK_IO = false;
	private static final boolean AUTO_IP = true;
	private static final boolean SHOW_STATS = false;

	public static void main(String[] args) {
		System.out.println("===AI===");
		new AI();
	}

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

	public AI(String ip, int port, GUI g) {
		this.gui = g;
		try {
			// Sets up connection
			server = new Socket(ip, port);

			sRead = new BufferedReader(new InputStreamReader(
					server.getInputStream()));
			sWrite = new PrintWriter(server.getOutputStream());
		} catch (ConnectException e) {
			JOptionPane.showConfirmDialog(null,
					"Are you sure the server is running?",
					"Cannot connect to server", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// more setup
		decision = new ActionSelector();

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

		if (gui != null)
			gui.setPlayerNumber(myPlayerNumber);

		sendMessage("READY");
		waitUntilMatching("% START");

		while (true) {
			if (!actOnMessage())
				break;
		}
	}

	private boolean actOnMessage() {
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
			// Bankruptcy... check if me?
			if (Integer.parseInt(message.split(" ")[1]) == myPlayerNumber) {
				if (DEBUG)
					System.err.println("Kicked off by asterisk");
				return false;
			}
			break;

		case '%':
			// Server command
			if (message.startsWith("% NEWROUND")) {
				resetForNewRound();
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
			for (int i = 1; i < updateCoins.length; i += 2) {
				if (Integer.parseInt(updateCoins[i]) == myPlayerNumber) {
					stillPlaying = true;
					try {
						newCoins = Short.parseShort(updateCoins[i + 1]);
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
					break;
				}
			}

			if (newCoins > myCoins) {
				myWins++;
				if (DEBUG)
					System.out.println("Wins++");
			} else {
				myLosses++;
				if (DEBUG)
					System.out.println("Losses++");
			}
			if (gui != null) {
				gui.updateWinLoss(myWins, myLosses, myCoins);
				gui.updateResultsDist("Unders = " + under + " Busts = " + busts
						+ " Blackjacks = " + perfects);
			}
			myCoins = newCoins;
			showStats();

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

	private void sendMessage(String message) {
		sWrite.println(message);
		sWrite.flush();

		if (SHOW_ALL_NETWORK_IO)
			System.out.println("Sent to server: " + message);
	}

	private String getNextLine() {
		String message = "";
		try {
			message = sRead.readLine();
		} catch (IOException e) {
			if (DEBUG)
				System.err.println("Connection to server failed.");
		}
		if (SHOW_ALL_NETWORK_IO) {
			System.out.println("Received from server: " + message);
		}
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

		betAmount = 10;

		sendMessage(betAmount + "");
		if (gui != null)
			gui.updateBetAmount(betAmount);
	}

	private void runMyTurn() {
		int move;

		// If our number of coins is less than double the bet amount, don't
		// allow doubling down.
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

	private void cardDealt(String input) {
		String[] dCard = input.split(" ");
		if (dCard[2].charAt(0) != 'X') {
			Card dealtCard = new Card(dCard[2].charAt(0));
			if (Integer.parseInt(dCard[1]) == myPlayerNumber) {
				decision.addToMyHand(dealtCard);
				if (gui != null)
					gui.updateMyCards(decision.getMyHand());
			} else if (Integer.parseInt(dCard[1]) == 0) {
				decision.setDealerCard(dealtCard);
				if (gui != null)
					gui.updateDealerCard(dealtCard);
			} else
				decision.cardPlayed(dealtCard);
		}
	}

	private void showStats() {
		if (SHOW_STATS)
			System.out.println("\nWins = " + myWins + " Losses = " + myLosses
					+ "\nUnders = " + under + " Busts = " + busts
					+ " Blackjacks = " + perfects + "\nCoins = " + myCoins
					+ "\n" + ActionSelector.getThresholds());
	}

	public int getWins() {
		return myWins;
	}

	public int getLosses() {
		return myLosses;
	}

	public String getAction() {
		return action;
	}

	public ActionSelector getDecisionMaker() {
		return decision;
	}

	public int getCoins() {
		return myCoins;
	}
}