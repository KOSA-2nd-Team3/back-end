package kosa.server.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import kosa.server.board.dto.request.*;
import kosa.server.board.dto.response.MyPostOneResponseDto;
import kosa.server.board.dto.response.MyPostResponseDto;
import kosa.server.board.service.PostService;
import kosa.server.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Tag(name = "Post API")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 방 생성
    @Operation(summary = "파티 생성", description = "새로운 구독 파티을 생성합니다.")
    @PostMapping("/subscription/create")
    public ResponseEntity<Long> create(@RequestBody PostCreateRequestDto request) {
        Long createPostId = postService.create(request);
        return new ResponseEntity<>(createPostId, HttpStatus.CREATED);
    }

    // 방 수정
    @Operation(summary = "파티 수정", description = "기존 구독 파티 정보를 수정합니다.")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PostUpdateRequestDto request) {
        postService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 내 페이지에서 내가 생성한 방 확인 (파티장)
    @Operation(summary = "내가 만든 파티 목록 조회", description = "마이페이지에서 내가 만든 파티 목록을 조회합니다.")
    @GetMapping("/myPost")
    public ResponseEntity<Page<MyPostResponseDto>> myPost(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "active") String statusFilter) {
        Page<MyPostResponseDto> myPostResponses = postService.readMyPost(
                principal.getName(), page, sortField, sortDirection, statusFilter);
        return new ResponseEntity<>(myPostResponses, HttpStatus.OK);
    }

    // 내 페이지에서 내가 참여한 모든 방 중에 하나 선택 시, 해당 페이지 정보 출력
    @Operation(summary = "내 파티 상세 조회", description = "내가 만든 파티 중 하나를 선택해 상세 정보를 조회합니다.")
    @GetMapping("/myPost/{postId}")
    public ResponseEntity<MyPostOneResponseDto> selectParty(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserPrincipal principal) {
        MyPostOneResponseDto myPostOneResponse = postService.selectParty(principal.getName(), postId);
        return new ResponseEntity<>(myPostOneResponse, HttpStatus.OK);
    }

    // 생성된 방 중에서 원하는 방 join 클릭 시, 파티원으로 저장
    @Operation(summary = "파티 참여", description = "구독 파티에 파티원으로 참여합니다.")
    @PostMapping("/joinParty")
    public ResponseEntity<?> joinParty(@RequestBody PartyJoinRequestDto request) {
        postService.joinParty(request.getLoginId(), request.getPostId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "파티 이메일 발송", description = "파티에 참여 중인 파티원들에게 이메일을 발송합니다.")
    @GetMapping("/mailSend/{postId}")
    public ResponseEntity<?> sendMail(@PathVariable Long postId)
            throws MessagingException, UnsupportedEncodingException {
        postService.prepareAndSendMail(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 파티원일때, 파티방 삭제
    @Operation(summary = "파티 나가기", description = "파티원이 파티에서 나갑니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> leaveMyPost(@PathVariable Long postId,
                                         @AuthenticationPrincipal CustomUserPrincipal principal) {

        postService.leaveMyPost(postId, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 내가 partyMember로 참여한 모든 Post 조회 (파티원)
    @Operation(summary = "참여 중인 파티 목록 조회", description = "내가 파티원으로 참여 중인 모든 파티을 조회합니다.")
    @GetMapping("/myPost-join")
    public ResponseEntity<Page<MyPostResponseDto>> getMyPartyPosts(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "active") String statusFilter) {
        String loginId = principal.getName();
        Page<MyPostResponseDto> result = postService.findPostsByPartyMemberLoginId(
                loginId, page, sortField, sortDirection, statusFilter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "서비스 시작", description = "파티이 준비되면 실제 서비스가 시작됩니다.")
    @PostMapping("/start")
    public ResponseEntity<?> startService(@RequestBody StartRequestDto dto){
        postService.startService(dto.getPostId(), dto.getDurationMonth());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
