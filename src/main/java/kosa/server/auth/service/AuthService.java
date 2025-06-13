package kosa.server.auth.service;

import kosa.server.auth.dto.JoinRequestDto;
import kosa.server.auth.dto.LoginRequestDto;
import kosa.server.auth.dto.UserInfoDto;
import kosa.server.common.code.ErrorCode;
import kosa.server.common.security.jwt.JwtProvider;
import kosa.server.member.entity.Member;
import kosa.server.member.entity.RefreshToken;
import kosa.server.member.entity.Role;
import kosa.server.member.enums.RoleType;
import kosa.server.member.exception.*;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import kosa.server.member.repository.jpa.RefreshTokenRepository;
import kosa.server.member.repository.jpa.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final MemberJpaRepository memberJpaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final JwtProvider jwtProvider;

    public void joinMember(JoinRequestDto joinRequestDto) {
        // 비밀번호 확인 검증
        if (!joinRequestDto.getPassword().equals(joinRequestDto.getPasswordCheck())) {
            throw new PasswordMismatchException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 아이디 중복 검증
        String loginId = joinRequestDto.getLoginId();
        if (memberJpaRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        String email = joinRequestDto.getEmail();
        // 이메일 중복 검증
        if (memberJpaRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL);
        }
        // 유저 타입 가져오기
        Role role = roleJpaRepository.findByRoleName(RoleType.USER.getKey())
                .orElseThrow(() -> new RuntimeException("해당 권한 타입이 없습니다."));

        // 멤버 빌더 엔티티 생성(암호화 포함)
        Member newMember = Member.builder()
                .loginId(joinRequestDto.getLoginId())
                .password(passwordEncoder.encode(joinRequestDto.getPassword()))
                .nickname(joinRequestDto.getNickname())
                .name(joinRequestDto.getName())
                .email(joinRequestDto.getEmail())
                .role(role)
                .build();

        // 멤버 저장
        memberJpaRepository.save(newMember);
        log.info("회원가입 성공 ID : {}, loginId : {}, nickname : {}", newMember.getId(), newMember.getLoginId(), newMember.getNickname());
    }

    public Map<String, Object> login(LoginRequestDto loginRequestDto) {
        String loginId = loginRequestDto.getLoginId();
        Member findMember = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), findMember.getPassword())) {
            throw new LoginFailedException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 액세스 토큰 생성
        String accessToken = jwtProvider.createAccessToken(findMember.getLoginId(), findMember.getRole().getRoleName());

        // 리프레시 토큰 생성
        String refreshToken = jwtProvider.createRefreshToken(findMember.getLoginId(), findMember.getRole().getRoleName());

        // 리프레시 토큰의 만료 시간
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000);

        // 토큰 빌더 엔티티 생성
        RefreshToken newRefreshToken = RefreshToken.builder()
                .member(findMember)
                .token(refreshToken)
                .expiredAt(refreshTokenExpireTime)
                .build();

        // 토큰 저장
        refreshTokenRepository.save(newRefreshToken);


        // 사용자 정보 생성
        UserInfoDto userInfo = new UserInfoDto(
                findMember.getLoginId(),
                findMember.getRole().getRoleName()
        );

        Map<String, Object> authData = new HashMap<>();
        authData.put("accessToken", accessToken);
        authData.put("refreshToken", refreshToken);
        authData.put("userInfo", userInfo);

        log.info("로그인 성공 및 토큰 생성. loginId = {}", loginId);
        return authData;
    }

    public void logout(String refreshToken) {
        // 토큰 찾고 있으면 제거
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            refreshTokenRepository.delete(token);
            log.info("리프레시 토큰을 DB에서 제거했습니다. user: {}", token.getMember().getLoginId());
        });
    }


    // 미완성
    public String reissue(String refreshToken) {
        // 리프레시 토큰 유효성 검증 (만료, 위조 등)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }

        // DB에 저장된 토큰인지, 그리고 만료되지 않았는지 확인
        RefreshToken findRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        // 토큰에서 사용자 정보(loginId, role) 추출
        String loginId = jwtProvider.getLoginId(refreshToken);
        String role = jwtProvider.getRole(refreshToken);

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(loginId, role);

        log.info("액세스 토큰을 재발급했습니다. user: {}", loginId);

        // 컨트롤러에 새로운 액세스 토큰 반환
        return newAccessToken;
    }
}
