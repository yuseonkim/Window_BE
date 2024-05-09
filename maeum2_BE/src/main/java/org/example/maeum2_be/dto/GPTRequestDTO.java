package org.example.maeum2_be.dto;

public class GPTRequestDTO {
    private MessageDTO messages;
    private String model;

    public MessageDTO getMessages() {
        return messages;
    }

    public void setMessages(MessageDTO userMessage) {
        this.messages = userMessage;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}