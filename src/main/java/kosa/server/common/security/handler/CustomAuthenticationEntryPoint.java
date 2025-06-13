package kosa.server.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kosa.server.common.code.ErrorCode;
import kosa.server.common.dto.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorCode errorCode;
        Throwable cause = authException.getCause();

        // JWT 관련 특정 예외 분기 처리
        if (cause instanceof ExpiredJwtException) {
            errorCode = ErrorCode.TOKEN_EXPIRED;
            log.warn("만료된 토큰입니다. : {}", request.getRequestURI());
        } else if (cause instanceof SecurityException ||
                cause instanceof MalformedJwtException ||
                cause instanceof UnsupportedJwtException ||
                cause instanceof IllegalArgumentException) {
            errorCode = ErrorCode.INVALID_TOKEN;
            log.warn("유효하지 않은 토큰입니다. : {}", request.getRequestURI());
        } else {
            // 그 외 모든 인증 실패 처리
            errorCode = ErrorCode.AUTHENTICATION_FAILED;
            log.warn(" 않은 사용자 접근: {} - Path: {}", authException.getMessage(), request.getRequestURI());
        }

        // ErrorCode를 사용하여 일관된 에러 응답을 전송하는 메서드 호출
        sendErrorResponse(request, response, errorCode);
    }

    /**
     * 공통화된 에러 응답 전송 메서드
     */
    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value()); // HTTP 상태 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ErrorCode의 정보를 사용하여 표준화된 ErrorResponseDto 생성
        ErrorResponseDto errorResponse = ErrorResponseDto.of(errorCode, request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
