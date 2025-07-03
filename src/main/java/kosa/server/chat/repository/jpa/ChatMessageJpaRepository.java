package kosa.server.chat.repository.jpa;

import kosa.server.chat.dto.ChatMessageResDto;
import kosa.server.chat.entity.ChatMessage;
import kosa.server.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

    @Query("SELECT new kosa.server.chat.dto.ChatMessageResDto(" +
            "m.chatRoom.id, m.content, mem.loginId, mem.nickname, pm.isOwner, m.createdAt) " +
            "FROM ChatMessage m " +
            "JOIN m.member mem " +
            "JOIN m.chatRoom cr " +
            "JOIN cr.post p " +
            "JOIN mem.partyMembers pm " +
            "WHERE pm.post = p AND m.chatRoom.id = :roomId " +
            "ORDER BY m.createdAt ASC")
    List<ChatMessageResDto> findMessageDtosByRoomId(@Param("roomId") Long roomId);
}
