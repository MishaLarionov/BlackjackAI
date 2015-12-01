import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AI {

	private final short PORT = 100;
	private Socket server;
	private PrintWriter serverWrite;
	private BufferedReader serverRead;

	public static void main(String[] args) {
		new AI();
	}

	public AI() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("What is the server's IP address? ");
		String ip = "";
		try {
			ip = br.readLine();
			System.out.println("");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (!ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
			System.out
					.print("That doesn't look like a valid IPv4 address.\nTry again: ");
			try {
				ip = br.readLine();
				System.out.println("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			server = new Socket(ip, PORT);
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

		serverWrite.print("VinceFelixIainAI");
	}

}
