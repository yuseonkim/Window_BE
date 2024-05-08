package org.example.maeum2_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.dto.GPTRequestDTO;
import org.example.maeum2_be.dto.GPTResponseDTO;
import org.example.maeum2_be.dto.MessageDTO;
import org.example.maeum2_be.dto.UserInputDTO;
import org.example.maeum2_be.service.gpt.GPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GPTController {
    private final GPTService gptService;

    @PostMapping("/api/main/gpt")
    public String processGPT(@RequestBody UserInputDTO userInputDTO) {

        // 클라이언트로부터 받은 사용자 입력 설정
        MessageDTO userMessage = new MessageDTO();
        userMessage.setRole("user");
        userMessage.setContent(userInputDTO.getUserInput());

        // GPT 처리
        GPTRequestDTO requestDTO = new GPTRequestDTO();
        requestDTO.setMessage(userMessage);
        requestDTO.setModel("gpt-3.5-turbo");

        GPTResponseDTO gptResponse = gptService.getResponse(requestDTO);
        String text = gptResponse.getChoices().getMessage().getContent();

        // JSON 처리


        // 클라이언트로 전송
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json"));

        return text;
    }
}