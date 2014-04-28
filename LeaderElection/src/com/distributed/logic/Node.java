package com.distributed.logic;

import com.distributed.utils.NodeAttributes;
import java.util.Date;

/**
 * Created by prateek on 4/25/14.
 */
public class Node {
    private static final int INIT_VAL = -1;

    NodeAttributes node = NodeAttributes.getInstance();



    // Initialize this server...
    public void nodeInit(int myPId){

        node.setMyPID(myPId);
        node.setMyPriorityID(INIT_VAL);
        node.setMyLeaderID(INIT_VAL);
        node.setMyLeaderPriorityID(INIT_VAL);
        node.setMyParentID(INIT_VAL);
        node.setElectionTag(INIT_VAL);
        node.setChildVotes(null);
        node.setTime(new Date());

    }




    //    TODO: This method should calculate the my new priorty based on the GPS data...
    public int updateMyPriority(){
        return 0;
    }

}
