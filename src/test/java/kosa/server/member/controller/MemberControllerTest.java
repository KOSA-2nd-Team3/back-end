package kosa.server.member.controller;

import kosa.server.common.security.user.CustomUserPrincipal;
import kosa.server.member.dto.response.ProfileResponseDto;
import kosa.server.member.entity.Member;
import kosa.server.member.entity.Role;
import kosa.server.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;  // @MockBean 대신 수동 등록

    private CustomUserPrincipal customUserDetails;

    @TestConfiguration
     static class TestConfig {
        @Bean
        public MemberService memberService() {
            return mock(MemberService.class);
        }
    }

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .loginId("test")
                .password("pw")
                .role(Role.builder().roleName("ROLE_USER").build())
                .name("name1")
                .nickname("nick1")
                .email("email")
                .build();
        customUserDetails = new CustomUserPrincipal(member);
    }

    @Test
    void getProfile_정상_응답() throws Exception {
        ProfileResponseDto responseDto = new ProfileResponseDto("name1", "nick1", "email");
        given(memberService.getProfile("test")).willReturn(responseDto);

        mockMvc.perform(get("/member/profile")
                        .with(user(customUserDetails)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void updateNickName_정상_응답() throws Exception {
        String newNickName = "새닉네임";
        given(memberService.updateNickname("test", newNickName)).willReturn(newNickName);

        mockMvc.perform(post("/member/nickName")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + newNickName + "\""))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
