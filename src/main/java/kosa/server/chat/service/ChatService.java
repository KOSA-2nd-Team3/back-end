package kosa.server.chat.service;

import jakarta.persistence.EntityNotFoundException;
import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Post;
import kosa.server.board.repository.PartyMemberRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.chat.dto.*;
import kosa.server.chat.entity.ChatMessage;
import kosa.server.chat.entity.ChatParticipant;
import kosa.server.chat.entity.ChatRoom;
import kosa.server.chat.entity.ReadStatus;
import kosa.server.chat.exception.ChatRoomNotFoundException;
import kosa.server.chat.repository.jpa.ChatMessageJpaRepository;
import kosa.server.chat.repository.jpa.ChatParticipantJpaRepository;
import kosa.server.chat.repository.jpa.ChatRoomJpaRepository;
import kosa.server.chat.repository.jpa.ReadStatusJpaRepository;
import kosa.server.common.code.ErrorCode;
import kosa.server.member.entity.Member;
import kosa.server.member.exception.MemberNotFoundException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatParticipantJpaRepository chatParticipantJpaRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final ReadStatusJpaRepository readStatusJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final PostRepository postJpaRepository;

    public void saveMessage(Long roomId, ChatMessageDto chatMessageDto) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomJpaRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 보낸 사람 조회
        Member sender = memberJpaRepository.findByLoginId(chatMessageDto.getSenderLoginId())
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 메세지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(chatMessageDto.getMessage())
                .build();
        chatMessageJpaRepository.save(chatMessage);

        // 사용자별로 읽음 여부 저장
        List<ChatParticipant> chatParticipants = chatParticipantJpaRepository.findByChatRoom(chatRoom);
        for (ChatParticipant c : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .member(c.getMember())
                    .chatMessage(chatMessage)
                    .isRead(c.getMember().equals(sender))
                    .build();
            readStatusJpaRepository.save(readStatus);
        }
    }

    public void createGroupRoom(ChatRoomCreateDto chatRoomCreateDto, String loginId) {
//        Member member = memberJpaRepository.findByLoginId(SecurityContextHolder.getContext().getAuthentication().getName())
//                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Post findPost = postJpaRepository.findById(chatRoomCreateDto.getPostId())
                .orElseThrow(() -> new RuntimeException("post not found"));

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomCreateDto.getRoomName())
                .post(findPost)
                .isGroupChat("Y")
                .build();
        chatRoomJpaRepository.save(chatRoom);

        // 채팅방 참여자로 개설자를 추가
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantJpaRepository.save(chatParticipant);
    }

    public List<ChatRoomListResDto> getMyGroupChatRooms(String loginId) {
        // 로그인 아이디 검증(조회)
        memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 그룹 채팅방 조회
        List<ChatRoom> chatRooms = chatRoomJpaRepository.findByIsGroupChatAndLoginId("Y", loginId);

        // 리스트 만든 후 담아서 리턴
        List<ChatRoomListResDto> dtos = new ArrayList<>();
        for (ChatRoom c : chatRooms) {
            Long count = readStatusJpaRepository.countByChatRoom_IdAndMember_LoginIdAndIsReadFalse(c.getId(), loginId);
            ChatRoomListResDto dto = ChatRoomListResDto.builder()
                    .leaderId(c.getPost().getMember().getLoginId())
                    .roomId(c.getId())
                    .roomName(c.getName())
                    .serviceName(c.getPost().getPlatform().getName())
                    .unreadCount(count)
                    .createdAt(c.getCreatedAt())
                    .build();

            dtos.add(dto);
        }

        return dtos;
    }

    public void addParticipantToGroupChat(Long roomId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomJpaRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 멤버 조회
        Member member = memberJpaRepository.findByLoginId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if(chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("그룹 채팅이 아닙니다.");
        }

        // 이미 참여자 인지 검증
        Optional<ChatParticipant> participant = chatParticipantJpaRepository.findByChatRoomAndMember(chatRoom, member);
        if (!participant.isPresent()) {
            addParticipantToRoom(chatRoom, member);
        }
    }

    // ChatParticipant 객체 생성 후 저장 (채팅방에 멤버 추가)
    public void addParticipantToRoom(ChatRoom chatRoom, Member member) {
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantJpaRepository.save(chatParticipant);
    }

    public List<ChatMessageResDto> getChatHistory(Long roomId) {
        // 내가 해당 채팅방의 참여자가 아닐경우 에러
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomJpaRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room cannot be found"));

        // 멤버 조회
        Member member = memberJpaRepository.findByLoginId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));

        List<ChatParticipant> chatParticipants = chatParticipantJpaRepository.findByChatRoom(chatRoom);
        boolean check = false;
        for (ChatParticipant c : chatParticipants) {
            if (c.getMember().equals(member)) {
                check = true;
            }
        }

        if (!check) {
            throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다.");
        }

        // 특정 room에 대한 message 조회
//        List<ChatMessage> chatMessages = chatMessageJpaRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);
        List<ChatMessageResDto> messageDtosByRoomId = chatMessageJpaRepository.findMessageDtosByRoomId(roomId);

        return messageDtosByRoomId;
    }

    public boolean isRoomParticipant(String email, Long roomId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomJpaRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room cannot be found"));

        // 멤버 조회
        Member member = memberJpaRepository.findByLoginId(email)
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));

        List<ChatParticipant> chatParticipants = chatParticipantJpaRepository.findByChatRoom(chatRoom);
        for (ChatParticipant c : chatParticipants) {
            if (c.getMember().equals(member)) {
                return true;
            }
        }
        return false;
    }

    public void messageRead(Long roomId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomJpaRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room cannot be found"));

        // 멤버 조회
        Member member = memberJpaRepository.findByLoginId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));

        // 채팅룸, 멤버별 읽음여부 채팅 조회
        List<ReadStatus> readStatuses = readStatusJpaRepository.findByChatRoomAndMember(chatRoom, member);
        for (ReadStatus r : readStatuses) {
            r.updateIsRead(true);
        }
    }

    public List<MyChatListResDto> getMyChatRooms() {
        Member member = memberJpaRepository.findByLoginId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));
        List<ChatParticipant> chatParticipant = chatParticipantJpaRepository.findAllByMember(member);
        List<MyChatListResDto> dtos = new ArrayList<>();
        for (ChatParticipant c : chatParticipant) {
//            Long count = readStatusJpaRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(c.getChatRoom().getId())
                    .roomName(c.getChatRoom().getName())
//                    .unReadCount(count)
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public void leaveChatRoom(Long roomId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomJpaRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room cannot be found"));

        // 멤버 조회
        Member member = memberJpaRepository.findByLoginId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));
        if (chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("단체 채팅방이 아닙니다.");
        }

        ChatParticipant chatParticipant = chatParticipantJpaRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다."));

        chatParticipantJpaRepository.delete(chatParticipant);

        List<ChatParticipant> chatParticipants = chatParticipantJpaRepository.findByChatRoom(chatRoom);
        if (chatParticipants.isEmpty()) {
            chatRoomJpaRepository.delete(chatRoom);
        }
    }

    public Long getOrCreatePrivateRoom(Long otherMemberId) {
        Member member = memberJpaRepository.findByLoginId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Member otherMember = memberJpaRepository.findById(otherMemberId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 나와 상대방이 1:1 채팅에 이미 참석하고 있다면 해당 roomId 리턴
        Optional<ChatRoom> chatRoom = chatParticipantJpaRepository.findExistingPrivateRoom(member.getId(), otherMember.getId());
        if (chatRoom.isPresent()) {
            return chatRoom.get().getId();
        }
        // 만약 참석하고 있지 않다면 새로운 채팅방 개설
        ChatRoom newRoom = ChatRoom.builder()
                .isGroupChat("N")
                .name(member.getName() + "-" + otherMember.getName())
                .build();
        chatRoomJpaRepository.save(newRoom);
        // 두사람 모두 참여자로 새롭게 추가
        addParticipantToRoom(newRoom, member);
        addParticipantToRoom(newRoom, otherMember);

        return newRoom.getId();
    }

    public List<PartyMemberResponseDto> chatRoomMemberList(Long roomId) {
        // RoomId 검증 작업
        chatRoomJpaRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room cannot be found"));

        // Fetch 조인으로 가져온 뒤 PartyMemberResponseDto List 생성
        return partyMemberRepository.findByChatRoomId(roomId)
                .stream()
                .map(partyMember -> PartyMemberResponseDto.builder()
                        .loginId(partyMember.getMember().getLoginId())
                        .leaderYn(partyMember.getIsOwner())
                        .build())
                .toList();
    }

}
