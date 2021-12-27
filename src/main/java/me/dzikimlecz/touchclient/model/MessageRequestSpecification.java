package me.dzikimlecz.touchclient.model;

import java.util.List;

public class MessageRequestSpecification {
    private List<String> nameTags;
    private int page;
    private int size;

    public MessageRequestSpecification(List<String> nameTags, int page, int size) {
        this.nameTags = nameTags;
        this.page = page;
        this.size = size;
    }

    public MessageRequestSpecification() {
    }

    public List<String> getNameTags() {
        return nameTags;
    }

    public void setNameTags(List<String> nameTags) {
        this.nameTags = nameTags;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
