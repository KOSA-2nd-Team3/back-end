package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MyPostResponseDto {

    private Long postId;
    private String platformName;
    private int currentCount;
    private int partySize;
    private BigDecimal price;
    private String imageUrl;
    private String isOwner;
    private String isExpired;
}
