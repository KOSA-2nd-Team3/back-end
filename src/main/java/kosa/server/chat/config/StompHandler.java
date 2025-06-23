package kosa.server.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kosa.server.chat.service.ChatService;
import kosa.server.common.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Component
public class StompHandler implements ChannelInterceptor {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final ChatService chatService;
    private final JwtProvider jwtProvider;

    public StompHandler(ChatService chatService, JwtProvider jwtProvider) {
        this.chatService = chatService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            System.out.println("connect 요청 시 토큰 유효성 검증");
            
            // 헤더에서 토큰 가져오기
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new AuthenticationServiceException("유효하지 않은 토큰 형식입니다.");
            }
            String token = bearerToken.substring(7);

            // 토큰 검증
            jwtProvider.validateToken(token);

            System.out.println("토큰 검증 완료");
        }

        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            System.out.println("subscribe 검증");
            
            // 헤더에서 토큰 가져오기
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new AuthenticationServiceException("유효하지 않은 토큰 형식입니다.");
            }
            String token = bearerToken.substring(7);

            // 토큰 검증
            jwtProvider.validateToken(token);

            String loginId = jwtProvider.getLoginId(token);
            String roomId = accessor.getDestination().split("/")[2];

            if (!chatService.isRoomParticipant(loginId, Long.parseLong(roomId))) {
                throw new AuthenticationServiceException("해당 room에 권한이 없습니다.");
            }
        }

        return message;
    }
}
