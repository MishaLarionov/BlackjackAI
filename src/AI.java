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
		serverWrite.println("player");
		serverWrite.flush();
		serverWrite.print("VinceFelix\\#$!*%@IainAI");
		serverWrite.flush();

		// Gets info about current player
		String introInfo = "";

		introInfo = serverRead.readLine();

		int myPlayerNumber = Integer.parseInt(introInfo.substring(
				introInfo.indexOf(' '), introInfo.indexOf(' ', 1)));

		// Waits for game to start
		// They broadcast a bunch of useless stuff like
		// "this other player joined, etc."

		serverWrite.println("READY");
		while (!serverRead.readLine().equals("% START"))
			System.out.println("waiting for game to start");
		// For the first "NEWROUND" message
		myCoins = 1000;

		// First round starts
		while (!serverRead.readLine().equals("% NEWROUND"))
			System.out.println("Waiting for new round to start");
		serverWrite.println(BET_AMOUNT);
		while (serverRead.readLine().startsWith("#"))
			serverRead.readLine();
	}

}
