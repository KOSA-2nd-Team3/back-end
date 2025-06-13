package kosa.server.member.entity;

import jakarta.persistence.*;
import kosa.server.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "refresh_token", nullable = false, length = 512)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(length = 100)
    private String deviceInfo;

    @Builder
    private RefreshToken(Member member, String token, LocalDateTime expiredAt, String deviceInfo) {
        this.member = member;
        this.token = token;
        this.expiredAt = expiredAt;
        this.deviceInfo = deviceInfo;
    }

    protected void setMember(Member member) {
        this.member = member;
    }
}
