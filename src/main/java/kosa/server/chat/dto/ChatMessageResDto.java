package kosa.server.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ChatMessageResponse", description = "채팅 메시지 응답 DTO")
public class ChatMessageResDto {

    @Schema(description = "채팅방 ID", example = "101", required = true)
    private Long roomId;            //  chat_room
    @Schema(description = "메시지 내용", example = "안녕하세요!", required = true)
    private String message;         //  chat_message
    @Schema(description = "보낸 사람의 로그인 ID", example = "user123", required = true)
    private String senderLoginId;   //  Authenticated
    @Schema(description = "리더 여부", example = "Y", required = true)
    private String leader;          //  party_member Y or N

    @Schema(description = "메시지 전송 시각", example = "14:25", type = "string", pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime createdAt;//  chat_message
}
