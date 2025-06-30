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
@Schema(name = "ChatRoomListResponse", description = "채팅방 리스트 응답 DTO")
public class ChatRoomListResDto {

    @Schema(description = "채팅방 ID", example = "42", required = true)
    private Long roomId;
    @Schema(description = "채팅방 이름", example = "Netflix", required = true)
    private String roomName;
    @Schema(description = "서비스 이름", example = "Netflix", required = true)
    private String serviceName;
    @Schema(description = "방장 ID", example = "leader123", required = true)
    private String leaderId;
    @Schema(description = "읽지 않은 메시지 수", example = "3", required = true)
    private Long unreadCount;

    @Schema(description = "채팅방 생성일", example = "2025.06.30", type = "string", pattern = "yyyy.MM.dd")
    @JsonFormat(pattern="yyyy.MM.dd")
    private LocalDateTime createdAt;
}
