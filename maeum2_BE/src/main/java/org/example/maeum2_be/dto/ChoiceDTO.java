package org.example.maeum2_be.dto;

public class ChoiceDTO {
    private int index;
    private MessageDTO message;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public MessageDTO getMessage() {
        return message;
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }
}