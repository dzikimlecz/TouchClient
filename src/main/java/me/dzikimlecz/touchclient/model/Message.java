package me.dzikimlecz.touchclient.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public final class Message {
    public UserProfile getSender() {
        return sender;
    }
    private final UserProfile sender;

    public UserProfile getRecipient() {
        return recipient;
    }
    private final UserProfile recipient;

    public String getContent() {
        return content;
    }
    private final String content;

    public LocalDateTime getSentOn() {
        return sentOn;
    }
    private final LocalDateTime sentOn;

    public Message(
            @NotNull UserProfile sender,
            @NotNull UserProfile recipient,
            @NotNull String content,
            @NotNull LocalDateTime sentOn) {
        this.sender = sender;
        this.recipient = recipient;
        this.sentOn = sentOn;
        if (content.isEmpty())
            throw new IllegalArgumentException("Content of the message can't be empty.");
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != Message.class) return false;

        Message message = (Message) o;

        if (!sender.equals(message.sender)) return false;
        if (!recipient.equals(message.recipient)) return false;
        if (!content.equals(message.content)) return false;
        return sentOn.equals(message.sentOn);
    }

    @Override
    public int hashCode() {
        int result = sender.hashCode();
        result = 31 * result + recipient.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + sentOn.hashCode();
        return result;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Message{" +
                "sender=" + sender +
                ", recipient=" + recipient +
                ", sentOn=" + sentOn +
                ", content='" + content + '\'' +
                '}';
    }
}
