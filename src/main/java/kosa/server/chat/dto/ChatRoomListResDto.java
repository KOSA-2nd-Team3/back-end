package kosa.server.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResDto {

    private Long roomId;
    private String roomName;
    private String serviceName;
    private String leaderId;
    private Long unreadCount;

    @JsonFormat(pattern="yyyy.MM.dd")
    private LocalDateTime createdAt;
}
