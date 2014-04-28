package com.distributed.messages;

import java.io.Serializable;

/**
 * Created by prateek on 4/25/14.
 */
public class LeaderMessage extends MessageFactory {


//    private int leaderPriorityID ;
    private int electionID ;

    // Constructor for Leader Message
    public LeaderMessage( int myPID, int electionID, int lid, int leaderPriorityID, long timeStamp) {

        this.pid = myPID;
        this.electionID = electionID;
        this.lid = lid;
        this.leaderPriorityID = leaderPriorityID;
        this.timeStamp = timeStamp;
    }

//    public int getLeaderPriorityID() {
//        return leaderPriorityID;
//    }
//
//    public void setLeaderPriorityID(int leaderPriorityID) {
//        this.leaderPriorityID = leaderPriorityID;
//    }

    public int getElectionID() {
        return electionID;
    }

    public void setElectionID(int electionID) {
        this.electionID = electionID;
    }

    @Override
    public String toString() {
        return "LeaderMessage {" +
                "leaderPriorityID= " + leaderPriorityID +
                ", electionID= " + electionID +
                " " + super.toString() + " }";
    }
}
