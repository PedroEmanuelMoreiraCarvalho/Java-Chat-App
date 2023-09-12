package socket2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static Socket socket = null;
	private static InputStreamReader inSR = null;
	private static OutputStreamWriter outSW = null;
	private static BufferedReader bin = null;
	private static BufferedWriter bout = null;
	private static String name = "";
	
	public Client(int port){
		
		try {
			socket = new Socket("localhost", port);
			
			inSR = new InputStreamReader(socket.getInputStream());
			outSW = new OutputStreamWriter(socket.getOutputStream());
			
			bin = new BufferedReader(inSR);
			bout = new BufferedWriter(outSW);
			
			Scanner in = new Scanner(System.in);
			
			Listenner listenner = new Listenner(bin);
			new Thread(listenner).start();
			
			while(name.isBlank() || name.isEmpty()) {
				System.out.println("Escreva seu nick:");
				name = in.nextLine();
			}

			System.out.println("=========================");
			
			while(true) {
				String line = in.nextLine();
				
				bout.write(name+": "+line);
				bout.newLine();
				bout.flush();
				
				if(line.equalsIgnoreCase("EXIT")) break;
			}
			
			in.close();
			
		}catch(IOException e) {
			e.getStackTrace();
		}finally {
			try {
				if(socket != null) socket.close();
				if(inSR != null) inSR.close();
				if(outSW != null) outSW.close();
				if(bin != null) bin.close();
				if(bout != null) bout.close();
			}catch(IOException e) {
				e.getStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client(1234);
	}
	
	private static class Listenner implements Runnable{
		
		private BufferedReader bin;
		
		public Listenner(BufferedReader socket_reader) {
			this.bin = socket_reader;
		}
		
		public void run() {
			try {
				while(true) {
					System.out.println(bin.readLine());
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
