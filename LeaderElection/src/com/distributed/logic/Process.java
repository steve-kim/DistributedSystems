package com.distributed.logic;

import com.distributed.messages.*;
import com.distributed.test.NodeTest;
import com.distributed.test.TCPClient;
import com.distributed.utils.MsgHandler;
import com.distributed.utils.NodeAttributes;
import com.distributed.utils.Pair;

import java.util.Date;
import java.util.Map;

/**
 * Created by prateek on 4/23/14.
 */
public class Process implements MsgHandler {


    // TODO: For production use the one below & not NodeTest...
//    NodeAttributes myNode = NodeAttributes.getInstance();
    // For testing...
    NodeTest myNode = NodeTest.getInstance();




    // TODO: Broadcast to all Nodes... Need to figure out how?
    public void broadcastMsg(MessageFactory m) {
        System.out.println( new Date().getTime() + " Sending msg to all"  );
//        com.sendMsg(destId, tag, msg);
        for(Map.Entry<Integer,String> entry: myNode.getServerAddresses().entrySet()){
            if(entry.getKey() != myNode.getMyPID()){
                sendMsg(entry.getKey(), myNode.getMyPID(), m);
            }
        }
    }

    // TODO: Only send to Child Nodes... Need to figure out how?
    public void sendElectMsg(ElectionMessage em) {
        System.out.println( new Date().getTime() + " "+ em.toString() );
//        com.sendMsg(destId, tag, msg);
        for(int i = 0;i < myNode.getChild().size() ; i++){
            if(myNode.getChild().getEntry(i) != myNode.getMyPID()){
                sendMsg(myNode.getChild().getEntry(i), myNode.getMyPID(), em);
            }
        }

    }

    // TODO: Only send to Parent Nodes... Need to figure out how?
    public void sendVoteMsg(VoteMessage vm) {
        System.out.println(new Date().getTime() + " " +  vm.toString());
        sendMsg(myNode.getMyParentID() ,myNode.getMyPID(), vm);
//        com.sendMsg(destId, tag, msg);
    }

    // TODO: Only send to Child Nodes directly... Need to figure out how?
    public void sendLeaderMsg(LeaderMessage lm) {
        System.out.println(new Date().getTime() + " " + lm.toString());
//        com.sendMsg(destId, tag, msg);
//        for(int i = 0;i < myNode.getChild().size() ; i++){
//                sendMsg(myNode.getChild().getEntry(i), myNode.getMyPID(), lm);
//        }
        broadcastMsg(lm);
    }

    // TODO: Broadcast to all... Need to figure out how?
    public void sendHelloMsg(HelloMessage hm) {
        System.out.println(new Date().getTime() + " " + hm.toString());
//        com.sendMsg(destId, tag, msg);
        broadcastMsg(hm);
    }

    public boolean isNeighbor(int i) {
        if (myNode.getChild().contains(i)) return true;
        else return false;
    }


    public void sendMsg( int destServerID, int srcServerID, MessageFactory m){
        String [] token = myNode.parseCommands(myNode.getServerAddresses().get(destServerID),":");
        String destIP = token[0];
        int destPort = Integer.valueOf(token[1]);
        TCPClient tcpClient = new TCPClient(destPort,destIP, m);
        Thread tC = new Thread(tcpClient);
        tC.start();
        try {
            tC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public synchronized void myWait() {
        try {
            wait();
        } catch (InterruptedException e) {System.err.println(e);
        }
    }



    public void handleMsg(MessageFactory m, String srcId) {

    }
}
