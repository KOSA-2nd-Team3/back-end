package kosa.server.auth.oauth2.service;

import kosa.server.common.security.user.CustomUserPrincipal;
import kosa.server.member.entity.Member;
import kosa.server.member.entity.Role;
import kosa.server.member.enums.RoleType;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import kosa.server.member.repository.jpa.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberJpaRepository memberJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        String attributeKey = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        CustomUserPrincipal customUserPrincipal = new CustomUserPrincipal(attributes, attributeKey, registrationId);
        //회원이 아니라면 회원가입 처리
        Optional<Member> optionalMember = memberJpaRepository.findByLoginId(customUserPrincipal.getName());
        if (optionalMember.isEmpty()) {
            Role role = roleJpaRepository.findByRoleName(RoleType.USER.getKey())
                    .orElseThrow(() -> new RuntimeException("해당 권한 타입이 없습니다."));

            Member member = Member.builder()
                    .loginId(customUserPrincipal.getName())
                    .nickname(customUserPrincipal.getNickname())
                    .name(customUserPrincipal.getUsername())
                    .email(customUserPrincipal.getEmail())
                    .role(role)
                    .build();
            memberJpaRepository.save(member);
        }
        return customUserPrincipal;
    }
}
