package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlatformPostResponseDto {
    private Long postId;
    private String leaderName;
    private String platformName;
    private Long platformPrice;
    private int monthUnit;
    private int currentCount;
    private int partySize;
    private String isExpired;
    private LocalDateTime startDate;
    private LocalDateTime createdAt;

}
