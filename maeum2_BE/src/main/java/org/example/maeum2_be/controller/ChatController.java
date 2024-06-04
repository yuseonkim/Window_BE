package org.example.maeum2_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be._core.MessageCode;
import org.example.maeum2_be.dto.details.ChatDTO;
import org.example.maeum2_be.dto.details.ChatRequestDTO;
import org.example.maeum2_be.dto.details.ChatRoomDTO;
import org.example.maeum2_be.entity.domain.Chat;
import org.example.maeum2_be.entity.domain.ChatRoom;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.exception.AccessDeniedException;
import org.example.maeum2_be.exception.MemberNotFoundException;
import org.example.maeum2_be.repository.ChatRepository;
import org.example.maeum2_be.repository.ChatRoomRepository;
import org.example.maeum2_be.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/api/chats")
    public ApiResponse<?> getChatRoomsByMember(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        Pageable pageable = PageRequest.of(page, size);
        Member member = memberRepository.findByMemberId(principalDetails.getMemberId());
        Page<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByMember(member, pageable);
        Page<ChatRoomDTO> chatRoomDTOs = chatRooms.map(chatRoom -> new ChatRoomDTO(chatRoom.getId(),chatRoom.isSolved(), chatRoom.getTimestamp()));

        return ApiResponseGenerator.success(chatRoomDTOs.getContent(), HttpStatus.OK);
    }

    @GetMapping("/api/chats/detail")
    public ApiResponse<?> getChatRoomsDetails(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ChatRequestDTO chatRequestDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String memberId = principalDetails.getMemberId();
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new MemberNotFoundException(MessageCode.MEMBER_NOT_FOUND);
        }
        ChatRoom chatRoom = chatRoomRepository.findChatRoomsById(chatRequestDTO.getDetailId());
        if (!chatRoom.getMember().getMemberId().equals(memberId)) {
            throw new AccessDeniedException(MessageCode.REQUEST_ACCESS_DENIED);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Chat> chats = chatRepository.findChatsByChatRoom(chatRoom, pageable);
        List<Map<String, Object>> chatPairs = new ArrayList<>();

        Iterator<Chat> chatIterator = chats.iterator();
        int startingIndex = page * size + 1;
        int index = startingIndex;
        while (chatIterator.hasNext()) {
            Chat childChat = chatIterator.next();
            if (chatIterator.hasNext()) {
                Chat aiChat = chatIterator.next();
                Map<String, Object> chatPair = new HashMap<>();
                chatPair.put("id",index);
                chatPair.put("ask", childChat.getMessage());
                chatPair.put("answer", aiChat.getMessage());
                chatPairs.add(chatPair);
                index++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("child_name", principalDetails.getMember().getChildFirstName());
        response.put("ai_name", principalDetails.getMember().getAiName());
        response.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("chats", chatPairs);

        return ApiResponseGenerator.success(response, HttpStatus.OK);
    }


}
