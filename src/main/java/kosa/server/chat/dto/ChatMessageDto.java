package kosa.server.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ChatMessage", description = "채팅 메시지 요청 DTO")
public class ChatMessageDto {

    @Schema(description = "채팅방 ID", example = "101", required = true)
    private Long roomId;
    @Schema(description = "메시지 내용", example = "안녕하세요!", required = true)
    private String message;
    @Schema(description = "보낸 사람의 로그인 ID", example = "user123", required = true)
    private String senderLoginId;

}
