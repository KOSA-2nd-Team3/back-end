package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MyPostResponseDto {

    private Long postId;
    private String platformName;
    private int currentCount;
    private int partySize;
    private BigDecimal price;
    private String isOwner;
    private String isExpired;
    private String platformImageUrl;
    private LocalDateTime createdAt;
}
