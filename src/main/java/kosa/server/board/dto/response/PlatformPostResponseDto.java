package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformPostResponseDto {
    private String platformName;
    private int partySize;
    private int currentCount;
    private String memberName;
    private String isExpired;

    private Long postId;
}
