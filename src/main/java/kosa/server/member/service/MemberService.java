package kosa.server.member.service;

import kosa.server.board.repository.PostRepository;
import kosa.server.common.code.ErrorCode;
import kosa.server.member.dto.response.ProfileResponseDto;
import kosa.server.member.entity.Member;
import kosa.server.member.exception.MemberNotFoundException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final PostRepository postRepository;

    public ProfileResponseDto getProfile(String loginId) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return ProfileResponseDto.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }

    public String updateNickname(String loginId, String nickname) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateNickname(nickname);
        memberJpaRepository.save(member);
        return nickname;
    }
}
