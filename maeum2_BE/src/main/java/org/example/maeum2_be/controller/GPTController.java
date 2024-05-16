package org.example.maeum2_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.*;
import org.example.maeum2_be.service.gpt.GPTService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GPTController {
    private final GPTService gptService;

    @PostMapping("/api/main/gpt")
    public ApiResponse<?> processGPT(@RequestBody UserInputDTO userInputDTO) {

        // 클라이언트로부터 받은 사용자 입력 설정
        List<MessageDTO> messageDTOList = new ArrayList<>();
        MessageDTO userMessage = new MessageDTO();
        userMessage.setRole("user");
        userMessage.setContent(userInputDTO.getUserInput());

        messageDTOList.add(userMessage);
        // GPT 처리
        GPTRequestDTO requestDTO = new GPTRequestDTO();
        requestDTO.setMessages(messageDTOList);
        requestDTO.setModel("gpt-3.5-turbo");

        GPTResponseDTO gptResponse = gptService.getResponse(requestDTO);
        List<String> textList = new ArrayList<>();
        for(ChoiceDTO text : gptResponse.getChoices()){
            textList.add(text.getMessage().getContent());
        }

        // JSON 처리


        // 클라이언트로 전송
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json"));

        return ApiResponseGenerator.success(new TextResponseDTO(textList), HttpStatus.OK);
    }
}