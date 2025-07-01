package kosa.server.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kosa.server.board.dto.request.StartRequestDto;
import kosa.server.board.dto.response.MyPostOneResponseDto;
import kosa.server.board.dto.response.MyPostResponseDto;
import kosa.server.board.service.DashboardService;
import kosa.server.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dashboard API")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // 내 페이지에서 내가 생성한 방 확인 (파티장)
    @Operation(summary = "내가 만든 파티 목록 조회", description = "마이페이지에서 내가 만든 파티 목록을 조회합니다.")
    @GetMapping("/my-parties")
    public ResponseEntity<Page<MyPostResponseDto>> myPost(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "active") String statusFilter) {
        Page<MyPostResponseDto> myPostResponses = dashboardService.readMyPost(
                principal.getName(), page, sortField, sortDirection, statusFilter);
        return new ResponseEntity<>(myPostResponses, HttpStatus.OK);
    }

    // 내 페이지에서 내가 참여한 모든 방 중에 하나 선택 시, 해당 페이지 정보 출력
    @Operation(summary = "내 파티 상세 조회", description = "내가 만든 파티 중 하나를 선택해 상세 정보를 조회합니다.")
    @GetMapping("/my-parties/{postId}")
    public ResponseEntity<MyPostOneResponseDto> selectParty(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserPrincipal principal) {
        MyPostOneResponseDto myPostOneResponse = dashboardService.selectParty(principal.getName(), postId);
        return new ResponseEntity<>(myPostOneResponse, HttpStatus.OK);
    }

    // 내가 partyMember로 참여한 모든 Post 조회 (파티원)
    @Operation(summary = "참여 중인 파티 목록 조회", description = "내가 파티원으로 참여 중인 모든 파티을 조회합니다.")
    @GetMapping("/participated-parties")
    public ResponseEntity<Page<MyPostResponseDto>> getMyPartyPosts(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "active") String statusFilter) {
        String loginId = principal.getName();
        Page<MyPostResponseDto> result = dashboardService.findPostsByPartyMemberLoginId(
                loginId, page, sortField, sortDirection, statusFilter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "서비스 시작", description = "파티가 준비되면 실제 서비스가 시작됩니다.")
    @PostMapping("/my-parties/start")
    public ResponseEntity<?> startService(@RequestBody StartRequestDto dto){
        dashboardService.startService(dto.getPostId(), dto.getDurationMonth());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
