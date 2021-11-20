package me.dzikimlecz.touchclient.mainview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import me.dzikimlecz.touchclient.model.Message;

public class NoSelectionModel extends MultipleSelectionModel<Message> {
    private NoSelectionModel() {
        super();
    }

    private static NoSelectionModel instance;

    public static NoSelectionModel getInstance() {
        if (instance == null) instance = new NoSelectionModel();
        return instance;
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return FXCollections.observableArrayList();
    }

    @Override
    public ObservableList<Message> getSelectedItems() {
        return FXCollections.observableArrayList();
    }

    @Override
    public void selectIndices(int index, int... indices) {
    }

    @Override
    public void selectAll() {
    }

    @Override
    public void selectFirst() {
    }

    @Override
    public void selectLast() {
    }

    @Override
    public void clearAndSelect(int index) {
    }

    @Override
    public void select(int index) {
    }

    @Override
    public void select(Message obj) {
    }

    @Override
    public void clearSelection(int index) {
    }

    @Override
    public void clearSelection() {
    }

    @Override
    public boolean isSelected(int index) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void selectPrevious() {
    }

    @Override
    public void selectNext() {
    }
}
