package com.distributed.logic;

import com.distributed.messages.*;
import java.util.Date;


/**
 * Created by prateek on 4/23/14.
 */
public class SpanningTree extends Process{

    //static final Logger logger = LoggerFactory.getLogger(SpanningTree.class);
    final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SpanningTree.class.getName());

    /**
     * Algorithm:
     * Parent is always with a lower PID than mine
     *
     */
    @Override
    public void handleMsg( MessageFactory msg, String srcId) {

        System.out.println("Received a Message" + new Date().getTime());
        if( msg.getPid() < myNode.getMyPID() ){
            System.out.println("receivedPID < myPID" );
            if(!myNode.hasParent()){
                myNode.setMyParentID(msg.getPid());
                myNode.setLastContactWithParent(msg.getTimeStamp());
                System.out.println("Updating Parent: " + myNode.toString());
            }
            else{
                System.out.println("Received PID is lower than mine, " +
                        "but I am already associated with a Parent" + myNode.toString());
            }
        }
        else{
            System.out.println("receivedPID > myPID");
            // Possible childVotes...
            if(!myNode.getChild().contains(msg.getPid())){
                myNode.addChild(msg.getPid());
                System.out.println("Adding Child: " + myNode.toString());
            }
        }

        // HELLO
        if(msg instanceof HelloMessage){
            HelloMessage receivedHello = (HelloMessage)msg;
            System.out.println("Received Hello: " + receivedHello.toString());

            if(receivedHello.getPid() > 0){

                if( receivedHello.getPid() < myNode.getMyPID() ){
                    System.out.println("receivedPID < myPID" );
                    if(!myNode.hasParent()){
                        myNode.setMyParentID(receivedHello.getPid());
                        myNode.setLastContactWithParent(receivedHello.getTimeStamp());
                        System.out.println("Updating Parent: " + myNode.toString());
                    }
                    else{
                        if( Integer.valueOf(receivedHello.getPid()).equals(Integer.valueOf(myNode.getMyParentID())) ){
                            myNode.setLastContactWithParent(receivedHello.getTimeStamp());
                            System.out.println("Updating Parent's Clock: " + myNode.toString());
                        }

                        else{
                            // Do nothing...
                            System.out.println("Received PID is lower than mine, " +
                                    "but I am already associated with a Parent" + myNode.toString());
                        }
                    }
                }
                else if (receivedHello.getPid() > myNode.getMyPID()){
                    System.out.println("receivedPID > myPID" );
                    // Possible child...
                    if(!myNode.getChild().contains(receivedHello.getPid())){
                        myNode.addChild(receivedHello.getPid());
                        System.out.println("Adding Child: " + myNode.toString());
                    }
                }
                else{}
                // Figure out a when to broadcast the hello message...
                // Trigger LE coz, my value is lower (higher priority) than current leader...
                // Problem with this is, that we can trigger a LE prematurely, This will also trigger even if i'm the
                // current leader (this is done to reflect the current change in priority)...
                if((myNode.getMyPriorityID() < receivedHello.getLeaderPriorityID()) && (receivedHello
                        .getLeaderPriorityID() > 0)){
                    System.out.println("myPriority < receivedPriority" );
                    // Only send Election Message to child if you have'em.
                    if(!myNode.getChild().isEmpty()){
                        System.out.println("Sending Election to Children: " + myNode.toString());
                        ElectionMessage elect = new ElectionMessage(myNode.getMyPID(), myNode.getMyPriorityID(), myNode.updateElectionTag(),
                            new Date().getTime(), "NotUsed");
                        sendElectMsg(elect);
                    }
                    // If no children & i have a parent, then request parent to make me the leader...
                    // Here i'm sending a vote w/o elections, hence, the ElectionTag is outdated(not sure what to do

                    else if(myNode.getChild().isEmpty() && myNode.hasParent()){
                        System.out.println("Sending Vote to Parent: " + myNode.toString());
                        VoteMessage vote = new VoteMessage(myNode.getMyPID(), myNode.getMyPID(),
                                myNode.getMyPriorityID(),myNode.getElectionTag(), new Date().getTime());
                        sendVoteMsg(vote);
                    }
                    // I have no children nor do i have a parent...
                    else{
                        // Can't do anything..Someone else will connect with me.
                        System.out.println("Don't have child or a parent: " + myNode.toString());
                    }

                }
            }

        }


        // ELECTION
        if(msg instanceof ElectionMessage){
            ElectionMessage receivedElection = (ElectionMessage)msg;
            System.out.println("Received ELECT: " + receivedElection.toString());

            // Update Election Tag to keep track of which election we are on...
            if(receivedElection.getElectionTag() > myNode.getElectionTag()){
                myNode.setElectionTag(receivedElection.getElectionTag());
            }

            // Need to also check for freshness of the ElectionMessage...No need to respond to old messages...
            // TODO: Also, we should only trigger election selectively, depending on the electionTag & time-elapsed...
            // only trigger if the elapsed time is greater? need to test if this is always true

            if(receivedElection.getPriorityID() <= myNode.getMyPriorityID()){
                System.out.println("receivedPriorty < myPriority" );
                // Forward the received messge to my Child...(This is done recursively)...
                if(!myNode.getChild().isEmpty() && myNode.hasParent()){
                    System.out.println("Sending Election to Children: " + myNode.toString());
                    sendElectMsg(receivedElection);
                }
                else if(myNode.getChild().isEmpty() && myNode.hasParent()){
                    System.out.println("Sending Vote to Parent: " + myNode.toString());
                    VoteMessage vote = new VoteMessage(myNode.getMyPID(), receivedElection.getPid(),
                            receivedElection.getPriorityID(), receivedElection.getElectionTag(), new Date().getTime());
                    sendVoteMsg(vote);
                }
                else{
                    // Should never receive
                    System.out.println("Wait what is going on...");
                }
            }
            // I have a lower priority than received message, then send Elect Me & Vote Me.
            else{
                System.out.println("receivedPriorty > myPriority" );
                if(!myNode.getChild().isEmpty() && myNode.hasParent()){
                    System.out.println("Sending Election to Children: " + myNode.toString());
                    ElectionMessage elect = new ElectionMessage(myNode.getMyPID(), myNode.getMyPriorityID(), myNode.updateElectionTag(),
                            new Date().getTime(), "NotUsed");
                    sendElectMsg(elect);
                }
                else if(myNode.getChild().isEmpty() && myNode.hasParent()){
                    System.out.println("Sending Vote to Parent: " + myNode.toString());
                    VoteMessage vote = new VoteMessage(myNode.getMyPID(), myNode.getMyPID(),
                            myNode.getMyPriorityID(),myNode.getElectionTag(), new Date().getTime());
                    sendVoteMsg(vote);
                }
                else{
                    // Should never receive
                    System.out.println("Wait what is going on again?");
                }
            }
        }

        // VOTE
        if(msg instanceof VoteMessage){
            VoteMessage receivedVote = (VoteMessage)msg;
            System.out.println("Received VOTE: " + receivedVote.toString());


            if(receivedVote.getLeaderPriorityID() <= myNode.getMyPriorityID()){
                System.out.println("receivedPriorty < myPriority" );
                // Forward the received messge to my Child...(This is done recursively)...
                // Have Child & Parent (FUll family)
                if(!myNode.getChild().isEmpty() && myNode.hasParent()){
                    // Only listen to my children & not my neighbours
                    if(myNode.getChild().contains(receivedVote.getPid())){
                        System.out.println("Sending Vote to Parent: " + myNode.toString());
                        sendVoteMsg(receivedVote);
                    }
                    else{
                        System.out.println("Who gives a shit out neighbour's children: " + myNode.toString());
                    }
                }
                // Have Child but No Parent - ROOT
                else if(!myNode.getChild().isEmpty() && !myNode.hasParent()){
                    if(myNode.getChild().contains(receivedVote.getPid())){
                        // Leader message coz i'm the root
                        // Added a check to make sure, the new leader is better than last...
                        if(receivedVote.getLeaderPriorityID() <= myNode.getMyLeaderPriorityID()){
                            LeaderMessage leader = new LeaderMessage(myNode.getMyPID(), receivedVote.getElectionID(),
                                    receivedVote.getMyVote2lid(), receivedVote.getLeaderPriorityID(), new Date().getTime());
                            sendLeaderMsg(leader);

                            // Update My Values as Well!!
                            myNode.setMyLeaderID( leader.getLid());
                            myNode.setMyLeaderPriorityID( leader.getLeaderPriorityID());
                            myNode.setElectionID(leader.getElectionID());

                        }
                        // Broadcast the old leader, if the new leader is not good enough!!
                        else{
                            LeaderMessage leader = new LeaderMessage(myNode.getMyPID(), receivedVote.getElectionID(),
                                    myNode.getMyLeaderID(), myNode.getMyLeaderPriorityID(), new Date().getTime());
                            sendLeaderMsg(leader);
                            // No need to update my values as i know my values...
                        }
                    }
                    else{
                        System.out.println("Should never see this line: One: Who gives a shit out neighbour's children: " + myNode.toString());
                    }
                }
                else{
                    // Should never receive. A child should never receive a VOTE message.
                    System.out.println("Should never see this line: Two " + myNode.toString());
                }
            }
            else{ // I have a lower# (higher priorty) than the Received Leader Priority
                System.out.println("receivedPriorty > myPriority" );
                if(!myNode.getChild().isEmpty() && myNode.hasParent()){
                    // Only listen to my children & not my neighbours
                    if(myNode.getChild().contains(receivedVote.getPid())){
                        System.out.println("Sending Vote to Parent: " + myNode.toString());
                        VoteMessage vote = new VoteMessage(myNode.getMyPID(), myNode.getMyPID(),
                                myNode.getMyPriorityID(), receivedVote.getElectionID(), new Date().getTime());
                        sendVoteMsg(vote);
                    }
                    else{
                        System.out.println("Who gives a shit out neighbour's children: " + myNode.toString());
                    }
                }
                // Have Child but No Parent - ROOT
                else if(!myNode.getChild().isEmpty() && !myNode.hasParent()){
                    if(myNode.getChild().contains(receivedVote.getPid())){
                        // Leader message coz i'm the root
                        // Additional check to make sure, I am still eligible to be a leader
                        if( myNode.getMyPriorityID() <= myNode.getMyLeaderPriorityID() ){
                            LeaderMessage leader = new LeaderMessage(myNode.getMyPID(), receivedVote.getElectionID(),
                                    myNode.getMyPID(), myNode.getMyPriorityID(), new Date().getTime());
                            sendLeaderMsg(leader);

                            // Update My Values as Well!!
                            myNode.setMyLeaderID( leader.getLid());
                            myNode.setMyLeaderPriorityID( leader.getLeaderPriorityID());
                            myNode.setElectionID(leader.getElectionID());

                        }
                        // Broadcast the old leader, if the new leader is not good enough!!
                        else{
                            LeaderMessage leader = new LeaderMessage(myNode.getMyPID(), receivedVote.getElectionID(),
                                    myNode.getMyLeaderID(), myNode.getMyLeaderPriorityID(), new Date().getTime());
                            sendLeaderMsg(leader);
                            // No need to update my values as i know my values...
                        }
                    }
                    else{
                        // Potential to make it more efficient, but using the ability to overhear messages and
                        // triggering a LE, by passing neighbour, since I have higher priority than my neighbour...
                        System.out.println("Should never see this line: Three: Overheard from neighbour, " +
                                "not my child, Who gives a shit out neighbour's children: " + myNode
                                .toString
                                ());
                    }
                }
                else{
                    // Should never receive. A child should never receive a VOTE message.
                    System.out.println("Should never see this line: Four" + myNode.toString());
                }
            }
            }

//
//            if(myNode.getChild().isEmpty()){
//                // This should never happen....
//            }
//            // TODO: Should probably keep track of which election to vote in. Ignore votes for election for which the
//            // comparison is already done...
//            else if( myNode.hasParent() && myNode.getChild().contains(receivedVote.getPid()) ){
//                    if(receivedVote.getLeaderPriorityID() < myNode.getMyPriorityID()){
//                        sendVoteMsg(receivedVote);
//                    }
//                    else{
//                        VoteMessage vote = new VoteMessage(myNode.getMyPID(), myNode.getMyPID(),
//                                myNode.getMyPriorityID(), receivedVote.getElectionID(), new Date().getTime());
//                        sendVoteMsg(vote);
//                    }
//            }
//            // I'm the root node...
//            else if(!myNode.hasParent() && myNode.getChild().contains(receivedVote.getPid())){
//                if(receivedVote.getLeaderPriorityID() < myNode.getMyPriorityID()){
//                    LeaderMessage leader = new LeaderMessage(myNode.getMyPID(), receivedVote.getElectionID(),
//                            receivedVote.getMyVote2lid(), receivedVote.getLeaderPriorityID(), new Date().getTime());
//                    sendLeaderMsg(leader);
//                }
//                else{
//                    LeaderMessage leader = new LeaderMessage(myNode.getMyPID(), receivedVote.getElectionID(),
//                            myNode.getMyPID(), myNode.getMyPriorityID(), new Date().getTime());
//                    sendLeaderMsg(leader);
//                }
//
//            }
//            else{
//                logger.log(Level.ALL,"Something bad happened...Should never execute this line");
//            }
//
//        }

        // LEADER
        // TODO: Figure out when to update self upon receiving LEADER message... We shouldn't be updating all the
        // time...
        if(msg instanceof LeaderMessage){
            LeaderMessage receivedLeader = (LeaderMessage)msg;
            System.out.println("Received LEADER: " + receivedLeader.toString());


            myNode.setMyLeaderID( receivedLeader.getLid());
            myNode.setMyLeaderPriorityID( receivedLeader.getLeaderPriorityID());
            myNode.setElectionID(receivedLeader.getElectionID());
            System.out.println("FOUND LEADER:: " + myNode.toString());
        }
    }


    public void beacon(){
        HelloMessage hello = new HelloMessage(myNode.getMyPID(), myNode.getMyLeaderID(),
                myNode.getMyLeaderPriorityID(), new Date().getTime());
        sendHelloMsg(hello);
    }



    public void testCasesVote(){

        // Tested by setting Server node as "INIT_VAL" as 1 & 2
        VoteMessage h1 = new VoteMessage( -1, -1, -1, 10,  new Date().getTime());
        VoteMessage h2 = new VoteMessage( 0, 0, 0, 10, new Date().getTime());
        VoteMessage h3 = new VoteMessage( 1, -1, -1, 10, new Date().getTime());
        VoteMessage h4 = new VoteMessage( 2, -1, -1, 10, new Date().getTime());
        VoteMessage h5 = new VoteMessage( 3, -1, -1, 10, new Date().getTime());
        VoteMessage h6 = new VoteMessage( 4, -1, -1, 10, new Date().getTime());

        VoteMessage h7 = new VoteMessage( 1,  0, -1, 10, new Date().getTime());
        VoteMessage h8 = new VoteMessage( 1,  1, -1, 10, new Date().getTime());
        VoteMessage h9 = new VoteMessage( 1,  2, -1, 10, new Date().getTime());
        VoteMessage h10 = new VoteMessage( 1,  3, -1, 10, new Date().getTime());
        VoteMessage h11 = new VoteMessage( 1,  4, -1, 10, new Date().getTime());

        VoteMessage h12 = new VoteMessage( 1,  0,  1, 10, new Date().getTime());
        VoteMessage h13 = new VoteMessage( 1,  1,  1, 10, new Date().getTime());
        VoteMessage h14 = new VoteMessage( 1,  2,  1, 10, new Date().getTime());
        VoteMessage h15 = new VoteMessage( 1,  3,  1, 10, new Date().getTime());
        VoteMessage h16 = new VoteMessage( 1,  4,  1, 10, new Date().getTime());

        VoteMessage h17 = new VoteMessage( 1,  0,  2, 10, new Date().getTime());
        VoteMessage h18 = new VoteMessage( 1,  1,  2, 10, new Date().getTime());
        VoteMessage h19 = new VoteMessage( 1,  2,  2, 10, new Date().getTime());
        VoteMessage h20 = new VoteMessage( 1,  3,  2, 10, new Date().getTime());
        VoteMessage h21 = new VoteMessage( 1,  4,  2, 10, new Date().getTime());

        VoteMessage h22 = new VoteMessage( 1,  0,  3, 10, new Date().getTime());
        VoteMessage h23 = new VoteMessage( 1,  1,  3, 10, new Date().getTime());
        VoteMessage h24 = new VoteMessage( 1,  2,  3, 10, new Date().getTime());
        VoteMessage h25 = new VoteMessage( 1,  3,  3, 10, new Date().getTime());
        VoteMessage h26 = new VoteMessage( 1,  4,  3, 10, new Date().getTime());

        VoteMessage h27 = new VoteMessage( 2,  0,  1, 10, new Date().getTime());
        VoteMessage h28 = new VoteMessage( 2,  1,  1, 10, new Date().getTime());
        VoteMessage h29 = new VoteMessage( 2,  2,  1, 10, new Date().getTime());
        VoteMessage h30 = new VoteMessage( 2,  3,  1, 10, new Date().getTime());
        VoteMessage h31 = new VoteMessage( 2,  4,  1, 10, new Date().getTime());

        VoteMessage h32 = new VoteMessage( 2,  0,  2, 10, new Date().getTime());
        VoteMessage h33 = new VoteMessage( 2,  1,  2, 10, new Date().getTime());
        VoteMessage h34 = new VoteMessage( 2,  2,  2, 10, new Date().getTime());
        VoteMessage h35 = new VoteMessage( 2,  3,  2, 10, new Date().getTime());
        VoteMessage h36 = new VoteMessage( 2,  4,  2, 10, new Date().getTime());

        VoteMessage h37 = new VoteMessage( 2,  0,  3, 10, new Date().getTime());
        VoteMessage h38 = new VoteMessage( 2,  1,  3, 10, new Date().getTime());
        VoteMessage h39 = new VoteMessage( 2,  2,  3, 10, new Date().getTime());
        VoteMessage h40 = new VoteMessage( 2,  3,  3, 10, new Date().getTime());
        VoteMessage h41 = new VoteMessage( 2,  4,  3, 10, new Date().getTime());

        VoteMessage h42 = new VoteMessage( 3,  0,  1, 10, new Date().getTime());
        VoteMessage h43 = new VoteMessage( 3,  1,  1, 10, new Date().getTime());
        VoteMessage h44 = new VoteMessage( 3,  2,  1, 10, new Date().getTime());
        VoteMessage h45 = new VoteMessage( 3,  3,  1, 10, new Date().getTime());
        VoteMessage h46 = new VoteMessage( 3,  4,  1, 10, new Date().getTime());

        VoteMessage h47 = new VoteMessage( 3,  0,  2, 10, new Date().getTime());
        VoteMessage h48 = new VoteMessage( 3,  1,  2, 10, new Date().getTime());
        VoteMessage h49 = new VoteMessage( 3,  2,  2, 10, new Date().getTime());
        VoteMessage h50 = new VoteMessage( 3,  3,  2, 10, new Date().getTime());
        VoteMessage h51 = new VoteMessage( 3,  4,  2, 10, new Date().getTime());

        VoteMessage h52 = new VoteMessage( 3,  0,  3, 10, new Date().getTime());
        VoteMessage h53 = new VoteMessage( 3,  1,  3, 10, new Date().getTime());
        VoteMessage h54 = new VoteMessage( 3,  2,  3, 10, new Date().getTime());
        VoteMessage h55 = new VoteMessage( 3,  3,  3, 10, new Date().getTime());
        VoteMessage h56 = new VoteMessage( 3,  4,  3, 10, new Date().getTime());

        VoteMessage h57 = new VoteMessage( 4,  0,  1, 10, new Date().getTime());
        VoteMessage h58 = new VoteMessage( 4,  1,  1, 10, new Date().getTime());
        VoteMessage h59 = new VoteMessage( 4,  2,  1, 10, new Date().getTime());
        VoteMessage h60 = new VoteMessage( 4,  3,  1, 10, new Date().getTime());
        VoteMessage h61 = new VoteMessage( 4,  4,  1, 10, new Date().getTime());

        VoteMessage h62 = new VoteMessage( 4,  0,  2, 10, new Date().getTime());
        VoteMessage h63 = new VoteMessage( 4,  1,  2, 10, new Date().getTime());
        VoteMessage h64 = new VoteMessage( 4,  2,  2, 10, new Date().getTime());
        VoteMessage h65 = new VoteMessage( 4,  3,  2, 10, new Date().getTime());
        VoteMessage h66 = new VoteMessage( 4,  4,  2, 10, new Date().getTime());

        VoteMessage h67 = new VoteMessage( 4,  0,  3, 10, new Date().getTime());
        VoteMessage h68 = new VoteMessage( 4,  1,  3, 10, new Date().getTime());
        VoteMessage h69 = new VoteMessage( 4,  2,  3, 10, new Date().getTime());
        VoteMessage h70 = new VoteMessage( 4,  3,  3, 10, new Date().getTime());
        VoteMessage h71 = new VoteMessage( 4,  4,  3, 10, new Date().getTime());



        sendVoteMsg(h1);
        sendVoteMsg(h2);
        sendVoteMsg(h3);
        sendVoteMsg(h4);
        sendVoteMsg(h5);

        sendVoteMsg(h6);
        sendVoteMsg(h7);
        sendVoteMsg(h8);
        sendVoteMsg(h9);
        sendVoteMsg(h10);

        sendVoteMsg(h11);
        sendVoteMsg(h12);
        sendVoteMsg(h13);
        sendVoteMsg(h14);
        sendVoteMsg(h15);

        sendVoteMsg(h16);
        sendVoteMsg(h17);
        sendVoteMsg(h18);
        sendVoteMsg(h19);
        sendVoteMsg(h20);

        sendVoteMsg(h21);
        sendVoteMsg(h22);
        sendVoteMsg(h23);
        sendVoteMsg(h24);
        sendVoteMsg(h25);

        sendVoteMsg(h26);
        sendVoteMsg(h27);
        sendVoteMsg(h28);
        sendVoteMsg(h29);
        sendVoteMsg(h30);

        sendVoteMsg(h31);
        sendVoteMsg(h32);
        sendVoteMsg(h33);
        sendVoteMsg(h34);
        sendVoteMsg(h35);

        sendVoteMsg(h36);
        sendVoteMsg(h37);
        sendVoteMsg(h38);
        sendVoteMsg(h39);
        sendVoteMsg(h40);

        sendVoteMsg(h41);
        sendVoteMsg(h42);
        sendVoteMsg(h43);
        sendVoteMsg(h44);
        sendVoteMsg(h45);

        sendVoteMsg(h46);
        sendVoteMsg(h47);
        sendVoteMsg(h48);
        sendVoteMsg(h49);
        sendVoteMsg(h50);

        sendVoteMsg(h51);
        sendVoteMsg(h52);
        sendVoteMsg(h53);
        sendVoteMsg(h54);
        sendVoteMsg(h55);

        sendVoteMsg(h56);
        sendVoteMsg(h57);
        sendVoteMsg(h58);
        sendVoteMsg(h59);
        sendVoteMsg(h60);

        sendVoteMsg(h61);
        sendVoteMsg(h62);
        sendVoteMsg(h63);
        sendVoteMsg(h64);
        sendVoteMsg(h65);

        sendVoteMsg(h66);
        sendVoteMsg(h67);
        sendVoteMsg(h68);
        sendVoteMsg(h69);
        sendVoteMsg(h70);

        sendVoteMsg(h71);

    }





    public void testCasesElect(){

        // Tested by setting Server node as "INIT_VAL" as 1 & 2
        ElectionMessage h1 = new ElectionMessage( -1, -1, -1, new Date().getTime(), "Not Used");
        ElectionMessage h2 = new ElectionMessage( 0, 0, 0, new Date().getTime(), "Not Used");
        ElectionMessage h3 = new ElectionMessage( 1, -1, -1, new Date().getTime(), "Not Used");
        ElectionMessage h4 = new ElectionMessage( 2, -1, -1, new Date().getTime(), "Not Used");
        ElectionMessage h5 = new ElectionMessage( 3, -1, -1, new Date().getTime(), "Not Used");
        ElectionMessage h6 = new ElectionMessage( 4, -1, -1, new Date().getTime(), "Not Used");

        ElectionMessage h7 = new ElectionMessage( 1,  0, -1, new Date().getTime(), "Not Used");
        ElectionMessage h8 = new ElectionMessage( 1,  1, -1, new Date().getTime(), "Not Used");
        ElectionMessage h9 = new ElectionMessage( 1,  2, -1, new Date().getTime(), "Not Used");
        ElectionMessage h10 = new ElectionMessage( 1,  3, -1, new Date().getTime(), "Not Used");
        ElectionMessage h11 = new ElectionMessage( 1,  4, -1, new Date().getTime(), "Not Used");

        ElectionMessage h12 = new ElectionMessage( 1,  0,  1, new Date().getTime(), "Not Used");
        ElectionMessage h13 = new ElectionMessage( 1,  1,  1, new Date().getTime(), "Not Used");
        ElectionMessage h14 = new ElectionMessage( 1,  2,  1, new Date().getTime(), "Not Used");
        ElectionMessage h15 = new ElectionMessage( 1,  3,  1, new Date().getTime(), "Not Used");
        ElectionMessage h16 = new ElectionMessage( 1,  4,  1, new Date().getTime(), "Not Used");

        ElectionMessage h17 = new ElectionMessage( 1,  0,  2, new Date().getTime(), "Not Used");
        ElectionMessage h18 = new ElectionMessage( 1,  1,  2, new Date().getTime(), "Not Used");
        ElectionMessage h19 = new ElectionMessage( 1,  2,  2, new Date().getTime(), "Not Used");
        ElectionMessage h20 = new ElectionMessage( 1,  3,  2, new Date().getTime(), "Not Used");
        ElectionMessage h21 = new ElectionMessage( 1,  4,  2, new Date().getTime(), "Not Used");

        ElectionMessage h22 = new ElectionMessage( 1,  0,  3, new Date().getTime(), "Not Used");
        ElectionMessage h23 = new ElectionMessage( 1,  1,  3, new Date().getTime(), "Not Used");
        ElectionMessage h24 = new ElectionMessage( 1,  2,  3, new Date().getTime(), "Not Used");
        ElectionMessage h25 = new ElectionMessage( 1,  3,  3, new Date().getTime(), "Not Used");
        ElectionMessage h26 = new ElectionMessage( 1,  4,  3, new Date().getTime(), "Not Used");

        ElectionMessage h27 = new ElectionMessage( 2,  0,  1, new Date().getTime(), "Not Used");
        ElectionMessage h28 = new ElectionMessage( 2,  1,  1, new Date().getTime(), "Not Used");
        ElectionMessage h29 = new ElectionMessage( 2,  2,  1, new Date().getTime(), "Not Used");
        ElectionMessage h30 = new ElectionMessage( 2,  3,  1, new Date().getTime(), "Not Used");
        ElectionMessage h31 = new ElectionMessage( 2,  4,  1, new Date().getTime(), "Not Used");

        ElectionMessage h32 = new ElectionMessage( 2,  0,  2, new Date().getTime(), "Not Used");
        ElectionMessage h33 = new ElectionMessage( 2,  1,  2, new Date().getTime(), "Not Used");
        ElectionMessage h34 = new ElectionMessage( 2,  2,  2, new Date().getTime(), "Not Used");
        ElectionMessage h35 = new ElectionMessage( 2,  3,  2, new Date().getTime(), "Not Used");
        ElectionMessage h36 = new ElectionMessage( 2,  4,  2, new Date().getTime(), "Not Used");

        ElectionMessage h37 = new ElectionMessage( 2,  0,  3, new Date().getTime(), "Not Used");
        ElectionMessage h38 = new ElectionMessage( 2,  1,  3, new Date().getTime(), "Not Used");
        ElectionMessage h39 = new ElectionMessage( 2,  2,  3, new Date().getTime(), "Not Used");
        ElectionMessage h40 = new ElectionMessage( 2,  3,  3, new Date().getTime(), "Not Used");
        ElectionMessage h41 = new ElectionMessage( 2,  4,  3, new Date().getTime(), "Not Used");

        ElectionMessage h42 = new ElectionMessage( 3,  0,  1, new Date().getTime(), "Not Used");
        ElectionMessage h43 = new ElectionMessage( 3,  1,  1, new Date().getTime(), "Not Used");
        ElectionMessage h44 = new ElectionMessage( 3,  2,  1, new Date().getTime(), "Not Used");
        ElectionMessage h45 = new ElectionMessage( 3,  3,  1, new Date().getTime(), "Not Used");
        ElectionMessage h46 = new ElectionMessage( 3,  4,  1, new Date().getTime(), "Not Used");

        ElectionMessage h47 = new ElectionMessage( 3,  0,  2, new Date().getTime(), "Not Used");
        ElectionMessage h48 = new ElectionMessage( 3,  1,  2, new Date().getTime(), "Not Used");
        ElectionMessage h49 = new ElectionMessage( 3,  2,  2, new Date().getTime(), "Not Used");
        ElectionMessage h50 = new ElectionMessage( 3,  3,  2, new Date().getTime(), "Not Used");
        ElectionMessage h51 = new ElectionMessage( 3,  4,  2, new Date().getTime(), "Not Used");

        ElectionMessage h52 = new ElectionMessage( 3,  0,  3, new Date().getTime(), "Not Used");
        ElectionMessage h53 = new ElectionMessage( 3,  1,  3, new Date().getTime(), "Not Used");
        ElectionMessage h54 = new ElectionMessage( 3,  2,  3, new Date().getTime(), "Not Used");
        ElectionMessage h55 = new ElectionMessage( 3,  3,  3, new Date().getTime(), "Not Used");
        ElectionMessage h56 = new ElectionMessage( 3,  4,  3, new Date().getTime(), "Not Used");

        ElectionMessage h57 = new ElectionMessage( 4,  0,  1, new Date().getTime(), "Not Used");
        ElectionMessage h58 = new ElectionMessage( 4,  1,  1, new Date().getTime(), "Not Used");
        ElectionMessage h59 = new ElectionMessage( 4,  2,  1, new Date().getTime(), "Not Used");
        ElectionMessage h60 = new ElectionMessage( 4,  3,  1, new Date().getTime(), "Not Used");
        ElectionMessage h61 = new ElectionMessage( 4,  4,  1, new Date().getTime(), "Not Used");

        ElectionMessage h62 = new ElectionMessage( 4,  0,  2, new Date().getTime(), "Not Used");
        ElectionMessage h63 = new ElectionMessage( 4,  1,  2, new Date().getTime(), "Not Used");
        ElectionMessage h64 = new ElectionMessage( 4,  2,  2, new Date().getTime(), "Not Used");
        ElectionMessage h65 = new ElectionMessage( 4,  3,  2, new Date().getTime(), "Not Used");
        ElectionMessage h66 = new ElectionMessage( 4,  4,  2, new Date().getTime(), "Not Used");

        ElectionMessage h67 = new ElectionMessage( 4,  0,  3, new Date().getTime(), "Not Used");
        ElectionMessage h68 = new ElectionMessage( 4,  1,  3, new Date().getTime(), "Not Used");
        ElectionMessage h69 = new ElectionMessage( 4,  2,  3, new Date().getTime(), "Not Used");
        ElectionMessage h70 = new ElectionMessage( 4,  3,  3, new Date().getTime(), "Not Used");
        ElectionMessage h71 = new ElectionMessage( 4,  4,  3, new Date().getTime(), "Not Used");



//        sendElectMsg(h1);
//        sendElectMsg(h2);
        sendElectMsg(h3);
        sendElectMsg(h4);
        sendElectMsg(h5);

        sendElectMsg(h6);
        sendElectMsg(h7);
        sendElectMsg(h8);
        sendElectMsg(h9);
        sendElectMsg(h10);

        sendElectMsg(h11);
        sendElectMsg(h12);
        sendElectMsg(h13);
        sendElectMsg(h14);
        sendElectMsg(h15);

        sendElectMsg(h16);
        sendElectMsg(h17);
        sendElectMsg(h18);
        sendElectMsg(h19);
        sendElectMsg(h20);

        sendElectMsg(h21);
        sendElectMsg(h22);
        sendElectMsg(h23);
        sendElectMsg(h24);
        sendElectMsg(h25);

        sendElectMsg(h26);
        sendElectMsg(h27);
        sendElectMsg(h28);
        sendElectMsg(h29);
        sendElectMsg(h30);

        sendElectMsg(h31);
        sendElectMsg(h32);
        sendElectMsg(h33);
        sendElectMsg(h34);
        sendElectMsg(h35);

        sendElectMsg(h36);
        sendElectMsg(h37);
        sendElectMsg(h38);
        sendElectMsg(h39);
        sendElectMsg(h40);

        sendElectMsg(h41);
        sendElectMsg(h42);
        sendElectMsg(h43);
        sendElectMsg(h44);
        sendElectMsg(h45);

        sendElectMsg(h46);
        sendElectMsg(h47);
        sendElectMsg(h48);
        sendElectMsg(h49);
        sendElectMsg(h50);

        sendElectMsg(h51);
        sendElectMsg(h52);
        sendElectMsg(h53);
        sendElectMsg(h54);
        sendElectMsg(h55);

        sendElectMsg(h56);
        sendElectMsg(h57);
        sendElectMsg(h58);
        sendElectMsg(h59);
        sendElectMsg(h60);

        sendElectMsg(h61);
        sendElectMsg(h62);
        sendElectMsg(h63);
        sendElectMsg(h64);
        sendElectMsg(h65);

        sendElectMsg(h66);
        sendElectMsg(h67);
        sendElectMsg(h68);
        sendElectMsg(h69);
        sendElectMsg(h70);

        sendElectMsg(h71);

    }




    public void testCasesHello(){

        // Tested by setting Server node as "INIT_VAL" as 1 & 2
        HelloMessage h1 = new HelloMessage( -1, -1, -1, new Date().getTime());
        HelloMessage h2 = new HelloMessage( 0, 0, 0, new Date().getTime());
        HelloMessage h3 = new HelloMessage( 1, -1, -1, new Date().getTime());
        HelloMessage h4 = new HelloMessage( 2, -1, -1, new Date().getTime());
        HelloMessage h5 = new HelloMessage( 3, -1, -1, new Date().getTime());
        HelloMessage h6 = new HelloMessage( 4, -1, -1, new Date().getTime());

        HelloMessage h7 = new HelloMessage( 1,  0, -1, new Date().getTime());
        HelloMessage h8 = new HelloMessage( 1,  1, -1, new Date().getTime());
        HelloMessage h9 = new HelloMessage( 1,  2, -1, new Date().getTime());
        HelloMessage h10 = new HelloMessage( 1,  3, -1, new Date().getTime());
        HelloMessage h11 = new HelloMessage( 1,  4, -1, new Date().getTime());

        HelloMessage h12 = new HelloMessage( 1,  0,  1, new Date().getTime());
        HelloMessage h13 = new HelloMessage( 1,  1,  1, new Date().getTime());
        HelloMessage h14 = new HelloMessage( 1,  2,  1, new Date().getTime());
        HelloMessage h15 = new HelloMessage( 1,  3,  1, new Date().getTime());
        HelloMessage h16 = new HelloMessage( 1,  4,  1, new Date().getTime());

        HelloMessage h17 = new HelloMessage( 1,  0,  2, new Date().getTime());
        HelloMessage h18 = new HelloMessage( 1,  1,  2, new Date().getTime());
        HelloMessage h19 = new HelloMessage( 1,  2,  2, new Date().getTime());
        HelloMessage h20 = new HelloMessage( 1,  3,  2, new Date().getTime());
        HelloMessage h21 = new HelloMessage( 1,  4,  2, new Date().getTime());

        HelloMessage h22 = new HelloMessage( 1,  0,  3, new Date().getTime());
        HelloMessage h23 = new HelloMessage( 1,  1,  3, new Date().getTime());
        HelloMessage h24 = new HelloMessage( 1,  2,  3, new Date().getTime());
        HelloMessage h25 = new HelloMessage( 1,  3,  3, new Date().getTime());
        HelloMessage h26 = new HelloMessage( 1,  4,  3, new Date().getTime());

        HelloMessage h27 = new HelloMessage( 2,  0,  1, new Date().getTime());
        HelloMessage h28 = new HelloMessage( 2,  1,  1, new Date().getTime());
        HelloMessage h29 = new HelloMessage( 2,  2,  1, new Date().getTime());
        HelloMessage h30 = new HelloMessage( 2,  3,  1, new Date().getTime());
        HelloMessage h31 = new HelloMessage( 2,  4,  1, new Date().getTime());

        HelloMessage h32 = new HelloMessage( 2,  0,  2, new Date().getTime());
        HelloMessage h33 = new HelloMessage( 2,  1,  2, new Date().getTime());
        HelloMessage h34 = new HelloMessage( 2,  2,  2, new Date().getTime());
        HelloMessage h35 = new HelloMessage( 2,  3,  2, new Date().getTime());
        HelloMessage h36 = new HelloMessage( 2,  4,  2, new Date().getTime());

        HelloMessage h37 = new HelloMessage( 2,  0,  3, new Date().getTime());
        HelloMessage h38 = new HelloMessage( 2,  1,  3, new Date().getTime());
        HelloMessage h39 = new HelloMessage( 2,  2,  3, new Date().getTime());
        HelloMessage h40 = new HelloMessage( 2,  3,  3, new Date().getTime());
        HelloMessage h41 = new HelloMessage( 2,  4,  3, new Date().getTime());

        HelloMessage h42 = new HelloMessage( 3,  0,  1, new Date().getTime());
        HelloMessage h43 = new HelloMessage( 3,  1,  1, new Date().getTime());
        HelloMessage h44 = new HelloMessage( 3,  2,  1, new Date().getTime());
        HelloMessage h45 = new HelloMessage( 3,  3,  1, new Date().getTime());
        HelloMessage h46 = new HelloMessage( 3,  4,  1, new Date().getTime());

        HelloMessage h47 = new HelloMessage( 3,  0,  2, new Date().getTime());
        HelloMessage h48 = new HelloMessage( 3,  1,  2, new Date().getTime());
        HelloMessage h49 = new HelloMessage( 3,  2,  2, new Date().getTime());
        HelloMessage h50 = new HelloMessage( 3,  3,  2, new Date().getTime());
        HelloMessage h51 = new HelloMessage( 3,  4,  2, new Date().getTime());

        HelloMessage h52 = new HelloMessage( 3,  0,  3, new Date().getTime());
        HelloMessage h53 = new HelloMessage( 3,  1,  3, new Date().getTime());
        HelloMessage h54 = new HelloMessage( 3,  2,  3, new Date().getTime());
        HelloMessage h55 = new HelloMessage( 3,  3,  3, new Date().getTime());
        HelloMessage h56 = new HelloMessage( 3,  4,  3, new Date().getTime());

        HelloMessage h57 = new HelloMessage( 4,  0,  1, new Date().getTime());
        HelloMessage h58 = new HelloMessage( 4,  1,  1, new Date().getTime());
        HelloMessage h59 = new HelloMessage( 4,  2,  1, new Date().getTime());
        HelloMessage h60 = new HelloMessage( 4,  3,  1, new Date().getTime());
        HelloMessage h61 = new HelloMessage( 4,  4,  1, new Date().getTime());

        HelloMessage h62 = new HelloMessage( 4,  0,  2, new Date().getTime());
        HelloMessage h63 = new HelloMessage( 4,  1,  2, new Date().getTime());
        HelloMessage h64 = new HelloMessage( 4,  2,  2, new Date().getTime());
        HelloMessage h65 = new HelloMessage( 4,  3,  2, new Date().getTime());
        HelloMessage h66 = new HelloMessage( 4,  4,  2, new Date().getTime());

        HelloMessage h67 = new HelloMessage( 4,  0,  3, new Date().getTime());
        HelloMessage h68 = new HelloMessage( 4,  1,  3, new Date().getTime());
        HelloMessage h69 = new HelloMessage( 4,  2,  3, new Date().getTime());
        HelloMessage h70 = new HelloMessage( 4,  3,  3, new Date().getTime());
        HelloMessage h71 = new HelloMessage( 4,  4,  3, new Date().getTime());


        sendHelloMsg(h42);
        sendHelloMsg(h43);
        sendHelloMsg(h44);
        sendHelloMsg(h45);

        sendHelloMsg(h46);
        sendHelloMsg(h47);
        sendHelloMsg(h48);
        sendHelloMsg(h49);
        sendHelloMsg(h50);

        sendHelloMsg(h51);
        sendHelloMsg(h52);
        sendHelloMsg(h53);
        sendHelloMsg(h54);
        sendHelloMsg(h55);

        sendHelloMsg(h56);

        sendHelloMsg(h1);
        sendHelloMsg(h2);
        sendHelloMsg(h3);
        sendHelloMsg(h4);
        sendHelloMsg(h5);

        sendHelloMsg(h6);
        sendHelloMsg(h7);
        sendHelloMsg(h8);
        sendHelloMsg(h9);
        sendHelloMsg(h10);

        sendHelloMsg(h11);
        sendHelloMsg(h12);
        sendHelloMsg(h13);
        sendHelloMsg(h14);
        sendHelloMsg(h15);

        sendHelloMsg(h16);
        sendHelloMsg(h17);
        sendHelloMsg(h18);
        sendHelloMsg(h19);
        sendHelloMsg(h20);

        sendHelloMsg(h21);
        sendHelloMsg(h22);
        sendHelloMsg(h23);
        sendHelloMsg(h24);
        sendHelloMsg(h25);

        sendHelloMsg(h26);
        sendHelloMsg(h27);
        sendHelloMsg(h28);
        sendHelloMsg(h29);
        sendHelloMsg(h30);

        sendHelloMsg(h31);
        sendHelloMsg(h32);
        sendHelloMsg(h33);
        sendHelloMsg(h34);
        sendHelloMsg(h35);

        sendHelloMsg(h36);
        sendHelloMsg(h37);
        sendHelloMsg(h38);
        sendHelloMsg(h39);
        sendHelloMsg(h40);

        sendHelloMsg(h41);
        sendHelloMsg(h42);
        sendHelloMsg(h43);
        sendHelloMsg(h44);
        sendHelloMsg(h45);

        sendHelloMsg(h46);
        sendHelloMsg(h47);
        sendHelloMsg(h48);
        sendHelloMsg(h49);
        sendHelloMsg(h50);

        sendHelloMsg(h51);
        sendHelloMsg(h52);
        sendHelloMsg(h53);
        sendHelloMsg(h54);
        sendHelloMsg(h55);

        sendHelloMsg(h56);
        sendHelloMsg(h57);
        sendHelloMsg(h58);
        sendHelloMsg(h59);
        sendHelloMsg(h60);

        sendHelloMsg(h61);
        sendHelloMsg(h62);
        sendHelloMsg(h63);
        sendHelloMsg(h64);
        sendHelloMsg(h65);

        sendHelloMsg(h66);
        sendHelloMsg(h67);
        sendHelloMsg(h68);
        sendHelloMsg(h69);
        sendHelloMsg(h70);

        sendHelloMsg(h71);

    }


}
