package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MyPostOneResponseDto {

    // platform
    private String platformName;
    private BigDecimal price;
    private int limitCount;
    // Post
    private Long postId;
    private int currentCount;
    private int partySize;
    private int durationMonth;
    private String hostId;
    private String hostPwd;
    private String isExpired;
    private List<PartyMemberDto> members;
    private LocalDateTime expirationDate;

    //partyMember
    private Long memberId;
    private String isOwner;
    private String platformImageUrl;
}
