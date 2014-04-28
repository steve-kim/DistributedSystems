package com.distributed.messages;

import java.io.Serializable;

/**
 * Created by prateek on 4/25/14.
 */
public class ElectionMessage extends MessageFactory {


    private int priorityID ;
    private int electionTag ;

    // Constructor for Election Message
    public ElectionMessage(int myPID, int myPriorityID, int electionTag, long timeStamp,
               String msgBuf) {

        this.pid = myPID;
        this.priorityID = myPriorityID;
        this.electionTag = electionTag;

        this.timeStamp = timeStamp;
        this.msgBuf = msgBuf;
    }


    public int getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(int priorityID) {
        this.priorityID = priorityID;
    }

    public int getElectionTag() {
        return electionTag;
    }

    public void setElectionTag(int electionTag) {
        this.electionTag = electionTag;
    }

    @Override
    public String toString() {
        return "ElectionMessage {" +
                "priorityID= " + priorityID +
                ", electionTag= " + electionTag +
                " " + super.toString() + " }";
    }
}
