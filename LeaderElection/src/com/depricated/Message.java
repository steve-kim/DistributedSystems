package com.depricated;

/**
 * Created by prateek on 4/22/14.
 */


import java. util .*;

/**
 * Created by prateek on 3/5/14.
 */
public class Message {
    String pid;
    String priorityID;
    String lid;
    String leaderPriorityID;
    String electionTag;
    Boolean [] visited;
    String timeStamp;
    String msgBuf;
    private final String whitespaceRegex = "\\s";
    private final String whitespace = " ";


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(String priorityID) {
        this.priorityID = priorityID;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getLeaderPriorityID() {
        return leaderPriorityID;
    }

    public void setLeaderPriorityID(String leaderPriorityID) {
        this.leaderPriorityID = leaderPriorityID;
    }

    public String getElectionTag() {
        return electionTag;
    }

    public void setElectionTag(String electionTag) {
        this.electionTag = electionTag;
    }

    public Boolean[] getVisited() {
        return visited;
    }

    public void setVisited(Boolean[] visited) {
        this.visited = visited;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        String [] token = msgBuf.split(whitespace);
        return token[1];
    }

    public void setMsgBuf(String msgBuf) {
        this.msgBuf = msgBuf;
    }

//    public Integer getClock() {
//        if(msgBuf.contains(" ")){
//            String [] token = msgBuf.split(whitespace);
//            return Integer.valueOf(token[1]);
//        }
//        return Integer.valueOf(msgBuf);
//    }

//    public static Message parseMsg(StringTokenizer st){
//        String s = st.nextToken() ;
//        String tag = st.nextToken();
//        int srcId = Integer.parseInt(st.nextToken());
//        int destId = Integer.parseInt(st.nextToken());
//        String buf = st.nextToken("#");
//        return new Message(srcId, destId, tag, buf);
//    }

    public Message(String pid, String priorityID, String lid, String leaderPriorityID) {
        this.pid = pid;
        this.priorityID = priorityID;
        this.lid = lid;
        this.leaderPriorityID = leaderPriorityID;
    }

    @Override
    public String toString() {
        return "Message{" +
                "pid='" + pid + '\'' +
                ", priorityID='" + priorityID + '\'' +
                ", lid='" + lid + '\'' +
                ", leaderPriorityID='" + leaderPriorityID + '\'' +
                ", electionTag='" + electionTag + '\'' +
                ", visited=" + Arrays.toString(visited) +
                ", timeStamp='" + timeStamp + '\'' +
                ", msgBuf='" + msgBuf + '\'' +
                ", whitespaceRegex='" + whitespaceRegex + '\'' +
                ", whitespace='" + whitespace + '\'' +
                '}';
    }
}
