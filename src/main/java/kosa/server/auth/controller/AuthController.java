package kosa.server.auth.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kosa.server.auth.dto.AuthStatusDto;
import kosa.server.auth.dto.JoinRequestDto;
import kosa.server.auth.dto.LoginRequestDto;
import kosa.server.auth.dto.UserInfoDto;
import kosa.server.auth.oauth2.handler.TempAuthStorage;
import kosa.server.auth.service.AuthService;
import kosa.server.common.code.ErrorCode;
import kosa.server.common.security.jwt.JwtProvider;
import kosa.server.common.security.user.CustomUserPrincipal;
import kosa.server.common.util.CookieUtil;
import kosa.server.member.exception.TokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthStatusDto> loginMember(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        Map<String, Object> authData = authService.login(loginRequestDto);

        String accessToken = (String) authData.get("accessToken");
        String refreshToken = (String) authData.get("refreshToken");
        UserInfoDto userInfo = (UserInfoDto) authData.get("userInfo");

        response.addHeader("Authorization", "Bearer " + accessToken);
//        cookieUtil.addCookie(response, "accessToken", accessToken, 30 * 60);
        cookieUtil.addCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 14);

        AuthStatusDto authStatusDto = new AuthStatusDto(true, userInfo);
        return new ResponseEntity<>(authStatusDto, HttpStatus.OK);
    }

    // **새로 추가된 소셜 로그인 토큰 교환 엔드포인트**
    @PostMapping("/social/exchange")
    public ResponseEntity<AuthStatusDto> exchangeSocialToken(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String tempCode = request.get("code");

        if (tempCode == null || tempCode.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 임시 코드로 액세스 토큰 조회
        String accessToken = TempAuthStorage.retrieveAndRemove(tempCode);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 토큰으로부터 사용자 정보 추출 (JWT 디코딩)
        String loginId = jwtProvider.getLoginId(accessToken);
        String role = jwtProvider.getRole(accessToken);

        // 응답 헤더에 액세스 토큰 설정
        response.addHeader("Authorization", "Bearer " + accessToken);

        // 사용자 정보 생성
        UserInfoDto userInfo = new UserInfoDto(loginId, role);
        AuthStatusDto authStatusDto = new AuthStatusDto(true, userInfo);

        return ResponseEntity.ok(authStatusDto);
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinMember(@RequestBody @Valid JoinRequestDto joinRequestDto) throws MessagingException {
        authService.joinMember(joinRequestDto);
        authService.sendVerificationEmail(joinRequestDto.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 이메일 인증 토큰 확인용 엔드포인트 (예: GET /api/auth/verify?token=...)
    @GetMapping("/login/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean verified = authService.verifyEmailToken(token);

        URI redirectUri;
        if (verified) {
            redirectUri = URI.create("http://localhost:5173/?verified=true");
        } else {
            redirectUri = URI.create("http://localhost:5173/verify-failed");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // 상태 확인 엔드포인트
    @GetMapping("/status")
    public ResponseEntity<AuthStatusDto> getAuthStatus(@AuthenticationPrincipal CustomUserPrincipal userDetails) {
        // 인증 객체 있으면 유저인포 만들어서 리턴
        if (userDetails != null) {
            UserInfoDto userInfo = new UserInfoDto(
                    userDetails.getName(),
                    userDetails.getAuthorities().iterator().next().getAuthority()
            );
            return ResponseEntity.ok(new AuthStatusDto(true, userInfo));
        } else {
            return ResponseEntity.ok(new AuthStatusDto(false, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        cookieUtil.getCookie(request, "refreshToken").ifPresent(cookie -> {
            authService.logout(cookie.getValue());
        });

        // 쿠키의 유효기간을 0으로 설정
        cookieUtil.deleteCookie(response, "refreshToken");

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 리프레시 토큰을 가져옴
        String refreshToken = cookieUtil.getCookie(request, "refreshToken")
                .map(Cookie::getValue)
                .orElseThrow(() -> new TokenNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        // 서비스를 호출하여 새로운 액세스 토큰을 발급
        String newAccessToken = authService.reissue(refreshToken);

        // 응답 헤더에 새로운 액세스 토큰을 추가
        response.addHeader("Authorization", "Bearer " + newAccessToken);

        return ResponseEntity.ok("액세스 토큰이 성공적으로 재발급되었습니다.");
    }
}
