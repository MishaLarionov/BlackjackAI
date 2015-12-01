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

	protected ArrayList<Integer> myCards = new ArrayList<Integer>();
	// Each index's possible value is 0-24
	protected int[] playedCards = new int[13];
	protected short myCoins;
	protected int dealerFaceUp;

	private ActionSelector decision;

	public static void main(String[] args) {
		new AI();
	}

	public AI() {
		// Asks user for IP of server, etc.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("What is the server's IP address? ");
		String ip = "";
		try {
			ip = br.readLine();
			System.out.println("");

			while (!ip
					.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
				System.out
						.print("That doesn't look like a valid IPv4 address.\nTry again: ");
				ip = br.readLine();
				System.out.println("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Gets port of game
		System.out.print("What port is the server operating on? ");
		String port = "";
		try {
			port = br.readLine();
			while (!port.matches("[0-9]*")) {
				System.out
						.println("That doesn't look like a valid port number.\nTry again: ");
				port = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Sets up connection
		try {
			server = new Socket(ip, Integer.parseInt(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			serverRead = new BufferedReader(new InputStreamReader(
					server.getInputStream()));
			serverWrite = new PrintWriter(server.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		decision = new ActionSelector(this);

		// Init connection w/ server
		serverWrite.println("player");
		serverWrite.flush();
		serverWrite.print("VinceFelixIainAI");
		serverWrite.flush();

		// Gets info about current player
		String introInfo = "";
		try {
			introInfo = serverRead.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int myPlayerNumber = Integer.parseInt(introInfo.substring(0,
				introInfo.indexOf(' ')));

		// Waits for game to start
		// They broadcast a bunch of useless stuff like
		// "this other player joined, etc."
		try {
			while (!serverRead.readLine().equals("START"))
				System.out.println("waiting for game to start");
			// For the  first "NEWROUND" message
			serverRead.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		myCoins = 1000;
		
	}

}
