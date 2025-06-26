package kosa.server.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import kosa.server.board.dto.response.MailTargetResponseDto;
import kosa.server.board.entity.PartyMember;
import kosa.server.board.repository.PartyMemberRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.board.service.PostService;
import kosa.server.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    @Qualifier("notificationMailSender")
    private final JavaMailSender notificationMailSender;
    private final PartyMemberRepository partyMemberRepository;
    @Value("${spring.mail.username}")
    private String fixedServiceSenderEmail;
    private final SpringTemplateEngine templateEngine;

    // 동기 메서드: 데이터 준비
    public void prepareAndSendMail(Long postId) throws UnsupportedEncodingException, MessagingException {
        List<PartyMember> members = partyMemberRepository.findByPostId(postId);
        List<MailTargetResponseDto> targets = members.stream()
                .map(m -> new MailTargetResponseDto(
                        m.getMember().getEmail(),
                        m.getPost().getPlatform().getName(),
                        "http://localhost:8080/post/" + postId,
                        m.getPost().getPartySize()
                ))
                .toList();
        sendMail(targets); // 비동기 메서드 호출
    }

    //구인글 모집이 완료되었을 때 사용할 메서드
    @Async
    public void sendMail(List<MailTargetResponseDto> targets)
                                        throws UnsupportedEncodingException, MessagingException {
        for (MailTargetResponseDto target : targets) {
            MimeMessage message = notificationMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 템플릿 context 세팅
            Context context = new Context();
            context.setVariable("ottName", target.getPlatformName());
            context.setVariable("postUrl", target.getPostUrl());
            context.setVariable("partySize", target.getPartySize());

            String html = templateEngine.process("email-alert", context);

            helper.setTo(target.getEmail());
            helper.setFrom(fixedServiceSenderEmail);
            helper.setSubject(MimeUtility.encodeText("[OTT Moa] '" + target.getPlatformName() + "' 파티 모집이 완료되었습니다.", "UTF-8", "B"));

            helper.setText(html, true);

            notificationMailSender.send(message);
        }
    }
}
