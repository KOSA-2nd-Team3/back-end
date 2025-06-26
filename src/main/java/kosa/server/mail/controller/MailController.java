package kosa.server.mail.controller;

import jakarta.mail.MessagingException;
import kosa.server.board.dto.response.MailSendDto;
import kosa.server.mail.service.MailService;
import kosa.server.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @GetMapping("/mailSend/{postId}")
    public ResponseEntity<?> sendMail(@PathVariable Long postId)
                                            throws MessagingException, UnsupportedEncodingException {
        mailService.prepareAndSendMail(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
