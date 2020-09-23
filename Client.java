import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
public class Client {
	private static Socket socket = null;
	public static boolean activateSocket(JTextField IPField, int port, JTextArea communicationField) {
		try {
			socket = new Socket(IPField.getText(), port);
			communicationField.append("Connected." + "\n");
			return true;
		}catch (UnknownHostException u) {
			communicationField.append("Error: Unknown Host" + "\n");
			socket = null;
			return false;
		}catch (IOException i) {
			communicationField.append("Error: IO Exception" + "\n");
			socket = null;
			return false;
		}

	}
	public static void deactivateSocket(JTextArea communicationField) {
		try {
			communicationField.append("Disconnected" + "\n");
			socket.close();
			socket = null;
		}catch(IOException i) {
			communicationField.append("IO Exception" + "\n");
			socket = null;
		}
	}
	public static void main(String[] args) {
		int port = 5521;
		JFrame frame = new JFrame();
		frame.setTitle("Program 4 - File Uploader");
		frame.setLayout(new BorderLayout());
		frame.setPreferredSize(new Dimension(1000, 600));
		frame.setMaximumSize(new Dimension(1000, 600));
		frame.setMinimumSize(new Dimension(1000, 600));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel IPLabel = new JLabel("IP Address: ");
		JTextField IPField = new JTextField("127.0.0.1");
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(IPLabel);
		panel.add(IPField);
		panel.setVisible(true);
		frame.add(panel, BorderLayout.WEST);
		
		JButton connectButton = new JButton("Connect and Upload");
		JPanel panel3 = new JPanel(new BorderLayout());
		panel3.add(connectButton, BorderLayout.CENTER);
		panel3.setVisible(true);
		frame.add(panel3, BorderLayout.NORTH);
		
		JLabel communicationLabel = new JLabel("Error Messages:");
		JTextArea communicationField = new JTextArea();
		JPanel panel5 = new JPanel(new BorderLayout());
		panel5.add(communicationLabel,BorderLayout.WEST);
		panel5.add(communicationField, BorderLayout.CENTER);
		communicationField.setVisible(true);
		panel5.setVisible(true);
		frame.add(panel5, BorderLayout.CENTER);
		
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("MSWord", "doc", "docx");
		chooser.setFileFilter(filter);
		JPanel panel6 = new JPanel(new BorderLayout());
		panel6.add(chooser, BorderLayout.CENTER);
		panel6.setVisible(true);
		frame.add(panel6, BorderLayout.SOUTH);
		frame.setVisible(true);
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent connect) {
				boolean check = activateSocket(IPField, port, communicationField);
				if(!check) {
					return;
				}
				try {
					File file = chooser.getSelectedFile();
					String name = file.getName();
					String path = file.getPath();
					long length = file.length();
					String size = String.valueOf(length);
					sendNullTerminatedString(name, communicationField);
					communicationField.append("Sent file name: " + name +"\n");
					sendNullTerminatedString(size, communicationField);
					communicationField.append("Sent file length: " + size +"\n");
					sendFile(path, communicationField);
					communicationField.append("File sent. Waiting for the Server...." + "\n");
					if(socket.getInputStream().read() == (int)'@') {
						communicationField.append("Upload O.K." + "\n");
					}else {
						communicationField.append("Error: Server did not return '@'." + "\n");
					}
					deactivateSocket(communicationField);
				}catch(IOException IOE) {
					communicationField.append("Error: IO Exception Occurred During Character Read From Server." + "\n");
				}catch(NullPointerException NPE) {
					communicationField.append("Error: Null Pointer Exception Occurred During File Find." + "\n");
				}
			}
		});
	}
	private static void sendNullTerminatedString(String s, JTextArea field) {
		byte[] line = s.getBytes();
		try {
			socket.getOutputStream().write(line);
			socket.getOutputStream().write((int) '\0');
		}catch(IOException IOE) {
			field.append("Error: IO Exception Occurred During String Write." + "\n");
		}catch(NullPointerException NPE) {
			field.append("Error: Null Pointer Exception Occurred During String Write." + "\n");
		}
	}
	private static void sendFile(String fullPathFileName, JTextArea field) {
		File file = new File(fullPathFileName);
		byte[] chunk = new byte[1024];
		try {
			FileInputStream fis = new FileInputStream(file);
			int temp = 0;
			while((temp = fis.read(chunk)) > 0) {
				socket.getOutputStream().write(chunk);
			}
			socket.getOutputStream().write((int) '\0');
			fis.close();
		}catch(FileNotFoundException FNFE) {
			field.append("Error: File Not Found." + "\n");
		}catch(IOException IOE) {
			field.append("Error: IO Exception Occurred During Chunk Write." + "\n");
		}
	}
}
