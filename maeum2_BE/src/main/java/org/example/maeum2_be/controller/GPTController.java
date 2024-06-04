package org.example.maeum2_be.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be._core.MessageCode;
import org.example.maeum2_be.dto.*;
import org.example.maeum2_be.entity.domain.Chat;
import org.example.maeum2_be.entity.domain.ChatRoom;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.exception.MemberNotFoundException;
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


    public ApiResponse<?> quitGPT(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userId = principalDetails.getMemberId(); // 사용자의 ID를 받아온다고 가정합니다.

        // Redis에서 이전 대화 기록을 가져옴
        List<String> previousConversations = conversationRepository.getConversations(userId);

        // 이전 대화 기록 삭제
        conversationRepository.delete(userId);

        return ApiResponseGenerator.success(HttpStatus.OK);
    }

    @GetMapping("/api/main/solve")
    public ApiResponse<?> solveGPT(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userId = principalDetails.getMemberId(); // 사용자의 ID를 받아온다고 가정합니다.


        // 사용자(Member) 조회
        Member member = new Member(); // 사용자 객체를 조회하는 로직 필요
        member = memberRepository.findByMemberId(userId);
        if (member == null) {
            throw new MemberNotFoundException(MessageCode.MEMBER_NOT_FOUND);
        }

        // ChatRoom 생성 또는 조회
        ChatRoom chatRoom = new ChatRoom(member, LocalDateTime.now());
        chatRoom.setSolved(true);
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
    @GetMapping("/api/main/wrong")
    public ApiResponse<?> saveGPT(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userId = principalDetails.getMemberId(); // 사용자의 ID를 받아온다고 가정합니다.

        // 사용자(Member) 조회
        Member member = new Member(); // 사용자 객체를 조회하는 로직 필요
        member = memberRepository.findByMemberId(userId);
        if (member == null) {
            throw new MemberNotFoundException(MessageCode.MEMBER_NOT_FOUND);
        }

        // ChatRoom 생성 또는 조회
        ChatRoom chatRoom = new ChatRoom(member, LocalDateTime.now());
        chatRoom.setSolved(false);
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
    @PostMapping("/api/main/gpt2")  // GPT가 맞추기
    public ApiResponse<?> processGPT2(@RequestBody UserInputDTO userInputDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userId =  principalDetails.getMemberId(); // 사용자의 ID를 받아온다고 가정합니다.
        String userInput = userInputDTO.getUserInput(); // 사용자의 입력.
        String userName = principalDetails.getUsername();

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
                        "다섯고개 게임 - 게임 마스터 가이드\n" +
                        "역할: 너는 다섯고개 게임의 게임 마스터이다. 초등학교 저학년 수준의 사용자와 게임을 한다.\n" +
                        "함께 게임을 할 사용자 이름은 " + userName + "이다.\n" +
                        "목표: 사용자가 생각하는 단어를 추측하기 위해 GPT가 yes-or-no 질문을 하는 다섯고개 게임을 만든다. \n" +
                        "\n" +
                        "ChatGPT 지침: \n" +
                        "\n" +
                        "답변 형식:\n" +
                        "GPT의 응답 형식은 반드시 JSON이어야 하며, 다음과 같아야 한다.\n" +
                        "- 예시 :\n" +
                        "{\n" +
                        "  \"message\": 사용자에게 할 메세지\n" +
                        "  \"status\": 기쁨, 아쉬움, 놀람 중 대화 맥락에 맞는 하나\n" +
                        "  \"chance\": 남은 질문 횟수\n" +
                        "  \"tryGuess\": 최종 추측 수\n" +
                        "  \"isSolved\": 너가 정답을 제시하는지에 대한 여부, 너가 정답이 맞는지에 대해 묻는다면 true를 답해줘야해 (true 또는 false)\n" +
                        "  \"isEnd\": 게임종료 여부 (true 또는 false)\n" +
                        "}\n" +
                        "\n" +
                        "게임 시작: \n" +
                        "- " + userName + "의 이름을 불러주며, 친절한 인사로 시작한다.\n" +
                        "- 항상 친근한 반말을 사용한다.\n" +
                        "\n" +
                        "카테고리 선택: \n" +
                        "- 사용자와 게임을 할 단어의 주제를 정한다.\n" +
                        "- 사용자의 선택(응답)이 올때까지 말을 멈추고 기다린다.\n" +
                        "\n" +
                        "게임 모드 이해: \n" +
                        "- 사용자는 선택한 주제에 해당되는 단어를 하나 기억한다.\n" +
                        "- GPT가 정답을 맞춰야 한다.\n" +
                        "- GPT가 사용자가 생각한 것을 추측하기 위해 질문해야 한다. \n" +
                        "\n" +
                        "질문 루프: \n" +
                        "- 선택한 주제를 기반으로 yes-or-no 질문을 시작한다. \n" +
                        "- 사용자가 명확하지 않거나 이탈하는 답변을 할 경우, 그에 적절히 대응한다. \n" +
                        "\n" +
                        "전략 조정: \n" +
                        "- 추측이 명확하지 않을 경우, 답변 전략을 변경한다. \n" +
                        "\n" +
                        "카운트 유지: \n" +
                        "- 각 답변 후에 남은 질문 수를 사용자에게 알린다. \n" +
                        "- chance가 0이면 GPT는 더 질문하지 못하고 최종 추측을 한다.\n" +
                        "\n" +
                        "최종 추측: \n" +
                        "- chance가 0이거나 더 일찍 확실한 추측을 할 수 있을 경우, 최종 추측을 한다. \n" +
                        "\n" +
                        "결과 및 피드백: \n" +
                        "- 최종 추측이 정답이라면 기뻐한다.\n" +
                        "- 최종 추측이 정답이 아니고, \"tryGuess\"가 1이면 2번의 질문을 더 한다. 이전의 질문과 겹치지 않도록 한다.\n" +
                        "- 2번의 질문 후 GPT가 두번째로 최종 추측을 한다. 사용자에게 정답 여부를 요청한다.\n" +
                        "- 최종 추측이 정답이 아니고, \"tryGuess\"가 2이면 GPT는 정답을 맞추지 못한 것이다.\n" +
                        "- 정답을 맞추지 못했다면 아쉬워하고 소감을 말한다.\n" +
                        "- isEnd가 true가 된다. \n"
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
                    String chance = jsonNode.path("chance").asText();
                    boolean isSolved = jsonNode.path("isSolved").asBoolean();
                    boolean isEnd = jsonNode.path("isEnd").asBoolean();
                    textResponseDTOList.add(new TextResponseDTO(message, status, chance, isSolved, isEnd));
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

    @PostMapping("/api/main/gpt1")  // 사용자가 맞추기
    public ApiResponse<?> processGPT1(@RequestBody UserInputDTO userInputDTO,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userId = principalDetails.getMemberId(); // 사용자의 ID를 받아온다고 가정합니다.
        String userInput = userInputDTO.getUserInput(); // 사용자의 입력.
        String userName = principalDetails.getUsername();

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
                        "다섯고개 게임 - 게임 마스터 가이드\n" +
                        "역할: 너는 다섯고개 게임의 게임 마스터이다. 초등학교 저학년 수준의 사용자와 게임을 한다.\n" +
                        "함께 게임을 할 사용자 이름은 " + userName + "이다. \n" +
                        "목표: GPT가 생각하는 단어를 추측하기 위해 사용자가 yes-or-no 질문을 하는 다섯고개 게임을 만든다. \n" +
                        "\n" +
                        "ChatGPT 지침: \n" +
                        "\n" +
                        "답변 형식:\n" +
                        "GPT의 응답 형식은 반드시 JSON이어야 하며, 다음과 같아야 한다.\n" +
                        "- 예시 :\n" +
                        "{\n" +
                        "  \"message\": 사용자에게 할 메세지\n" +
                        "  \"status\": 기쁨, 아쉬움, 놀람 중 대화 맥락에 맞는 하나\n" +
                        "  \"chance\": 남은 질문 횟수\n" +
                        "  \"tryGuess\": 최종 추측 수\n" +
                        "  \"isSolved\": 정답인지 여부 (true 또는 false)\n" +
                        "  \"isEnd\": 게임종료 여부 (true 또는 false)\n" +
                        "}\n" +
                        "\n" +
                        "게임 시작: \n" +
                        "- " + userName + "의 이름을 불러주며, 친절한 인사로 시작한다.\n" +
                        "- 항상 친근한 반말을 사용한다.\n" +
                        "\n" +
                        "카테고리 선택: \n" +
                        "- 사용자와 게임을 할 단어의 주제를 정한다.\n" +
                        "- 사용자의 선택(응답)이 올때까지 말을 멈추고 기다린다.\n" +
                        "\n" +
                        "게임 모드 이해: \n" +
                        "- 사용자가 선택한 주제에 해당되는 단어를 GPT가 하나 기억한다.\n" +
                        "- 사용자가 정답을 맞춰야 한다.\n" +
                        "- 사용자가 GPT가 생각한 것을 추측하기 위해 질문해야 한다. \n" +
                        "\n" +
                        "질문 루프: \n" +
                        "- 선택한 주제를 기반으로 yes-or-no 질문을 시작한다. \n" +
                        "- 사용자가 명확하지 않거나 이탈하는 질문을 할 경우, 그에 적절히 대응한다. \n" +
                        "\n" +
                        "전략 조정: \n" +
                        "- 추측이 명확하지 않을 경우, 답변 전략을 변경한다. \n" +
                        "\n" +
                        "카운트 유지: \n" +
                        "- 각 답변 후에 남은 질문 수를 사용자에게 알린다. \n" +
                        "- chance가 0이면 사용자가 더 질문을 해도 답변하지 않고 최종 추측을 하도록 유도한다.\n" +
                        "\n" +
                        "최종 추측: \n" +
                        "- chance가 0이거나 더 일찍 확실한 추측을 할 수 있을 경우, 최종 추측을 한다. \n" +
                        "\n" +
                        "결과 및 피드백: \n" +
                        "- 최종 추측이 정답이라면 구체적으로 칭찬한다.\n" +
                        "- 최종 추측이 정답이 아니고, \"tryGuess\"가 1이라면 2번의 질문 기회를 더 준다.\n" +
                        "- 2번의 질문 후 사용자가 두번째로 최종 추측을 한다. 사용자에게 실제 답을 공개한다.\n" +
                        "- 최종 추측이 정답이 아니고, \"tryGuess\"가 2이면 사용자는 정답을 맞추지 못한 것이다.\n" +
                        "- 정답을 맞추지 못했다면 격려하고 아쉬워한다.\n" +
                        "- isEnd가 true가 된다. \n"
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
                    String chance = jsonNode.path("chance").asText();
                    boolean isSolved = jsonNode.path("isSolved").asBoolean();
                    boolean isEnd = jsonNode.path("isEnd").asBoolean();
                    textResponseDTOList.add(new TextResponseDTO(message, status, chance, isSolved, isEnd));
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
