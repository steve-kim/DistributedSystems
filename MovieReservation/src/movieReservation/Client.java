package movieReservation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
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
		try {
			while (true) {
				//UDP protocol
				if (protocol.equals("u")) {
					//Read input from User
					BufferedReader inFromUser =
					         new BufferedReader(new InputStreamReader(System.in));
					DatagramSocket clientSocket = new DatagramSocket();
					String input = inFromUser.readLine();
					sendData = input.getBytes();
					//Send user input to Server for processing
					InetAddress IPAddress = InetAddress.getByName("localhost");
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
					clientSocket.send(sendPacket);
					//Receive data from Server
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					String response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
					System.out.println(response);
					clientSocket.close();
				}
				//TCP Protocol
				else if (protocol.equals("t")) {
					//Create TCP Socket to communicate with Server
					//Socket clientSocket = new Socket("localhost", portNumber);
					Socket clientSocket = new Socket(ipAddress, portNumber);
					//Create handles for Input/Output
					PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
					BufferedReader inFromServer = 
							new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
					//Get input from console
					String input = inFromUser.readLine();

					//Send data to server
					outToServer.flush();
					outToServer.println(input);

					//Receive response from server
					String response = inFromServer.readLine();
					System.out.println(response);
					clientSocket.close();
				}
	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
