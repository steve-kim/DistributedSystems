package com.distributed.test;

import com.distributed.logic.*;
import java.io.IOException;
import android.util.Log;


/**
 * Created by prateek on 4/26/14.
 */
public class TestRun {
    static NodeTest server = NodeTest.getInstance();
    static SpanningTree st = new SpanningTree();

    public static void main(String[] args) throws IOException {

        server.setMyPID(Integer.valueOf(args[0]));
        server.nodeInit(Integer.valueOf(args[0]));
        // Get all the known servers...
        server.serverFileParser(args[1]);

//        if(server.getMyPID() == 3){
            server.startMyServerInstance();
//        }
//        else{
            st.beacon();
//            st.testCasesHello();
//            st.testCasesElect();
//            st.testCasesVote();
//        }
    }




}
