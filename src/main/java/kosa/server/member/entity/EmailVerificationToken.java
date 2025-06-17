package kosa.server.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public EmailVerificationToken(String token, LocalDateTime expiryDate, Member member) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.member = member;
    }

    public EmailVerificationToken(String token, Member member) {
        this.token = token;
        this.member = member;  // 여기 수정
    }
}


