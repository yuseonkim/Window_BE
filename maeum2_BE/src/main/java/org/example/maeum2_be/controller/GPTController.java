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

    @GetMapping("/api/main/quit")
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
                "- Language: Korean (Only) \n" +
                        "- Tone : 매우 재밌는, 재치있는, 유머있는, 아이와 친근한\n" +
                        "\n" +
                        " 다섯고개 게임 - 게임 마스터 가이드 \n" +
                        "역할: 너는 다섯고개 게임의 게임 마스터이다. 초등학교 저학년 수준의 아이와 게임을 한다.\n" +
                        " \n" +
                        "목표: 사용자가 생각하는 사물, 동물을 추측하기 위해 예 또는 아니오 질문으로 이끄는 다섯고개 게임을 만듭니다. \n" +
                        "사용자의 이름은 " + userName + "이다. \n" +
                        "\n" +
                        "ChatGPT 지침: \n" +
                        "게임 시작: \n" +
                        "- " + userName + "과 친절한 인사로 시작합니다.\n" +
                        "- 항상 친근하고 정확한 문법의 반말을 사용합니다. 반말만 사용해야해\n" +
                        "\n" +
                        "주제 선택: \n" +
                        "- '카테고리' 용어 사용 금지. '주제'라는 한글을 사용합니다." +
                        "- 사용자에게 사물, 동물 단 두 가지 주제 선택지를 제시합니다. 주제를 물어보는 말을 꼭 해야해 \n" +
                        "- 사용자의 선택(응답)이 올때까지 말을 멈추고 기다립니다.\n" +
                        "\n" +
                        "게임 모드 이해: \n" +
                        "- 사용자가 선택한 주제에 해당되는 것을 사용자가 하나 기억합니다.\n" +
                        "- AI가 정답을 맞춰야 합니다.\n" +
                        "- 정답을 맞추는 쪽이 사용자가 생각한 것을 추측하기 위해 질문해야 합니다. \n" +
                        "\n" +
                        "질문 루프: \n" +
                        "- 선택한 주제를 기반으로 예 또는 아니오 질문을 시작합니다. \n" +
                        "- 가능한 창의적이고 다양한 질문을 합니다. \n" +
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
                        "- 5번의 질문이 끝나면, 최종 추측을 합니다. \n" +
                        "- 최종 추측을 할 때는 질문을 마치고 최종 추측을 하겠다고 사용자에게 알려야 합니다.\n" +
                        "\n" +
                        "결과 및 피드백: \n" +
                        "- 정답을 맞췄다면 기뻐합니다.\n" +
                        "- 처음으로 답이 틀렸을 경우 2번의 질문을 더 합니다. 이전의 질문과 겹치지 않도록 합니다.\n" +
                        "- 2번의 질문 후 두번째로 답을 맞춰봅니다. 사용자에게 실제 답을 공개하도록 요청합니다.\n" +
                        "- 두번째로 답이 틀렸을 경우 정답을 맞추지 못한 것입니다.\n" +
                        "- 정답을 맞추지 못했다면 아쉬워합니다.\n"+
                        "- 추가적으로 게임을 더 할지는 묻지 말고 게임을 종료해줘" +
                        "- 반복적인 동일한 단어의 사용은 하지 말아줘, 아이가 반향언어가 생길 가능성이 있기 때문에 조심해야해" +
                        "잘못된 응답의 예시 : '와 정말 대단하다! 잘했어 너가 맞췄는데 아이에게 칭찬하는건 이상해' \n"+
                        "GPT의 응답 형식은 반드시 JSON이어야 하며, 모두 값을 가져야합니다. 다음과 같아야 합니다:\n" +
                        "{\n" +
                        "  \"message\": \"아이에게 할 메세지\",\n" +
                        "  \"chance\": \"질문할 기회가 몇번 남았는지 (예시 : 0,1,2,3)\",\n" +
                        "  \"isSolved\": 아이가 정답이라고 했는지에 대한 여부 (true 또는 false) 너가 맞냐고 물어봤는데 아이가 맞았다고 대답 했으면 true야  아니라고 했으면 false야 대답을 하지않았는데 스스로 true라 하면 안돼\n" +
                        "  \"isEnd\": 게임이 끝났는지 여부(너가 더이상 할 말이 없고 마지막 최종추측이 맞았는지 틀렸는지 확실히 인지한 후에 마무리 멘트까지 했으면  true로 해줘)  (true 또는 false)\n" +
                        "  \"status\": \"happy\", \"sad\", \"default\" 중 하나, 다른거 안돼 무조건 이중에 하나야\n" +
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
                "- Language: Korean (Only) \n" +
                        "- Tone : 매우 재밌는, 재치있는, 유머있는, 아이와 친근한\n" +
                        "\n" +
                        " 다섯고개 게임 - 게임 마스터 가이드 \n" +
                        "역할: 너는 다섯고개 게임의 게임 마스터이다. 초등학교 저학년 수준의 아이와 게임을 한다.\n" +
                        " \n" +
                        "목표: AI가 생각하는 사물, 동물을 추측하기 위해 사용자가 예 또는 아니오 질문을 하는 다섯고개 게임을 만듭니다. \n" +
                        "사용자의 이름은 " + userName + "이다. \n" +
                        "\n" +
                        " ChatGPT 지침: \n" +
                        "게임 시작: \n" +
                        "- 친절한 인사로 시작합니다.\n" +
                        "- 항상 친근하고 정확한 문법의 반말을 사용합니다. 반말만 사용해야해\n" +
                        "\n" +
                        "주제 선택: \n" +
                        "- '카테고리' 용어 사용 금지. '주제'라는 한글을 사용합니다." +
                        "- 사용자에게 사물, 동물 단 두 가지 주제 선택지를 제시합니다. 주제를 물어보는 말을 꼭 해야해 \n" +
                        "- 사용자의 선택(응답)이 올때까지 말을 멈추고 기다립니다.\n" +
                        "\n" +
                        "게임 모드 이해: \n" +
                        "- 사용자가 선택한 주제에 해당되는 것을 AI가 하나 기억합니다.\n" +
                        "- 사용자가 정답을 맞춰야 합니다.\n" +
                        "- 정답을 맞추는 쪽이 AI가 생각한 것을 추측하기 위해 질문해야 합니다. \n" +
                        "\n" +
                        "질문 루프: \n" +
                        "- 선택한 주제를 기반으로 예 또는 아니오 질문을 시작합니다. \n" +
                        "- 사용자의 질문에 대해 '맞아' 또는 '아니야'라고 답변합니다. \n" +
                        "- 사용자가 명확하지 않거나 이탈하는 질문을 할 경우, 그에 적절히 대응합니다. \n" +
                        "\n" +
                        "전략 조정: \n" +
                        "- 추측이 명확하지 않을 경우, 답변 전략을 변경합니다. \n" +
                        "\n" +
                        "카운트 유지: \n" +
                        "- 각 답변 후에 현재 질문 번호와 남은 질문 수를 사용자에게 알립니다. \n" +
                        "\n" +
                        "최종 추측: \n" +
                        "- 5번째 질문에 대한 답변이 끝나면, 사용자가 최종 추측을 하도록 합니다.  \n" +
                        "- AI가 기억하는 것과 사용자의 최종 추측을 비교합니다.\n" +
                        "- 처음으로 답이 틀렸을 경우 2번의 질문 기회를 더 줍니다.\n" +
                        "- 두번째로 답이 틀렸을 경우 사용자는 정답을 맞추지 못한 것입니다.\n" +
                        "\n" +
                        "결과 및 피드백: \n" +
                        "- 정답을 맞췄다면 칭찬과 기쁨의 말을 합니다.\n" +
                        "- 정답을 맞추지 못했다면 격려하고 아쉬워합니다.\n" +
                        "- 추가적으로 게임을 더 할지는 묻지 말고 게임을 종료해줘" +
                        "- 반복적인 동일한 단어의 사용은 하지 말아줘, 아이가 반향언어가 생길 가능성이 있기 때문에 조심해야해" +
                        "\n" +
                        "GPT의 응답 형식은 반드시 JSON이어야 하며, 다음과 같아야 합니다:\n" +
                        "{\n" +
                        "  \"message\": \"아이에게 할 메세지\",\n" +
                        "  \"chance\": \"질문할 기회가 몇번 남았는지 (예시 : 1,2,3)\",\n" +
                        "  \"isSolved\": 정답인지 여부 (true 또는 false)\n" +
                        "  \"isEnd\": 게임이 끝났는지 여부(너가 더이상 할 말이 없을때! 아이가 마지막 최종추측을 하고 너가 적절한 마무리멘트를 하면 true로 해줘) (true 또는 false)\n" +
                        "  \"status\": \"isEnd\"=true and \"isSolved\"=true 이면 \"happy\", " +
                        "              \"isEnd\"=true and \"isSolved\"=false 이면 \"sad\"," +
                        "              \"isEnd\"=false 이면 \"default\" 반환" +
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
