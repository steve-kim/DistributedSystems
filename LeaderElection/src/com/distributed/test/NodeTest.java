package com.distributed.test;

import com.distributed.logic.Node;
import com.distributed.utils.NodeAttributes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by prateek on 4/26/14.
 */
public class NodeTest extends NodeAttributes {
    Map<Integer, String> serverAddresses = new LinkedHashMap<Integer, String>();


    private static final NodeTest singleton = new NodeTest();

    public NodeTest() {
    }

    public static NodeTest getInstance() {
        return singleton;
    }

    public Map<Integer, String> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(Map<Integer, String> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public void serverFileParser(String filename) throws IOException {
        String line;
        //Get how many servers are in the cluster
        BufferedReader reader = new BufferedReader(new FileReader(filename));



        //Load server instructions for later use
        int i = 1;
        while ((line = reader.readLine()) != null) {
            this.getServerAddresses().put(i, line);
            i++;
        }

    }


    public void startMyServerInstance() {
        // Start listening...
        String [] token = parseCommands(this.getServerAddresses().get(this.getMyPID()), ":");
        int server_port = Integer.valueOf(token[1]);
        TCPServer tcpServer = new TCPServer(server_port, 1024);
        Thread qt = new Thread(tcpServer);
        qt.start();
    }


    public String[] parseCommands (String command, String regex) {
        String[] tokens = command.split(regex);
        return tokens;
    }



}
