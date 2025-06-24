package kosa.server.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import kosa.server.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    @Value("${spring.mail.username}")
    private String fixedServiceSenderEmail;
    private final SpringTemplateEngine templateEngine;

    //구인글 모집이 완료되었을 때 사용할 메서드
    public void sendMail(List<Member> members, String platFormName, int partySize, Long postId)
                                        throws UnsupportedEncodingException, MessagingException {

        String postUrl = "http://localhost:8080/post/" + postId;

        for (Member member : members) {
            MimeMessage message = notificationMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 템플릿 context 세팅
            Context context = new Context();
            context.setVariable("ottName", platFormName);
            context.setVariable("postUrl", postUrl);
            context.setVariable("partySize", partySize);

            String html = templateEngine.process("email-alert", context);

            helper.setTo(member.getEmail());
            helper.setFrom(fixedServiceSenderEmail);
            helper.setSubject(MimeUtility.encodeText("[OTT Moa] '" + platFormName + "' 파티 모집이 완료되었습니다.", "UTF-8", "B"));

            helper.setText(html, true);

            notificationMailSender.send(message);
        }
    }
}
