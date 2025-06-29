package kosa.server.common.security.user;

import kosa.server.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomUserPrincipal implements UserDetails, OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;
    private final String attributeKey;
    private final String registrationId;

    //일반 Login 생성
    public CustomUserPrincipal(Member member) {
        this.member = member;
        this.attributes = null;
        this.attributeKey = null;
        this.registrationId = null;
    }

    //OAuth2 생성
    public CustomUserPrincipal(Map<String, Object> attributes, String attributeKey, String registrationId) {
        this.member = null;
        this.attributes = attributes;
        this.attributeKey = attributeKey;
        this.registrationId = registrationId;
    }

    //공통
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (member == null) return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getRoleName()));
    }

    //OAuth2 전용
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //loginId 반환
    @Override
    public String getName() {
        if (registrationId == null) {
            return member.getLoginId();
        }
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

    //name 반환
    @Override
    public String getUsername() {
        if (registrationId == null) {
            return member.getName();
        }
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

    //UserDetails
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
