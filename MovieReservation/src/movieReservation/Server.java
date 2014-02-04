package movieReservation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	
	private int poolSize = 2;
	private int capacity;
	private int portNumber;
	private movieTheater theater;
	private final ExecutorService pool;
	private UDPThread udp = null;
	private TCPThread tcp = null;
		
	public Server (String capacity, String portNumber) {
		this.capacity = Integer.parseInt(capacity);
		this.portNumber = Integer.parseInt(portNumber);
		udp = new UDPThread();
		tcp = new TCPThread();
	
		pool = Executors.newFixedThreadPool(poolSize);
		
		theater = new movieTheater(this.capacity);
	}
	
	//UDP and TCP will be launched on separate threads
	public void startServer() {
		pool.execute(udp);
		pool.execute(tcp);
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
	        //byte[] bufferedReceive = new byte[1024];
	        byte[] sendData = new byte[1024];
	        String result = null;
	        
	        try {
		        while(true)
		        {
		        	//System.out.println("WAITING FOR UDP SHIT TO COME IN!!!");
		        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		        	ds.receive(receivePacket);
		        	//System.out.println("GOT THIS UDP SHIT!!!!");
		        	//Receive IPAddress and port of connected client
		        	InetAddress IPAddress = receivePacket.getAddress();
		        	int port = receivePacket.getPort();
		        	
		        	//Receive data from connected client
		        	String command = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
		        	
		        	//System.out.println("FUCKER SAID THIS: " + command);
		        	String[] elements = command.split(" ");
		        	//System.out.println("WHAT THE FUCK DO I DO WITH THIS?!?!");
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
		        	
		        	System.out.println("GET THE HELL OUT OF HERE!");
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
			String fromClient = null;
			String result = null;
			
			try {
				while(true) {
					//System.out.println("WAITING FOR TCP SHIT TO COME IN!!!");
					//System.out.println(ss.getInetAddress().toString());
					//System.out.println(ss.getLocalPort());
					//TCP connections return a new proxy socket to handle connections
					Socket connectionSocket = ss.accept();
					//System.out.println("Connected: " + connectionSocket.getRemoteSocketAddress());
					//Create input/output streams for communicating with Client
					BufferedReader inFromClient = 
							new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
					//DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
					//Receive data from Client
					//System.out.println("Preparing to receive");
					while(!inFromClient.ready()) {}
					//System.out.println("Ready to receive");
					fromClient = inFromClient.readLine();
					//System.out.println("GOT THIS TCP SHIT!!!!");
					//System.out.println("FUCKER SAID THIS: " + fromClient);
					String[] elements = fromClient.split(" ");
					//System.out.println("WHAT THE FUCK DO I DO WITH THIS?!?!");
					
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
		        	
		        	//System.out.println("GET THE HELL OUT OF HERE!");
		        	//Send the results back to client
		        	outToClient.flush();
		        	outToClient.println(result);
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
