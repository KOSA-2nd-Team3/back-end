package kosa.server.chat.entity;

import jakarta.persistence.*;
import kosa.server.common.BaseEntity;
import kosa.server.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ReadStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id", nullable = false)
    private ChatMessage chatMessage;

    @Column(nullable = false)
    private Boolean isRead;

    @Builder
    private ReadStatus(ChatRoom chatRoom, Member member, ChatMessage chatMessage, Boolean isRead) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.chatMessage = chatMessage;
        this.isRead = isRead;
    }

    public void updateIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
