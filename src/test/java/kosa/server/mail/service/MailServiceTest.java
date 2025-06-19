package kosa.server.mail.service;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import kosa.server.member.entity.Member;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private MailService mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("메일을 보낼 수 있다.")
    void sendMail() throws MessagingException, IOException {
        // given
        Member member1  = Member.builder()
                .email("ghyunjin0913@naver.com")
                .name("테스트유저1")
                .build();
        Member member2 = Member.builder()
                .email("ghyunjin0913@gmail.com")
                .name("테스트유저2")
                .build();
        List<Member> members = List.of(member1, member2);

        String platform = "넷플릭스";
        int partySize = 2;
        Long fakePostId = 999L;

        // when
        mailService.sendMail(members, platform, partySize, fakePostId);

        // then
        verify(mailService, times(1)).sendMail(members, platform, partySize, fakePostId);

        verifyNoMoreInteractions(mailService);
    }
}



/*@SpringBootTest
@ActiveProfiles("test")
public class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Test
    void setMail() throws InterruptedException, MessagingException, UnsupportedEncodingException {
        // given
        Member member = Member.builder()
                .email("ghyunjin0913@naver.com")
                .name("테스트유저")
                .build();
        List<Member> members = List.of(member);

        // when
        mailService.sendMail(members, "Netflix", 1, 999L);

        // then
        assertDoesNotThrow(() ->
                mailService.sendMail(members, "Netflix", 4, 123L)
        );
    }
}*/

