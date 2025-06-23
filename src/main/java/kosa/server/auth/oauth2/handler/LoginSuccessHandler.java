package kosa.server.auth.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kosa.server.common.security.jwt.JwtProvider;
import kosa.server.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserPrincipal oAuth2User = (CustomUserPrincipal) authentication.getPrincipal();

        String token = jwtProvider.createAccessToken(oAuth2User.getName(), oAuth2User.getEmail());

        response.setHeader("Authorization", "Bearer " + token);
        response.sendRedirect("http://localhost:5173/");
    }
}
