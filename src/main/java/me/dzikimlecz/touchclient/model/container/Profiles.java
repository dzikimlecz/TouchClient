package me.dzikimlecz.touchclient.model.container;

import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class Profiles extends Container<UserProfile> {
    private List<UserProfile> elements;

    public Profiles(int index, int numberOfPages, List<UserProfile> elements, int size) {
        super(index, numberOfPages, size);
        this.elements = List.copyOf(elements);
    }

    public void setElements(List<UserProfile> elements) {
        this.elements = List.copyOf(elements);
    }

    public Profiles() {
        super();
    }

    @Override
    public @Unmodifiable List<UserProfile> getElements() {
        return elements;
    }
}
