package org.example.maeum2_be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GPTController {
    private final GPTService gptService;

    @PostMapping("/api/main/gpt")
    public String processGPT(@RequestBody String userInput) {

        // 클라이언트로부터 받은 사용자 입력 설정
        MessageDTO userMessage = new MessageDTO();
        userMessage.setRole("user");
        userMessage.setContent(userInput);

        // GPT 처리
        GPTRequestDTO requestDTO = new GPTRequestDTO();
        requestDTO.setMessage(userMessage);
        requestDTO.setModel("gpt-3.5-turbo");

        GPTResponseDTO gptResponse = GPTService.getResponse(requestDTO);
        String text = gptResponse.getChoices().getMessage().getContent();

        // JSON 처리


        // 클라이언트로 전송
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json"));

        return text;
    }
}