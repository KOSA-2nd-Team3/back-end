package kosa.server.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "MyChatListResponse", description = "내 채팅방 목록 응답 DTO")
public class MyChatListResDto {

    @Schema(description = "채팅방 ID", example = "101", required = true)
    private Long roomId;
    @Schema(description = "채팅방 이름", example = "같이 점심 먹어요", required = true)
    private String roomName;
    @Schema(description = "그룹 채팅 여부", example = "Y", required = true)
    private String isGroupChat;
    @Schema(description = "읽지 않은 메시지 수", example = "5", required = true)
    private Long unReadCount;
}
