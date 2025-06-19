package kosa.server.mail.service;

import jakarta.mail.MessagingException;
import kosa.server.member.entity.Member;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
public class MailServiceImplTest {

    @Autowired
    private MailServiceImpl mailService;

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
}


/*
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private MailService mailService;

    private MimeMessage mockMimeMessage;

    @BeforeEach
    void setUp() {
        mockMimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
    }

    @Test
    @DisplayName("메일을 보낼 수 있다.")
    void sendMail() throws MessagingException, UnsupportedEncodingException {

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

        mailService.sendMail(members, platform, partySize, fakePostId);


        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(2)).send(captor.capture());

        List<SimpleMailMessage> sentMessages = captor.getAllValues();

        List<String> expectedEmails = members.stream()
                .map(Member::getEmail)
                .toList();

        assertThat(sentMessages).hasSize(2);
        assertThat(sentMessages)
                .extracting(msg -> msg.getTo()[0])
                .containsExactlyInAnyOrderElementsOf(expectedEmails);

        for (SimpleMailMessage msg : sentMessages) {
            assertThat(msg.getSubject()).contains(platform);
            assertThat(msg.getText()).contains("파티 인원: 4");
        }

    }
}*/
