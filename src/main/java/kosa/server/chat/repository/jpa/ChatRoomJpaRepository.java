package kosa.server.chat.repository.jpa;

import kosa.server.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByIsGroupChat(String isGroupChat);
}
