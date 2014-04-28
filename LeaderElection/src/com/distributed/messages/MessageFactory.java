package com.distributed.messages;

import java.io.Serializable;

/**
 * Created by prateek on 4/25/14.
 */
public abstract class MessageFactory implements Serializable {
    //    public Boolean[] visited ;
    public int pid ;
    public int lid ;
    public int leaderPriorityID ;


    public long timeStamp ;

    public String msgBuf ;
    public Object serverData ; // used in registration messages

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMsgBuf() {
        return msgBuf;
    }

    public void setMsgBuf(String msgBuf) {
        this.msgBuf = msgBuf;
    }

    public Object getServerData() {
        return serverData;
    }

    public void setServerData(Object serverData) {
        this.serverData = serverData;
    }

    public int getLeaderPriorityID() {
        return leaderPriorityID;
    }

    public void setLeaderPriorityID(int leaderPriorityID) {
        this.leaderPriorityID = leaderPriorityID;
    }

    @Override
    public String toString() {
        return "MessageFactory{" +
                "pid= " + pid +
                ", lid= " + lid +
                ", leaderPriorityID= " + leaderPriorityID +
                ", timeStamp= " + timeStamp +
                ", msgBuf= '" + msgBuf + '\'' +
                ", serverData= " + serverData +
                " }";
    }
}

