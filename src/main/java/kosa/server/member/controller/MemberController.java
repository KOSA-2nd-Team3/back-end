package kosa.server.member.controller;

import kosa.server.common.security.user.CustomUserDetails;
import kosa.server.member.dto.response.ProfileResponseDto;
import kosa.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String loginId = customUserDetails.getUsername();
        ProfileResponseDto profileResponseDto = memberService.getProfile(loginId);
        return ResponseEntity.ok(profileResponseDto);
    }

    @PostMapping("/nickName")
    public ResponseEntity<String> updateNickName(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody String nickName) {
        String loginId = customUserDetails.getUsername();
        String updatedNickname = memberService.updateNickname(loginId, nickName);
        return ResponseEntity.ok(updatedNickname);
    }
}
