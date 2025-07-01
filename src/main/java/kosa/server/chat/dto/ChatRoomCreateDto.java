package kosa.server.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ChatRoomCreate", description = "채팅방 생성 요청 DTO")
public class ChatRoomCreateDto {

    @Schema(description = "채팅방과 연결될 게시글 ID", example = "11", required = true)
    private Long postId;
    @Schema(description = "채팅방 이름", example = "Netflix", required = true)
    private String roomName;
    @Schema(description = "채팅방 최대 인원 수", example = "3", required = true)
    private Long maxMembers;
}
