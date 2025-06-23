package kosa.server.chat.dto;

import lombok.Data;

@Data
public class ChatRoomCreateDto {

    private Long postId;
    private String roomName;
    private Long maxMembers;
}
