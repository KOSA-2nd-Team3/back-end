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
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 방 생성
    @Operation(summary = "파티 생성", description = "새로운 구독 파티을 생성합니다.")
    @PostMapping("/create")
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

    // 생성된 방 중에서 원하는 방 join 클릭 시, 파티원으로 저장
    @Operation(summary = "파티 참여", description = "구독 파티에 파티원으로 참여합니다.")
    @PostMapping("/join")
    public ResponseEntity<?> joinParty(@RequestBody PartyJoinRequestDto request) {
        postService.joinParty(request.getLoginId(), request.getPostId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 파티원일때, 파티방 삭제
    @Operation(summary = "파티 나가기", description = "파티원이 파티에서 나갑니다.")
    @DeleteMapping("/{postId}/out")
    public ResponseEntity<?> leaveMyPost(@PathVariable Long postId,
                                         @AuthenticationPrincipal CustomUserPrincipal principal) {

        postService.leaveMyPost(postId, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "파티 이메일 발송", description = "파티에 참여 중인 파티원들에게 이메일을 발송합니다.")
    @GetMapping("/send-mail/{postId}")
    public ResponseEntity<?> sendMail(@PathVariable Long postId)
            throws MessagingException, UnsupportedEncodingException {
        postService.prepareAndSendMail(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
