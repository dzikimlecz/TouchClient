package me.dzikimlecz.touchclient.mainview;

import me.dzikimlecz.touchclient.model.Message;

@FunctionalInterface
public interface MessageSender {
    void send(Message msg);
}
