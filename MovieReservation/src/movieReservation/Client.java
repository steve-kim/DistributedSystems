package movieReservation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {
	
	private String protocol = null;
	private InetAddress ipAddress = null;
	private int portNumber = 0;
	private byte[] sendData = new byte[1024];
	private byte[] receiveData = new byte[1024];
	
	//Constructor
	public Client(String protocol, String ipAddress, String portNumber) {
		this.protocol = protocol;
		try {
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.portNumber = Integer.parseInt(portNumber);
	}
	
	public void startClient() {
		System.out.println("CLIENT HAS STARTED MOTHERFUCKER!!!!!");
		try {
			while (true) {
				System.out.println("STARTING THE GOD DAMN WHILE LOOP!!!");
				//Read input from User
				BufferedReader inFromUser =
				         new BufferedReader(new InputStreamReader(System.in));
				DatagramSocket clientSocket = new DatagramSocket();
				String input = inFromUser.readLine();
				System.out.println("ASSHOLE INPUTED: " + input);
				sendData = input.getBytes();
				//Send user input to Server for processing
				InetAddress IPAddress = InetAddress.getByName("localhost");
				System.out.println("SENDING THIS SHIT!!!");
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
				clientSocket.send(sendPacket);
				//Receive data from Server
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				String response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
				System.out.println(response);
				clientSocket.close();
				System.out.println("WHILE LOOP IS FINISHED BITCH!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void reserveCommand() {
			
	}
	
	public void bookSeatCommand() {
		
	}
	
	public void searchCommand() {
		
	}

	public void deleteCommand() {
	
	}
}
