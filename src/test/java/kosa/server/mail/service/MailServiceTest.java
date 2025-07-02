package kosa.server.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kosa.server.board.dto.response.MailTargetResponseDto;
import kosa.server.board.repository.PartyMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender notificationMailSender;

    @Mock
    private PartyMemberRepository partyMemberRepository;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void setUp() {
        // @Value로 주입되는 필드를 직접 설정
        ReflectionTestUtils.setField(mailService, "fixedServiceSenderEmail", "test@example.com");
    }

    @Test
    @DisplayName("메일을 정상적으로 발송할 수 있다")
    void sendMail_shouldSendEmailSuccessfully() throws MessagingException, UnsupportedEncodingException {
        // given
        MailTargetResponseDto target1 = new MailTargetResponseDto(
                "user1@test.com",
                "Netflix",
                "http://localhost:3000/post/1",
                4
        );

        MailTargetResponseDto target2 = new MailTargetResponseDto(
                "user2@test.com",
                "Netflix",
                "http://localhost:3000/post/1",
                4
        );

        List<MailTargetResponseDto> targets = List.of(target1, target2);

        // Mock 설정
        when(notificationMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("email-alert"), any(Context.class))).thenReturn("<html>Test HTML</html>");

        // when & then
        assertThatNoException().isThrownBy(() -> mailService.sendMail(targets));

        // verify
        verify(notificationMailSender, times(2)).createMimeMessage(); // 2개의 메일이므로 2번 호출
        verify(notificationMailSender, times(2)).send(mimeMessage);   // 2번 전송
        verify(templateEngine, times(2)).process(eq("email-alert"), any(Context.class)); // 2번 템플릿 처리
    }

    @Test
    @DisplayName("빈 리스트일 때는 메일을 발송하지 않는다")
    void sendMail_shouldNotSendWhenListIsEmpty() throws MessagingException, UnsupportedEncodingException {
        // given
        List<MailTargetResponseDto> emptyTargets = List.of();

        // when & then
        assertThatNoException().isThrownBy(() -> mailService.sendMail(emptyTargets));

        // verify
        verify(notificationMailSender, never()).createMimeMessage();
        verify(notificationMailSender, never()).send(any(MimeMessage.class));
        verify(templateEngine, never()).process(anyString(), any(Context.class));
    }

    @Test
    @DisplayName("템플릿 처리 시 올바른 변수가 전달된다")
    void sendMail_shouldPassCorrectVariablesToTemplate() throws MessagingException, UnsupportedEncodingException {
        // given
        MailTargetResponseDto target = new MailTargetResponseDto(
                "test@example.com",
                "Disney+",
                "http://localhost:3000/post/123",
                3
        );

        List<MailTargetResponseDto> targets = List.of(target);

        // Mock 설정
        when(notificationMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("email-alert"), any(Context.class))).thenReturn("<html>Test HTML</html>");

        // when
        mailService.sendMail(targets);

        // then - ArgumentCaptor를 사용하여 Context 내용 검증
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        
        verify(templateEngine).process(eq("email-alert"), contextCaptor.capture());
        
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getVariable("ottName")).isEqualTo("Disney+");
        assertThat(capturedContext.getVariable("postUrl")).isEqualTo("http://localhost:3000/post/123");
        assertThat(capturedContext.getVariable("partySize")).isEqualTo(3);
    }
}
