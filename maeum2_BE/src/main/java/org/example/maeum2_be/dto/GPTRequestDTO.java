package org.example.maeum2_be.dto;

public class GPTRequestDTO {
    private MessageDTO message;  // 사용자 입력은 단일 message
    private String model;

    public MessageDTO getMessage() {
        return message;
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}