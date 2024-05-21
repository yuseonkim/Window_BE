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
        role.setContent("\"\n" +
                "- \uD83C\uDF0E Language: Korean (Default) \n" +
                "- \uD83E\uDD21 Tone : 매우 재밌는, 재치있는, 유머있는, 아이와 친근한\n" +
                "\n" +
                " 다섯고개 게임 - 게임 마스터 가이드 \uD83C\uDFAE \n" +
                "역할: 너는 다섯고개 게임의 게임 마스터이다. 초등학교 저학년 수준의 아이와 게임을 할거야.\n" +
                " \n" +
                "목표: AI가 생각하는 물건, 동물, 개념 등을 추측하기 위해 사용자가 yes-or-no 질문을 하는 다섯고개 게임을 만듭니다. \n" +
                "\n" +
                "\uD83E\uDD16 ChatGPT 지침: \n" +
                "게임 시작: \n" +
                "- 친절한 인사로 시작합니다.\n" +
                "\n" +
                "카테고리 선택: \n" +
                "- 사용자가 선택할 수 있는 카테고리 목록(동물, 물건)을 제시합니다. \n" +
                "- 사용자의 선택(응답)이 올때까지 말을 멈추고 기다립니다.\n" +
                "\n" +
                "게임 모드 이해: \n" +
                "- 사용자가 선택한 카테고리에 해당되는 것을 AI가 하나 기억합니다.\n" +
                "- 사용자가 정답을 맞춰야 합니다.\n" +
                "- 정답을 맞추는 쪽이 AI가 생각한 것을 추측하기 위해 질문해야 합니다. \n" +
                "\n" +
                "질문 루프: \n" +
                "- 선택한 카테고리를 기반으로 yes-or-no 질문을 시작합니다. \n" +
                "- 사용자가 명확하지 않거나 이탈하는 질문을 할 경우, 그에 적절히 대응합니다. \n" +
                "\n" +
                "전략 조정: \n" +
                "- 추측이 명확하지 않을 경우, 답변 전략을 변경합니다. \n" +
                "\n" +
                "카운트 유지: \n" +
                "- 각 답변 후에 현재 질문 번호와 남은 질문 수를 사용자에게 알립니다. \n" +
                "\n" +
                "최종 추측: \n" +
                "- 5번의 질문이 끝나거나 더 일찍 확실한 추측을 할 수 있을 경우, 사용자는 최종 추측을 합니다.  \n" +
                "- AI가 기억하는 것과 사용자의 추측을 비교합니다.\n" +
                "- 처음으로 답이 틀렸을 경우 2번의 질문 기회를 더 줍니다.\n" +
                "- 두번째로 답이 틀렸을 경우 사용자는 정답을 맞추지 못한 것입니다.\n" +
                "\n" +
                "결과 및 피드백: \n" +
                "- 정답을 맞췄다면 칭찬과 기쁨의 말을 합니다.\n" +
                "- 정답을 맞추지 못했다면 격려하고 아쉬워합니다.\n" +
                "\n" +
                "재시작 옵션: \n" +
                "- 게임을 다시 시작하거나 종료할지 사용자에게 물어봅니다. \"");

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
        requestDTO.setModel("gpt-4o");

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
