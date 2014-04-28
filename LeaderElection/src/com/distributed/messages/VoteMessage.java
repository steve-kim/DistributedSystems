package com.distributed.messages;

import java.io.Serializable;

/**
 * Created by prateek on 4/25/14.
 */
public class VoteMessage extends MessageFactory {

//    private int leaderPriorityID ;
    private int myVote2lid;
    private int electionID ;

    // Constructor for VOTE Message
    public VoteMessage( int myPID , int myVote2lid, int leaderPriorityID, int electionID, long date)  {
        this.pid = myPID;
        this.myVote2lid = myVote2lid;
        this.leaderPriorityID = leaderPriorityID;
        this.electionID = electionID;
        this.timeStamp = date;
    }

//    public int getLeaderPriorityID() {
//        return leaderPriorityID;
//    }
//
//    public void setLeaderPriorityID(int leaderPriorityID) {
//        this.leaderPriorityID = leaderPriorityID;
//    }

    public int getMyVote2lid() {
        return myVote2lid;
    }

    public void setMyVote2lid(int myVote2lid) {
        this.myVote2lid = myVote2lid;
    }

    public int getElectionID() {
        return electionID;
    }

    public void setElectionID(int electionID) {
        this.electionID = electionID;
    }

    @Override
    public String toString() {
        return "VoteMessage {" +
                "leaderPriorityID= " + leaderPriorityID +
                ", myVote2lid= " + myVote2lid +
                ", electionID= " + electionID +
                " " + super.toString() + " }";
    }
}
