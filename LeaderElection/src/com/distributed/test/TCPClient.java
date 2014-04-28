package com.distributed.test;

import com.distributed.messages.MessageFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

/**
 * Created by prateek on 4/24/14.
 */
public class TCPClient implements Runnable {

    int port;
    String hostname;
    MessageFactory message;
    Socket clientSocket;

    public TCPClient(int port, String hostname, MessageFactory message) {
        this.port = port;
        this.hostname = hostname;
        this.message = message;
    }


    @Override
    public void run() {
      try {
    	  	clientSocket = new Socket();
    	  	clientSocket.bind(null);
    	  	clientSocket.connect(new InetSocketAddress(hostname, port), 500);
            
            // Send to Server
            // Write object
            ObjectOutputStream oOUT = new ObjectOutputStream(clientSocket.getOutputStream());
            oOUT.writeObject(message);

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String modifiedSentence = inFromServer.readLine();

            if(modifiedSentence.isEmpty()){
               System.out.println("Well the server didn't send shit back...");
            }
            //Close the socket when finished with the transaction
            clientSocket.close();

        } catch (SocketTimeoutException to) {
        } catch (IOException e) {
        }
    }

}
