package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MyPostOneResponseDto {

    // platform
    private String platformName;
    private BigDecimal price;

    // Post
    private Long postId;
    private int currentCount;
    private int partySize;
    private int durationMonth;
    private String hostId;
    private String hostPwd;
    private String isExpired;
    private List<PartyMemberDto> members;

    //partyMember
    private Long memberId;
    private String isOwner;

}
