package kosa.server.common.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey secretKey;

    // 토큰 만료 시간을 상수로 관리 (밀리초 단위)
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; // 14일

    public JwtProvider(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getLoginId(String token) {
        return parseClaims(token).get("loginId", String.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public String createAccessToken(String loginId, String role) {
        return createToken(loginId, role, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String loginId, String role) {
        return createToken(loginId, role, REFRESH_TOKEN_EXPIRE_TIME);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 생성 로직 공통화
    private String createToken(String loginId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("loginId", loginId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }

}
