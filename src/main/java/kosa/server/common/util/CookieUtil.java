package kosa.server.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtil {

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .sameSite("Lax")
                .httpOnly(true)
                .secure(false)
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0) // maxAge를 0으로 설정하여 쿠키 즉시 삭제
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findFirst();
        }

        return Optional.empty();
    }
}
