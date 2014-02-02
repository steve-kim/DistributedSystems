package movieReservation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

public class Server {
	
	private int capacity;
	private int portNumber;
	private movieTheater theater;
	private UDPThread udp = null;
	private TCPThread tcp = null;
		
	public Server (String capacity, String portNumber) {
		this.capacity = Integer.parseInt(capacity);
		this.portNumber = Integer.parseInt(portNumber);
		udp = new UDPThread();
		tcp = new TCPThread();
	
		theater = new movieTheater(this.capacity);
	}
	
	//UDP and TCP will be launched on separate threads
	public void startServer() {
		
	}
	
	//UDP Thread code
	private class UDPThread implements Runnable {
		private DatagramSocket ds = null;
		
		public UDPThread () {
			try {
				ds = new DatagramSocket(portNumber);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
	        byte[] receiveData = new byte[1024];
	        byte[] sendData = new byte[1024];
	        String result = null;
	        
	        try {
		        while(true)
		        {
		        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		        	ds.receive(receivePacket);
		        	//Receive IPAddress and port of connected client
		        	InetAddress IPAddress = receivePacket.getAddress();
		        	int port = receivePacket.getPort();
		        	
		        	//Receive data from connected client
		        	String command = new String( receivePacket.getData());
		        	
		        	String[] elements = command.split(" ");
		        	
		        	//We will now call the functions to handle the commands
		        	if (elements[0].equals("reserve")) {
		        		result = theater.reserveSeat(elements[1]);
		        	}
		        	else if (elements[0].equals("bookSeat")) {
		        		int seatNumber = Integer.parseInt(elements[2]);
		        		
		        		result = theater.bookSeat(elements[1], seatNumber);
		        	}
		        	
		        	else if (elements[0].equals("search")) {
		        		int searchResult = theater.search(elements[1]);
		        		
		        		if (searchResult != -1) 
		        			result = "Seat number is " + Integer.toString(searchResult);
		        		else
		        			result = "No reservation found for " + elements[1];
		        	}

		        	else if (elements[0].equals("delete")) {
		        		result = theater.delete(elements[1]);
		        	}
		        	
		        	//Send results back to client
		            sendData = result.getBytes();
		            DatagramPacket sendPacket = 
		            		new DatagramPacket(sendData, sendData.length, IPAddress, port);
		            ds.send(sendPacket);
		        }	        	
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
		}
		
		/*public void handleCommands(String command) {
			String[] elements = command.split(" ");
			
			
		}*/
		
	}
	
	
	//TCP Thread code
	private class TCPThread implements Runnable {
		private ServerSocket ss = null;
		
		public TCPThread () {
			try {
				ss = new ServerSocket(portNumber);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
