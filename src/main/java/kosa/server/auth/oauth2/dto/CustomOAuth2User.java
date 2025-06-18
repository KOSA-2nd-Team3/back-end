package kosa.server.auth.oauth2.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String attributeKey;
    private final String registrationId;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        switch (registrationId) {
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get(attributeKey);
                return (String) response.get("id");
            case "google":
                return (String) attributes.get(attributeKey);
            case "kakao":
                return attributes.get(attributeKey).toString();
            default:
                return null;
        }
    }

    public String getEmail() {
        switch (registrationId) {
            case "google":
                return (String) attributes.get("email");
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                return (String) kakaoAccount.get("email");
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get(attributeKey);
                return (String) response.get("email");
            default:
                return null;
        }
    }

    public String getUsername() {
        switch (registrationId) {
            case "google":
                return (String) attributes.get("name");
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>)  attributes.get("properties"); //todo 비즈니스 정보 통과가 된다면 kakao_account로 변경
                return (String) kakaoAccount.get("nickname"); // 추후 name변경
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get(attributeKey);
                return (String) response.get("name");
            default:
                return null;
        }
    }

    public String getNickname() {
        switch (registrationId) {
            case "google":
                return "G" + attributes.get("name");
            case "kakao":
                Map<String, Object> profile = (Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile");
                return "K" + profile.get("nickname");
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get(attributeKey);
                return "N" + response.get("nickname");
            default:
                return null;
        }
    }
}
