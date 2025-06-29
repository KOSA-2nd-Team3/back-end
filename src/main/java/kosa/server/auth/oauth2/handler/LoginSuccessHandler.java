package kosa.server.auth.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kosa.server.common.security.jwt.JwtProvider;
import kosa.server.common.security.user.CustomUserPrincipal;
import kosa.server.common.util.CookieUtil;
import kosa.server.member.entity.Member;
import kosa.server.member.entity.RefreshToken;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import kosa.server.member.repository.jpa.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserPrincipal oAuth2User = (CustomUserPrincipal) authentication.getPrincipal();

        String token = jwtProvider.createAccessToken(oAuth2User.getName(), "ROLE_USER");
        String refreshToken = jwtProvider.createRefreshToken(oAuth2User.getName(), "ROLE_USER");

        // 리프레시 토큰 DB에 저장 (일반 로그인과 동일)
        Member member = memberJpaRepository.findByLoginId(oAuth2User.getName())
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다.")); //todo 예외처리
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000);
        RefreshToken newRefreshToken = RefreshToken.builder()
                .member(member)
                .token(refreshToken)
                .expiredAt(refreshTokenExpireTime)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        // 쿠키에 리프레시 토큰 저장 (일반 로그인과 동일)
        cookieUtil.addCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 14);

        // **수정된 부분: 액세스 토큰을 URL 파라미터로 전달**
        // 보안상 이유로 URL 파라미터 대신 임시 인증 코드를 생성하여 전달
        String tempAuthCode = UUID.randomUUID().toString();

        // 임시 인증 코드와 액세스 토큰을 연결하여 캐시/DB에 저장 (5분 유효)
        // 여기서는 간단히 메모리 캐시를 사용하지만, 실제 운영에서는 Redis 등을 사용하는 것이 좋습니다.
        TempAuthStorage.store(tempAuthCode, token, 5 * 60 * 1000); // 5분

        // 프론트엔드로 리다이렉트 (임시 인증 코드 포함)
        response.sendRedirect("http://localhost:5173/auth/social-callback?code=" + tempAuthCode);
    }
}
