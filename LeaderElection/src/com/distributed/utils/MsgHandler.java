package com.distributed.utils;

import com.distributed.messages.MessageFactory;

import java.io.IOException;

/**
 * Created by prateek on 4/23/14.
 */
public interface MsgHandler {
    public void handleMsg(MessageFactory m, String srcId);
//    public Msg receiveMsg(Msg m, String srcId);
}
