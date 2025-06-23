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
public class ChatMessageResDto {

    private Long roomId;            //  chat_room
    private String message;         //  chat_message
    private String senderLoginId;   //  Authenticated
    private String leader;          //  party_member Y or N

    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime createdAt;//  chat_message
}
