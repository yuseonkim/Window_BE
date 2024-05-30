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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
        Page<ChatRoomDTO> chatRoomDTOs = chatRooms.map(chatRoom -> new ChatRoomDTO(chatRoom.getId(), chatRoom.getTimestamp()));

        return ApiResponseGenerator.success(chatRoomDTOs.getContent(), HttpStatus.OK);
    }

    @GetMapping("/api/chats")
    public ApiResponse<?> getChatRoomsDetails(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam ChatRequestDTO chatRequestDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ){
        String memberId = principalDetails.getMemberId();
        Member member = memberRepository.findByMemberId(memberId);
        if(member == null){
            throw new MemberNotFoundException(MessageCode.MEMBER_NOT_FOUND);
        }
        ChatRoom chatRoom = chatRoomRepository.findChatRoomsById(chatRequestDTO.getId());
        if(!chatRoom.getMember().getMemberId().equals(memberId)){
            throw new AccessDeniedException(MessageCode.REQUEST_ACCESS_DENIED);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Chat> chats = chatRepository.findChatsByChatRoom(chatRoom,pageable);
        List<ChatDTO> chatDTOs = new ArrayList<>();
        int startingIndex = page * size + 1;
        int index = startingIndex;
        for (Chat chat : chats) {
            ChatDTO chatDTO = new ChatDTO(index, chat.getMessage());
            chatDTOs.add(chatDTO);
            index++;
        }

        return ApiResponseGenerator.success(chatDTOs, HttpStatus.OK);
    }

}
