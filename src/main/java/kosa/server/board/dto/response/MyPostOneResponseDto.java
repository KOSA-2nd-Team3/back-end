package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MyPostOneResponseDto {

    // platform
    private String platformName;
    private BigDecimal price;

    // Post
    private int currentCount;
    private int partySize;
    private int durationMonth;
    private String hostId;
    private String hostPwd;
    private String isExpired;

    //partyMember
    private Long memberId;
    private String isOwner;

}
