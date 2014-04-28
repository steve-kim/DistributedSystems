package com.distributed.utils;

import java.util.Date;
import java.util.Hashtable;

/**
 * Created by prateek on 4/23/14.
 * This class defines the identity of the Node...
 */
public class NodeAttributes {
    private int myPID;
    private int myPriorityID;
    private int myLeaderID;
    private int myLeaderPriorityID;
    private int myParentID;
    private int electionTag; // To keep track of current election
    private int electionID; // To reflect when the leader was elected

    private long lastContactWithParent;

    protected Hashtable childVotes = new Hashtable<Integer, Integer>(); // To keep track of who voted for who?

    private IntLinkedList child = new IntLinkedList();
    private Date time;

    private static final int INIT_VAL = -1;
    private static final int INIT_PRIORITY = 10000;
    private static final int INIT_MY_PRIORITY = 3000;
    private static final NodeAttributes singleton = new NodeAttributes();

    public NodeAttributes() {
    }

    public static NodeAttributes getInstance() {
        return singleton;
    }


//    public NodeAttributes(int myPId, int myPriorityID, int myLeaderID, int myLeaderPriorityID, int myParentID, IntLinkedList childVotes) {
//        this.myPID = myPId;
//        this.myPriorityID = myPriorityID;
//        this.myLeaderID = myLeaderID;
//        this.myLeaderPriorityID = myLeaderPriorityID;
//        this.myParentID = myParentID;
//        this.childVotes = childVotes;
//    }


    //    Unique PID of the node, as we get it from Lowerlevel
    public int getMyPID() {
        return myPID;
    }

    public void setMyPID(int myPID) {
        this.myPID = myPID;
    }

    public int getMyPriorityID() {
        return myPriorityID;
    }

    public void setMyPriorityID(int myPriorityID) {
        this.myPriorityID = myPriorityID;
    }

    public int getMyLeaderID() {
        return myLeaderID;
    }

    public void setMyLeaderID(int myLeaderID) {
        this.myLeaderID = myLeaderID;
    }

    public int getMyLeaderPriorityID() {
        return myLeaderPriorityID;
    }

    public void setMyLeaderPriorityID(int myLeaderPriorityID) {
        this.myLeaderPriorityID = myLeaderPriorityID;
    }

    public int getMyParentID() {
        return myParentID;
    }

    public void setMyParentID(int myParentID) {
        this.myParentID = myParentID;
    }

    public boolean hasParent(){
        if(this.getMyParentID() > 0)
            return true;
        return false;
    }

    public Hashtable getChildVotes() {
        return childVotes;
    }

    public void setChildVotes(Hashtable childVotes) {
        this.childVotes = childVotes;
    }

    public void addChildVote(int childID, int voteLeader) {
        childVotes.put(childID, voteLeader);
    }

    public IntLinkedList getChild() {
        return child;
    }

    public void setChild(IntLinkedList child) {
        this.child = child;
    }

    public void addChild(int c) {
        child.add(c);
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getElectionTag() {
        return electionTag;
    }

    public void setElectionTag(int electionTag) {
        this.electionTag = electionTag;
    }

    public int updateElectionTag() {
        this.setElectionTag(this.getElectionTag() + 1);
        return this.getElectionTag();

    }

    public int getElectionID() {
        return electionID;
    }

    public void setElectionID(int electionID) {
        this.electionID = electionID;
    }

    public long getLastContactWithParent() {
        return lastContactWithParent;
    }

    public void setLastContactWithParent(long lastContactWithParent) {
        this.lastContactWithParent = lastContactWithParent;
    }


    // Initialize this server...
    public void nodeInit(int myPId){

        this.setMyPID(myPId);
        this.setMyPriorityID(INIT_MY_PRIORITY);
        this.setMyLeaderID(INIT_VAL);
        this.setMyLeaderPriorityID(INIT_PRIORITY);
        this.setMyParentID(INIT_VAL);
        this.setElectionTag(INIT_VAL);
        this.setElectionID(INIT_VAL);
        this.setChildVotes(null);
        this.setTime(new Date());
//        this.addChild(1);

    }

    @Override
    public String toString() {
        return "NodeAttributes {" +
                "myPID= " + myPID +
                ", myPriorityID= " + myPriorityID +
                ", myLeaderID= " + myLeaderID +
                ", myLeaderPriorityID= " + myLeaderPriorityID +
                ", myParentID= " + myParentID +
                ", electionTag= " + electionTag +
                ", electionID= " + electionID +
                ", lastContactWithParent= " + lastContactWithParent +
                ", childVotes= " + childVotes +
                ", child= " + child +
                ", time= " + time +
                " }";
    }
}
