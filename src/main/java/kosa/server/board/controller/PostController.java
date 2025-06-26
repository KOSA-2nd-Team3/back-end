package kosa.server.board.controller;

import kosa.server.board.dto.request.LeaveMyPostRequestDto;
import kosa.server.board.dto.request.PartyJoinRequestDto;
import kosa.server.board.dto.response.MyPostOneResponseDto;
import kosa.server.board.dto.response.MyPostResponseDto;
import kosa.server.board.dto.request.PostCreateRequestDto;
import kosa.server.board.dto.request.PostUpdateRequestDto;
import kosa.server.board.dto.response.PlatformPostNullResponseDto;
import kosa.server.board.dto.response.PlatformPostResponseDto;
import kosa.server.board.service.PostService;
import kosa.server.common.security.user.CustomUserPrincipal;
import kosa.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    // 방 생성
    @PostMapping("/subscription/create")
    public ResponseEntity<Long> create(@RequestBody PostCreateRequestDto request) {
        Long createPostId = postService.create(request);
        return new ResponseEntity<>(createPostId, HttpStatus.CREATED);
    }

    // 방 수정
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PostUpdateRequestDto request) {
        postService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 방 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        postService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 내 페이지에서 내가 참여한 방 확인
    @GetMapping("/myPost")
    public ResponseEntity<Page<MyPostResponseDto>> myPost(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                          @RequestParam(defaultValue = "0") int page) {
        Page<MyPostResponseDto> myPostResponses = postService.readMyPost(principal.getName(), page);
        return new ResponseEntity<>(myPostResponses, HttpStatus.OK);
    }

    // 내 페이지에서 내가 참여한 모든 방 중에 하나 선택 시, 해당 페이지 정보 출력
    @GetMapping("/myPost/{postId}")
    public ResponseEntity<MyPostOneResponseDto> selectParty(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserPrincipal principal) {
        MyPostOneResponseDto myPostOneResponse = postService.selectParty(principal.getName(), postId);
        return new ResponseEntity<>(myPostOneResponse, HttpStatus.OK);
    }

    // 생성된 방 중에서 원하는 방 join 클릭 시, 파티원으로 저장
    @PostMapping("/joinParty")
    public ResponseEntity<?> joinParty(@RequestBody PartyJoinRequestDto request) {
        postService.joinParty(request.getLoginId(), request.getPostId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 파티원일때, 파티방 삭제
    @PostMapping("/leaveMyPost")
    public ResponseEntity<?> leaveMyPost(@RequestBody LeaveMyPostRequestDto request) {
        postService.leaveMyPost(request.getPostId(), request.getLoginId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 플랫폼 별 파티 리스트 가져오기
    @GetMapping("/platform/{platformId}")
    public ResponseEntity<List<PlatformPostResponseDto>> platformPostList(@PathVariable Long platformId) {
        List<PlatformPostResponseDto> platformPostResponseDtos = postService.platformPostList(platformId);
        return new ResponseEntity<>(platformPostResponseDtos, HttpStatus.OK);
    }

    // 나의 모집중인 구인글 개수
    @GetMapping("/myPost/active")
    public ResponseEntity<?> myPostActive(@AuthenticationPrincipal CustomUserPrincipal principal) {
        String loginId = principal.getName();
        int activeCount = postService.getActiveCount(loginId);
        return new ResponseEntity<>(activeCount, HttpStatus.OK);
    }

    @GetMapping("/platform/{platformId}/")
    public ResponseEntity<PlatformPostNullResponseDto> platformPostNull(@PathVariable Long platformId) {
        PlatformPostNullResponseDto platformPostNulls = postService.platformPostNull(platformId);
        return new ResponseEntity<>(platformPostNulls, HttpStatus.OK);
    }
}
