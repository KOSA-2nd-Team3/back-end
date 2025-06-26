package kosa.server.chat.repository.jpa;

import kosa.server.chat.entity.ChatRoom;
import kosa.server.chat.entity.ReadStatus;
import kosa.server.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadStatusJpaRepository extends JpaRepository<ReadStatus, Long> {
    List<ReadStatus> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    Long countByChatRoom_IdAndMember_LoginIdAndIsReadFalse(Long chatRoomId, String LoginId);
}
