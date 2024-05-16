package org.example.maeum2_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.*;
import org.example.maeum2_be.service.gpt.GPTService;
import org.example.maeum2_be.repository.ConversationRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GPTController {
    private final GPTService gptService;
    private final ConversationRepository conversationRepository;

    @PostMapping("/api/main/gpt")
    public ApiResponse<?> processGPT(@RequestBody UserInputDTO userInputDTO) {
        String userId = "1"; // 사용자의 ID를 받아온다고 가정합니다.
        String userInput = userInputDTO.getUserInput(); // 사용자의 입력.

        // Redis에서 이전 대화 기록을 가져옴
        List<String> previousConversations = conversationRepository.getConversations(userId);

        // 현재 사용자 입력을 Redis에 저장
        conversationRepository.save(userId, userInput);

        // GPT에게 전달할 메시지 구성
        List<MessageDTO> messageDTOList = new ArrayList<>();
        MessageDTO role = new MessageDTO();
        role.setRole("system");
        role.setContent("");

        messageDTOList.add(role);

        for (String message : previousConversations) {
            MessageDTO previousMessage = new MessageDTO();
            previousMessage.setRole("user");
            previousMessage.setContent(message);
            messageDTOList.add(previousMessage);
        }

        MessageDTO userMessage = new MessageDTO();
        userMessage.setRole("user");
        userMessage.setContent(userInput);
        messageDTOList.add(userMessage);

        // GPT 요청 구성
        GPTRequestDTO requestDTO = new GPTRequestDTO();
        requestDTO.setMessages(messageDTOList);
        requestDTO.setModel("gpt-3.5-turbo");

        // GPT 처리
        GPTResponseDTO gptResponse = gptService.getResponse(requestDTO);
        List<String> textList = new ArrayList<>();
        for (ChoiceDTO text : gptResponse.getChoices()) {
            textList.add(text.getMessage().getContent());
        }

        // 받은 답변을 Redis에 저장
        for (String response : textList) {
            conversationRepository.save(userId, response);
        }

        // 클라이언트로 전송
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json"));

        return ApiResponseGenerator.success(new TextResponseDTO(textList), HttpStatus.OK);
    }
}
