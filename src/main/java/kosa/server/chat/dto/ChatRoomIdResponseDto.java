package kosa.server.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(name = "ChatRoomIdResponse", description = "채팅방 ID 응답 DTO")
public class ChatRoomIdResponseDto {

    @Schema(description = "생성된 채팅방 ID", example = "101", required = true)
    private Long chatRoomId;
}
