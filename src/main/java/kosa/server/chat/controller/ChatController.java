package kosa.server.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kosa.server.chat.dto.*;
import kosa.server.chat.service.ChatService;
import kosa.server.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat API")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    // 그룹 채팅방 개설
    @Operation(summary = "그룹 채팅방 생성", description = "새로운 그룹 채팅방을 생성합니다.")
    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(@RequestBody ChatRoomCreateDto chatRoomCreateDto,
                                             @AuthenticationPrincipal CustomUserPrincipal principal) {
        log.info("/room/group/create 요청");
        chatService.createGroupRoom(chatRoomCreateDto, principal.getName());
        return ResponseEntity.ok().build();
    }

    // 그룹채팅 목록 조회
//    @GetMapping("/room/group/list")
    @Operation(summary = "내 그룹 채팅방 목록 조회", description = "현재 로그인한 사용자가 참여 중인 그룹 채팅방 목록을 조회합니다.")
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<?> getGroupChatRooms(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("/api/chat/rooms 요청");
        String loginId = customUserPrincipal.getMember().getLoginId();
        List<ChatRoomListResDto> chatRooms = chatService.getMyGroupChatRooms(loginId);
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    // 그룹채팅방 참여 API
    @Operation(summary = "그룹 채팅방 참여", description = "해당 postId의 그룹 채팅방에 참가합니다.")
    @PostMapping("/room/group/{postId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long postId,
                                               @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("/room/group/{roomId}/join 요청");
        chatService.addParticipantToGroupChat(postId, customUserPrincipal.getName());
        return ResponseEntity.ok().build();
    }

    // 이전 메시지 조회
    @Operation(summary = "채팅방 이전 메시지 조회", description = "지정된 채팅방 ID에 대한 메시지 히스토리를 조회합니다.")
    @GetMapping("/api/chat/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId,
                                            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("/api/chat/history/{roomId} 요청");
        List<ChatMessageResDto> chatHistory = chatService.getChatHistory(roomId, customUserPrincipal.getName());
        return new ResponseEntity<>(chatHistory, HttpStatus.OK);
    }

    // 채팅 메시지 읽음 처리
    @Operation(summary = "채팅 메시지 읽음 처리", description = "지정된 채팅방의 메시지를 읽음 처리합니다.")
    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> messageRead(@PathVariable Long roomId,
                                         @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("/room/{roomId}/read 요청");
        chatService.messageRead(roomId, customUserPrincipal.getName());
        return ResponseEntity.ok().build();
    }

    // 채팅방 나가기
    @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제합니다.")
    @DeleteMapping("/chat-room/{roomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable Long roomId,
                                                @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("/chat-room/{roomId} 요청");
        chatService.deleteChatRoom(roomId, customUserPrincipal.getName());
        return ResponseEntity.ok().build();
    }

    // 채팅방 나가기
    @Operation(summary = "채팅방 나가기", description = "지정된 채팅방에서 나갑니다.")
    @DeleteMapping("/chat-room/{roomId}/leave")
    public ResponseEntity<?> leaveChatRoom(@PathVariable Long roomId,
                                             @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("/chat-room/{roomId}/leave 요청");
        chatService.leaveChatRoom(roomId, customUserPrincipal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 참여자 목록 조회", description = "지정된 채팅방의 참여자 목록을 조회합니다.")
    @GetMapping("/api/chat/room/{roomId}/members")
    public ResponseEntity<?> chatRoomMemberList(@PathVariable Long roomId) {
        log.info("/api/chat/member/list 요청");
        List<PartyMemberResponseDto> partyMemberResponseDtos = chatService.chatRoomMemberList(roomId);
        return new ResponseEntity<>(partyMemberResponseDtos, HttpStatus.OK);
    }

    //    /api/chat/room-by-post/{postId}
    @Operation(summary = "postId 기준 채팅방 ID 조회", description = "postId로 연결된 채팅방 ID를 조회합니다.")
    @GetMapping("/api/chat/room-by-post/{postId}")
    public ResponseEntity<?> chatRoomByPostId(@PathVariable Long postId,
                                              @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {

        log.info("/api/chat/room-by-post/{postId} 요청");
        Long findChatRoomId = chatService.ChatRoomIdByPostId(customUserPrincipal.getMember().getLoginId(), postId);
        ChatRoomIdResponseDto dto = ChatRoomIdResponseDto.builder()
                .chatRoomId(findChatRoomId)
                .build();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // 내 채팅방 목록 조회 : roomId, roomName, 그룹채팅여부, 메세지 읽음 개수
    /*@GetMapping("/my/rooms")
    public ResponseEntity<?> getMyChatRooms() {
        List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRooms();
        return new ResponseEntity<>(myChatListResDtos, HttpStatus.OK);
    }*/

    // 개인 채팅방 개설 또는 roomId 리턴
    /*@PostMapping("/room/private/create")
    public ResponseEntity<?> getOrCreatePrivateRoom(@RequestParam Long otherMemberId) {
        Long roomId = chatService.getOrCreatePrivateRoom(otherMemberId);
        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }*/

}
