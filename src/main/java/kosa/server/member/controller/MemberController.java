package kosa.server.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kosa.server.common.security.user.CustomUserPrincipal;
import kosa.server.member.dto.request.NicknameUpdateRequest;
import kosa.server.member.dto.response.ProfileResponseDto;
import kosa.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 프로필 조회", description = "로그인된 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        String loginId = customUserPrincipal.getName();
        ProfileResponseDto profileResponseDto = memberService.getProfile(loginId);
        return ResponseEntity.ok(profileResponseDto);
    }

    @Operation(summary = "닉네임 사용 가능 여부 체크", description = "바꾸고 싶은 닉네임을 사용할 수 있는지 확입합니다.")
    @PostMapping("/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestBody NicknameUpdateRequest request) {
        boolean result = memberService.isNicknameAvailable(request.getNickname());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "닉네임 변경", description = "로그인된 사용자의 닉네임을 수정합니다.")
    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickName(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
                                                 @RequestBody NicknameUpdateRequest request) {
        String loginId = customUserPrincipal.getName();
        String updatedNickname = memberService.updateNickname(loginId, request.getNickname());
        return ResponseEntity.ok(updatedNickname);
    }
}
