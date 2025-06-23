package kosa.server.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class PlatformPeopleSortDto {
    private Long postId;
    private String platformName;
    private String memberId; // post.memberId

    private BigDecimal onePersonPrice;
    private int partySize;
    private int CurrentCount;
    private int leftSeat;
    private double rate; // CurrentCount / partySize

}
