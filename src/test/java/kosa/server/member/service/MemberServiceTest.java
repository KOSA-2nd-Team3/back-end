package kosa.server.member.service;

import kosa.server.member.dto.response.ProfileResponseDto;
import kosa.server.member.entity.Member;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("유저 닉네임을 변경할 수 있다.")
    void updateNickname() {
        //given
        String nickname = "after";
        Member member = Member.builder()
                .loginId("test")
                .password("password")
                .nickname("before")
                .name("name")
                .email("email")
                .build();
        memberJpaRepository.save(member);
        //when
        memberService.updateNickname("test", nickname);
        //then
        Member changedMember = memberJpaRepository.findByLoginId(member.getLoginId())
                .orElseThrow(()->new IllegalArgumentException("error"));
        Assertions.assertThat(changedMember.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("유저 프로필을 조회 한다.")
    void getProfile() {
        //given
        String name = "name1";
        String nickname = "after";
        String email = "email1";
        Member member = Member.builder()
                .loginId("test")
                .password("password")
                .nickname(nickname)
                .name(name)
                .email(email)
                .build();
        memberJpaRepository.save(member);
        //when
        ProfileResponseDto dto = memberService.getProfile("test");
        //then
        Assertions.assertThat(dto).isNotNull()
        .isInstanceOf(ProfileResponseDto.class);
        Assertions.assertThat(dto.getNickname()).isEqualTo(nickname);
        Assertions.assertThat(dto.getName()).isEqualTo(name);
        Assertions.assertThat(dto.getEmail()).isEqualTo(email);
    }
}