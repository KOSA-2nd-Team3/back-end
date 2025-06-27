package kosa.server.chat.repository.jpa;

import kosa.server.board.entity.PartyMember;
import kosa.server.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN FETCH cr.chatParticipants cp JOIN FETCH cp.member m" +
            " WHERE m.loginId = :loginId AND cr.isGroupChat = :isGroupChat")
    List<ChatRoom> findByIsGroupChatAndLoginId(@Param("isGroupChat") String isGroupChat,@Param("loginId") String loginId);

    @Query("SELECT cr FROM ChatRoom cr JOIN FETCH cr.chatParticipants cp JOIN FETCH cp.member m" +
            " WHERE m.loginId = :memberLoginId AND cr.post.id = :postId")
    Optional<ChatRoom> findByPostIdAndMemberLoginId(String memberLoginId, Long postId);

    Optional<ChatRoom> findByPostId(Long postId);
}
