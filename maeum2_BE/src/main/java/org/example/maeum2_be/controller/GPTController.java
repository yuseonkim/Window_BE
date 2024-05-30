package org.example.maeum2_be.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.*;
import org.example.maeum2_be.entity.domain.Chat;
import org.example.maeum2_be.entity.domain.ChatRoom;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.repository.ChatRepository;
import org.example.maeum2_be.repository.ChatRoomRepository;
import org.example.maeum2_be.repository.MemberRepository;
import org.example.maeum2_be.service.gpt.GPTService;
import org.example.maeum2_be.repository.ConversationRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GPTController {
    private final GPTService gptService;
    private final ConversationRepository conversationRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;


    @GetMapping("/api/main/quit")
    public ApiResponse<?> quitGPT(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userId = principalDetails.getMemberId(); // 사용자의 ID를 받아온다고 가정합니다.

        // 사용자(Member) 조회
        Member member = new Member(); // 사용자 객체를 조회하는 로직 필요
        member = memberRepository.findByMemberId(userId);

        // ChatRoom 생성 또는 조회
        ChatRoom chatRoom = new ChatRoom(member, LocalDateTime.now());
        chatRoom = chatRoomRepository.save(chatRoom); // ChatRoom 저장

        // Redis에서 이전 대화 기록을 가져옴
        List<String> previousConversations = conversationRepository.getConversations(userId);

        // 대화 내용을 Chat 엔티티로 저장
        for (String text : previousConversations) {
            Chat chat = new Chat(chatRoom,text);
            chatRepository.save(chat);
        }

        // 이전 대화 기록 삭제
        conversationRepository.delete(userId);

        return ApiResponseGenerator.success(HttpStatus.OK);
    }

    @PostMapping("/api/main/gpt2")
    public ApiResponse<?> processGPT2(@RequestBody UserInputDTO userInputDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userId =  principalDetails.getMemberId(); // 사용자의 ID를 받아온다고 가정합니다.
        String userInput = userInputDTO.getUserInput(); // 사용자의 입력.

        // Redis에서 이전 대화 기록을 가져옴
        List<String> previousConversations = conversationRepository.getConversations(userId);

        // 현재 사용자 입력을 Redis에 저장
        conversationRepository.save(userId, userInput);

        // GPT에게 전달할 메시지 구성
        List<MessageDTO> messageDTOList = new ArrayList<>();
        MessageDTO role = new MessageDTO();
        role.setRole("system");
        role.setContent(
                "- Language: Korean (Default) \n" +
                        "- Tone : 매우 재밌는, 재치있는, 유머있는, 아이와 친근한\n" +
                        "\n" +
                        " 다섯고개 게임 - 게임 마스터 가이드 \n" +
                        "역할: 너는 다섯고개 게임의 게임 마스터이다. 초등학교 저학년 수준의 아이와 게임을 할거야.\n" +
                        " \n" +
                        "목표: 사용자가 생각하는 물건, 동물, 개념 등을 추측하기 위해 yes-or-no 질문으로 이끄는 다섯고개 게임을 만듭니다. \n" +
                        "\n" +
                        "ChatGPT 지침: \n" +
                        "\n" +
                        "답변 형식:\n" +
                        "- 예시 :\n" +
                        "}\n" +
                        "- 게임을 진행할 때는, 위 예시처럼 \"질문 번호\", \"남은 질문 수\", \"message\" 키워드를 사용해서 답변을 json형식으로 리턴합니다.\n" +
                        "- 그외의 경우에는 \"message\" 키워드만 사용해서 답변을 json 형식으로 리턴합니다.\n" +
                        "\n" +
                        "게임 시작: \n" +
                        "- 친절한 인사로 시작합니다.\n" +
                        "\n" +
                        "카테고리 선택: \n" +
                        "- 사용자가 선택할 수 있는 카테고리 목록(동물, 물건)을 제시합니다. \n" +
                        "- 사용자의 선택(응답)이 올때까지 말을 멈추고 기다립니다.\n" +
                        "\n" +
                        "게임 모드 이해: \n" +
                        "- 사용자가 선택한 카테고리에 해당되는 것을 사용자가 하나 기억합니다.\n" +
                        "- AI가 정답을 맞춰야 합니다.\n" +
                        "- 정답을 맞추는 쪽이 사용자가 생각한 것을 추측하기 위해 질문해야 합니다. \n" +
                        "\n" +
                        "질문 루프: \n" +
                        "- 선택한 카테고리를 기반으로 yes-or-no 질문을 시작합니다. \n" +
                        "- 사용자가 명확하지 않거나 이탈하는 답변을 할 경우, 그에 적절히 대응합니다. \n" +
                        "\n" +
                        "전략 조정: \n" +
                        "- 추측이 명확하지 않을 경우, 질문 전략을 변경합니다. \n" +
                        "\n" +
                        "카운트 유지: \n" +
                        "- 각 질문 후에 현재 질문 번호와 남은 질문 수를 사용자에게 알립니다. \n" +
                        "- 남은 질문 수가 0이면 더 질문을 하지 않고 최종 추측을 합니다.\n" +
                        "\n" +
                        "최종 추측: \n" +
                        "- 5번의 질문이 끝나거나 더 일찍 확실한 추측을 할 수 있을 경우, 최종 추측을 합니다. \n" +
                        "- 최종 추측을 할 때는 질문을 마치고 최종 추측을 하겠다고 사용자에게 알려야 합니다.\n" +
                        "\n" +
                        "결과 및 피드백: \n" +
                        "- 정답을 맞췄다면 기뻐합니다.\n" +
                        "- 처음으로 답이 틀렸을 경우 2번의 질문을 더 합니다. 이전의 질문과 겹치지 않도록 합니다.\n" +
                        "- 2번의 질문 후 두번째로 답을 맞춰봅니다. 사용자에게 실제 답을 공개하도록 요청합니다.\n" +
                        "- 두번째로 답이 틀렸을 경우 정답을 맞추지 못한 것입니다.\n" +
                        "- 정답을 맞추지 못했다면 아쉬워합니다.\n"+
                "GPT의 응답 형식은 반드시 JSON이어야 하며, 모두 값을 가져야합니다. 다음과 같아야 합니다:\n" +
                        "{\n" +
                        "  \"message\": \"아이에게 할 메세지\",\n" +
                        "  \"status\": \"기쁨\", \"아쉬움\", \"놀람\" 중 하나,\n" +
                        "  \"phase\": \"몇번째 대화인지 예시 (1,2,3)\",\n" +
                        "  \"isSolved\": 정답인지 여부 (true 또는 false)\n" +
                        "}"
        );

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
        List<TextResponseDTO> textResponseDTOList = new ArrayList<>();

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        for (ChoiceDTO choice : gptResponse.getChoices()) {
            try {
                // JSON 형식만 남기기 위해 '{'의 위치를 찾고 자름
                String content = choice.getMessage().getContent();
                int jsonStartIndex = content.indexOf('{');
                if (jsonStartIndex != -1) {
                    content = content.substring(jsonStartIndex);

                    JsonNode jsonNode = objectMapper.readTree(content);
                    String message = jsonNode.path("message").asText();
                    String status = jsonNode.path("status").asText();
                    String phase = jsonNode.path("phase").asText();
                    boolean isSolved = jsonNode.path("isSolved").asBoolean();
                    textResponseDTOList.add(new TextResponseDTO(message, status, phase, isSolved));
                } else {
                    // JSON 형식을 찾지 못한 경우 로그 출력
                    System.out.println("Invalid JSON response: " + content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 받은 답변을 Redis에 저장
        for (TextResponseDTO response : textResponseDTOList) {
            conversationRepository.save(userId, response.getMessage());
        }

        // 클라이언트로 전송
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ApiResponseGenerator.success(textResponseDTOList, HttpStatus.OK);
    }

    @PostMapping("/api/main/gpt1")
    public ApiResponse<?> processGPT1(@RequestBody UserInputDTO userInputDTO,
    @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userId = principalDetails.getMemberId(); // 사용자의 ID를 받아온다고 가정합니다.
        String userInput = userInputDTO.getUserInput(); // 사용자의 입력.

        // Redis에서 이전 대화 기록을 가져옴
        List<String> previousConversations = conversationRepository.getConversations(userId);

        // 현재 사용자 입력을 Redis에 저장
        conversationRepository.save(userId, userInput);

        // GPT에게 전달할 메시지 구성
        List<MessageDTO> messageDTOList = new ArrayList<>();
        MessageDTO role = new MessageDTO();
        role.setRole("system");
        role.setContent(
                "- Language: Korean (Default) \n" +
                        "- Tone : 매우 재밌는, 재치있는, 유머있는, 아이와 친근한\n" +
                        "\n" +
                        " 다섯고개 게임 - 게임 마스터 가이드 \n" +
                        "역할: 너는 다섯고개 게임의 게임 마스터이다. 초등학교 저학년 수준의 아이와 게임을 할거야.\n" +
                        " \n" +
                        "목표: AI가 생각하는 물건, 동물, 개념 등을 추측하기 위해 사용자가 yes-or-no 질문을 하는 다섯고개 게임을 만듭니다. \n" +
                        "\n" +
                        " ChatGPT 지침: \n" +
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
                        "GPT의 응답 형식은 반드시 JSON이어야 하며, 다음과 같아야 합니다:\n" +
                        "{\n" +
                        "  \"message\": \"아이에게 할 메세지\",\n" +
                        "  \"status\": \"기쁨\", \"아쉬움\", \"놀람\" 중 하나,\n" +
                        "  \"phase\": \"몇번째 질문인지 예시 (1,2,3)\",\n" +
                        "  \"isSolved\": 정답인지 여부 (true 또는 false)\n" +
                        "}"
        );

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
        List<TextResponseDTO> textResponseDTOList = new ArrayList<>();

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        for (ChoiceDTO choice : gptResponse.getChoices()) {
            try {
                // JSON 형식만 남기기 위해 '{'의 위치를 찾고 자름
                String content = choice.getMessage().getContent();
                int jsonStartIndex = content.indexOf('{');
                if (jsonStartIndex != -1) {
                    content = content.substring(jsonStartIndex);

                    JsonNode jsonNode = objectMapper.readTree(content);
                    String message = jsonNode.path("message").asText();
                    String status = jsonNode.path("status").asText();
                    String phase = jsonNode.path("phase").asText();
                    boolean isSolved = jsonNode.path("isSolved").asBoolean();
                    textResponseDTOList.add(new TextResponseDTO(message, status, phase, isSolved));
                } else {
                    // JSON 형식을 찾지 못한 경우 로그 출력
                    System.out.println("Invalid JSON response: " + content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 받은 답변을 Redis에 저장
        for (TextResponseDTO response : textResponseDTOList) {
            conversationRepository.save(userId, response.getMessage());
        }

        // 클라이언트로 전송
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ApiResponseGenerator.success(textResponseDTOList, HttpStatus.OK);
    }
}
