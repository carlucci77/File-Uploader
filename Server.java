import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;
public class Server {
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
	public void run() {
		int port = 5521;
		try {
			ServerSocket socket = new ServerSocket(port);
			System.out.println("Server Running....");
			while(true) {
				System.out.println("Waiting for connection....");
				try {
					Socket client = socket.accept();
					Date date = new Date();
					SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zz yyyy");
					String dateString = format.format(date);
					System.out.println("Got a connection: " + dateString);
					System.out.println("Connected to: " + client.getInetAddress() + " Port: " + client.getPort());
					String fileName = getNullTerminatedString(client);
					System.out.println("Got file name: " + fileName);
					String fileLength = getNullTerminatedString(client);
					System.out.println("File Size: " + fileLength);
					long size = Long.valueOf(fileLength);
					getFile(fileName, size, client);
					System.out.println("Got the file.");
					client.getOutputStream().write((int)'@');
					client.close();
				}catch(Exception e) {
					System.out.println(e);
				}
				
			}
		}catch(IOException IOE) {
			System.out.println(IOE);
		}
	}
	private String getNullTerminatedString(Socket client) {
		byte[] chunk = new byte[1024];
		byte temp = 0;
		try {
			int k = 0;
			 do{
				temp = (byte)client.getInputStream().read();
				chunk[k] = temp;
				k++;
			}while(temp != '\0');
			String result = new String(chunk);
			return result.substring(0, k - 1);
		}catch(IOException IOE) {
			System.out.println(IOE);
			return null;
		}
	}
	private void getFile(String filename, long size, Socket client) {
		byte[] chunk = new byte[1024];
		File file = new File(filename);
		try {
			file.createNewFile();
			int temp = 0;
			FileOutputStream out = new FileOutputStream(file);
			while(client.getInputStream().read(chunk) > 1) {
				out.write(chunk);
			}
			out.close();
		}catch(IOException IOE) {
			System.out.println(IOE);
		}
	}
}
