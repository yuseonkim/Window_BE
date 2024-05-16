package org.example.maeum2_be.dto;

import java.util.List;

public class TextResponseDTO {
    private List<String> texts;

    public TextResponseDTO() {}

    public TextResponseDTO(List<String> texts) {
        this.texts = texts;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}
