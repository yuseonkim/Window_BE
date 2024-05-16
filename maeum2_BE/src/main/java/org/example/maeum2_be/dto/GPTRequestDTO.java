package org.example.maeum2_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GPTRequestDTO {
    private List<MessageDTO > messages;
    private String model;


}