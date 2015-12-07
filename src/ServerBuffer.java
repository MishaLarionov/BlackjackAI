import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Queue;

public class ServerBuffer {

	private BufferedReader br;
	private Queue<String> queue;

	public ServerBuffer(Socket server) {
		try {
			br = new BufferedReader(new InputStreamReader(server.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				queue.add(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
