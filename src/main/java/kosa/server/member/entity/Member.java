package kosa.server.member.entity;

import jakarta.persistence.*;
import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Post;
import kosa.server.common.BaseEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 인조키

    @Column(name = "login_id", unique = true, nullable = false, length = 50)
    private String loginId; // 실제 로그인 ID

    //Social Login 시 password 없음
    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private boolean enabled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role; // 회원 권한 (USER, ADMIN)

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Post> post = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<PartyMember> partyMembers = new ArrayList<>();

    @Builder
    private Member(String loginId, String password, String nickname, String name, String email, Role role, boolean enabled) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.role = role;
        this.enabled = false;
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        this.refreshTokens.add(refreshToken);
        refreshToken.setMember(this);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void verifyEmail() {
        this.enabled = true;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
