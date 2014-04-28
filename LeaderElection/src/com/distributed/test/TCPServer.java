package com.distributed.test;

import com.distributed.logic.*;
import com.distributed.logic.Process;
import com.distributed.messages.MessageFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * Created by prateek on 4/24/14.
 */
public class TCPServer implements Runnable {

    int port, cap;
    byte[] Ibuf;
    ServerSocket welcomeSocket;
    MessageFactory clientRequest;
    String clientResponse;
    SpanningTree newMsg = new SpanningTree();

    public TCPServer(int serverPort, int bufflen) {
        this.port = serverPort;
        Ibuf = new byte[bufflen];

        try {
            welcomeSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        Socket connectionSocket = null;
        while (true) {

            try {
                // Inbound
                connectionSocket = welcomeSocket.accept();

                    // For string messages...
 //               BufferedReader inFromClient =
 //                       new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                
                ObjectInputStream inFromClient =
                        new ObjectInputStream(connectionSocket.getInputStream());
                
                clientRequest = (MessageFactory)inFromClient.readObject();
//
//                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//                outToClient.writeBytes("Yo"+ '\n');
//                outToClient.flush();

                


                // Business Logic
                if (clientRequest != null) {
//                        byte[] b = newMsg.receiveMsg(clientRequest);
//                    newMsg.rereceive Msg(clientRequest);
                    newMsg.handleMsg( clientRequest,"poop" );
                    // Outbound
//                        clientResponse = new String(b, "UTF-8");
//                        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//                        outToClient.writeBytes(clientResponse + '\n');
//                        outToClient.flush();

                }
                

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 
        }
    }


}
