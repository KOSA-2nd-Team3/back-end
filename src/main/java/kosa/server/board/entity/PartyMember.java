package kosa.server.board.entity;

import jakarta.persistence.*;
import kosa.server.common.BaseEntity;
import kosa.server.member.entity.Member;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class PartyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(nullable = false)
    private String isOwner;

    @Builder
    public PartyMember(Member member, Post post, String isOwner) {
        this.member = member;
        this.post = post;
        this.isOwner = isOwner;
    }
}
