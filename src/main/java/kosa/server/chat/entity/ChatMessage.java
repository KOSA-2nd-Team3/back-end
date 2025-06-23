package kosa.server.chat.entity;

import jakarta.persistence.*;
import kosa.server.common.BaseEntity;
import kosa.server.member.entity.Member;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 500)
    private String content;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReadStatus> readStatuses = new ArrayList<>();

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member member, String content, List<ReadStatus> readStatuses) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.content = content;
        this.readStatuses = readStatuses;
    }
}
