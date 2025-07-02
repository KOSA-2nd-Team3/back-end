package kosa.server.member.service;

import kosa.server.common.code.ErrorCode;
import kosa.server.member.dto.response.ProfileResponseDto;
import kosa.server.member.entity.Member;
import kosa.server.member.exception.DuplicateNicknameException;
import kosa.server.common.exception.MemberNotFoundException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;

    public ProfileResponseDto getProfile(String loginId) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return ProfileResponseDto.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }

    // 닉네임 사용 가능 여부 확인 (사용 가능하면 true, 사용 불가능하면 false)
    public boolean isNicknameAvailable(String nickname) {
        Optional<Member> memberByNickName = memberJpaRepository.findMemberByNickname(nickname);
        return memberByNickName.isEmpty();
    }

    @Transactional
    public String updateNickname(String loginId, String nickname) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 닉네임 중복 체크
        if (!isNicknameAvailable(nickname)) {
            throw new DuplicateNicknameException(ErrorCode.DUPLICATE_NICKNAME);
        }

        member.updateNickname(nickname);
        return member.getNickname();
    }
}
