package me.dzikimlecz.touchclient.model.container;


import java.util.List;

abstract class Container<E> {
    private int index;
    private int numberOfPages;
    private int size;

    public Container(int index, int numberOfPages, int size) {
        this.index = index;
        this.numberOfPages = numberOfPages;
        this.size = size;
    }
    public Container() {}

    public int getIndex() {
        return index;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public int getSize() {
        return size;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public abstract List<E> getElements();
}
