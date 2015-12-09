import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUI extends JFrame {

	private AI ai;

	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(300, 200));
		this.setResizable(false);
		this.add(new MonitorPanel());
		this.pack();
		this.setVisible(true);
	}

	class MonitorPanel extends JPanel {
		public MonitorPanel() {
			this.setBackground(Color.WHITE);

			String ip = JOptionPane.showInputDialog(
					"Please enter the IP of the server", "127.0.0.1");
			while (!ip
					.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
				ip = JOptionPane.showInputDialog(
						"That doesn't look like a proper IPv4 address",
						"127.0.0.1");
			}

			String port = JOptionPane.showInputDialog(
					"Please enter the port of the server", "1234");
			while (!port.matches("[0-9]*"))
				port = JOptionPane.showInputDialog(
						"That doesn't look like a valid port", "1234");

			ai = new AI(ip, Integer.parseInt(port));
		}
	}

}
