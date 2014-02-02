package movieReservation;

import java.io.Console;

public class MovieReservation {
	
	//Main function.  Program will begin here
	public static void main (String [] args) {
		if (args.length > 0) {
			//parse command line to see if running as server or client
			
			//Running as a server
			if (args[0].equals("-s")) {
				String theaterCapacity = null;
				String portNumber = null;
				
				for (int i=0; i<args.length; i++) {
					if (args[i].equals("-s")) {
						theaterCapacity = args[i+1];
						System.out.println("Theater has " + theaterCapacity + "seats");
					}
						
					if (args[i].equals("-p")){
						portNumber = args[i+1];
						System.out.println("Port Number: " + portNumber);
					}
						
				}
				
				//Create new Server object
				Server movieServer = new Server(theaterCapacity, portNumber);
			}
			
			//Running as a client
			else if (args[0].equals("-c")) {
				String protocol = null;
				String ipAddress = null;
				String portNumber = null;
				
				//Get rest of command line arguments
				for (int i=0; i<args.length; i++) {
					//user selects UDP
					if (args[i].equals("-u")) {
						protocol = "u";
						System.out.println("Set client to use UDP");
					}
					//user selects TCP
					if (args[i].equals("-t")) {
						protocol = "t";
						System.out.println("Set client to use TCP");
					}
					//user IP Address
					if (args[i].equals("-ip")) {
						ipAddress = args[i+1];
						System.out.println("IP Address: " + ipAddress);
					}
					//user Port Number
					if (args[i].equals("-p")) {
						portNumber = args[i+1];	
						System.out.println("Port Number: " + portNumber);
					}				
				}
				
				//Create new Client object
				Client movieClient = new Client(protocol, ipAddress, portNumber);
				//Start Client
				movieClient.startClient();
			}
		}
		
	}
}
