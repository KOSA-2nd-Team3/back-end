package kosa.server.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kosa.server.auth.dto.JoinRequestDto;
import kosa.server.auth.dto.LoginRequestDto;
import kosa.server.auth.dto.UserInfoDto;
import kosa.server.auth.exception.*;
import kosa.server.common.code.ErrorCode;
import kosa.server.common.exception.MemberNotFoundException;
import kosa.server.common.security.jwt.JwtProvider;
import kosa.server.member.entity.EmailVerificationToken;
import kosa.server.member.entity.Member;
import kosa.server.member.entity.RefreshToken;
import kosa.server.member.entity.Role;
import kosa.server.member.enums.RoleType;
import kosa.server.member.exception.*;
import kosa.server.member.repository.jpa.EmailVerificationTokenRepository;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import kosa.server.member.repository.jpa.RefreshTokenRepository;
import kosa.server.member.repository.jpa.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final MemberJpaRepository memberJpaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final SpringTemplateEngine templateEngine;

    @Value("${custom.site-host}")
    private String siteHost;

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
                .orElseThrow(() -> new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND));

        // 멤버 빌더 엔티티 생성(암호화 포함)
        Member newMember = Member.builder()
                .loginId(joinRequestDto.getLoginId())
                .password(passwordEncoder.encode(joinRequestDto.getPassword()))
                .nickname(joinRequestDto.getNickname())
                .name(joinRequestDto.getName())
                .email(joinRequestDto.getEmail())
                .role(role)
                .enabled(false)
                .build();

        // 멤버 저장
        memberJpaRepository.save(newMember);

        // 인증 토큰 생성
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken(token, newMember);
        tokenRepository.save(verificationToken);

        log.info("회원가입 성공 ID : {}, loginId : {}, nickname : {}", newMember.getId(), newMember.getLoginId(), newMember.getNickname());
    }

    public void sendVerificationEmail(String email) throws MessagingException {
        Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 랜덤 인증 토큰 생성
        String token = UUID.randomUUID().toString();

        // 토큰 저장
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .member(member)
                .expiryDate(LocalDateTime.now().plusHours(24)) // 24시간 유효
                .build();

        tokenRepository.save(verificationToken);

        // 이메일 전송
        String verificationLink = "http://" + siteHost + "/api/auth/login/verify?token=" + token;

        Context context = new Context();
        context.setVariable("verificationLink", verificationLink);

        String html = templateEngine.process("verification-email", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("회원가입 이메일 인증");
        helper.setText(html, true);

        mailSender.send(mimeMessage);
    }

    public boolean verifyEmailToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException(ErrorCode.INVALID_TOKEN));

        // 만료 확인
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken); // 만료된 토큰 삭제
            return false;
        }

        Member member = verificationToken.getMember();
        member.setEnabled(true); // 인증 완료 처리
        memberJpaRepository.save(member); // 변경사항 저장

        tokenRepository.delete(verificationToken); // 사용한 토큰 제거
        return true;
    }


    public Map<String, Object> login(LoginRequestDto loginRequestDto) {
        String loginId = loginRequestDto.getLoginId();
        Member findMember = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        //  이메일 인증 여부 확인
        if (!findMember.isEnabled()) {
            throw new EmailNotVerifiedException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

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

    public HashMap<String, String> reissue(String refreshToken) {
        // 리프레시 토큰 유효성 검증 (만료, 위조 등)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }

        // DB에 저장된 토큰인지, 그리고 만료되지 않았는지 확인
        RefreshToken findRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.delete(findRefreshToken);

        // 토큰에서 사용자 정보(loginId, role) 추출 및 검증(위에서 진행하긴 했음)
        String loginId = jwtProvider.getLoginId(refreshToken);
        String role = jwtProvider.getRole(refreshToken);

        // 리프레시 토큰 엔티티 저장하기 위해 추출
        Member findMember = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(loginId, role);
        // 새로운 리프레시 토큰 생성
        String newRefreshToken = jwtProvider.createRefreshToken(loginId, role);
        // 만료시간(DB저장 용)
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000);

        // 토큰 빌더 엔티티 생성
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .member(findMember)
                .token(newRefreshToken)
                .expiredAt(refreshTokenExpireTime)
                .build();
        // DB 저장
        refreshTokenRepository.save(newRefreshTokenEntity);

        log.info("액세스 토큰을 재발급했습니다. user: {}", loginId);
        log.info("리프레시 토큰을 재발급했습니다. user: {}", loginId);

        // 컨트롤러 리턴용 맵
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", newAccessToken);
        tokenMap.put("refreshToken", newRefreshToken);
        // 컨트롤러에 새로운 액세스, 리프레시 토큰 반환
        return tokenMap;
    }
}
