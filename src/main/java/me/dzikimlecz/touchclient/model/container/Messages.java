package me.dzikimlecz.touchclient.model.container;

import me.dzikimlecz.touchclient.model.Message;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class Messages extends Container<Message> {
    public Messages() {
        super();
    }

    private List<Message> elements;

    public Messages(int index, int numberOfPages, List<Message> elements, int size) {
        super(index, numberOfPages, size);
        this.elements = elements;
    }

    @Override
    public List<Message> getElements() {
        return elements;
    }

    public void setElements(List<Message> elements) {
        this.elements = List.copyOf(elements);
    }
}
