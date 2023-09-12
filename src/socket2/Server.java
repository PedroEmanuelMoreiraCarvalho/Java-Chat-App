package socket2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	static ServerSocket server;
	static ArrayList<ClientHandler> sockets_connecteds = new ArrayList<ClientHandler>();
	
	public Server(int port) throws IOException {
		server = new ServerSocket(port);
		
		while(true) {
			Socket client = server.accept();
			
			ClientHandler client_handler = new ClientHandler(client);
			this.NotifyAll(client_handler);
			sockets_connecteds.add(client_handler);
			new Thread(client_handler).start();
		}
	}
	
	public void NotifyAll(ClientHandler handler) throws IOException {
		SendToClient("novo cliente conectado",handler);
	}
	
	public static void SendToClient(String message, ClientHandler author) throws IOException {
		for(int i = 0; i < sockets_connecteds.size(); i++) {
			ClientHandler client_hundler = sockets_connecteds.get(i);
			if(!client_hundler.equals(author)) client_hundler.SendMessage(message);
		}
	}
	
	public static void main(String[] args) {
		try {
			@SuppressWarnings("unused")
			Server serv = new Server(1234);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class ClientHandler implements Runnable{
		private final Socket client_socket;
		private InputStreamReader inSR = null;
		private OutputStreamWriter outSW = null;
		private BufferedReader bin = null;
		private BufferedWriter bout = null;
		
		public ClientHandler(Socket client_socket) throws IOException{
			this.client_socket = client_socket;
			this.inSR = new InputStreamReader(client_socket.getInputStream());
			this.outSW = new OutputStreamWriter(client_socket.getOutputStream());
			
			this.bin = new BufferedReader(inSR);
			this.bout = new BufferedWriter(outSW);
		}
		
		public void SendMessage(String message) throws IOException{
			bout.write(message);
			bout.newLine();
			bout.flush();
		}
		
		public void run() {
			try {
				String line;
				
				while((line = bin.readLine()) != null) {
					SendToClient(line,this);
				}
			}catch(IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if(bout == null) {
						bout.close();
					}
					if(bin == null) {
						bin.close();
						client_socket.close();
					}
				}
				catch (IOException e) {
                    e.printStackTrace();
                }
			}
		}
	}
}
