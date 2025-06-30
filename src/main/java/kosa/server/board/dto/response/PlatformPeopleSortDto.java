package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@Schema(name = "PlatformPeopleSort", description = "인원 기준 플랫폼 정렬 DTO")
public class PlatformPeopleSortDto {
    @Schema(description = "게시글 ID", example = "11", required = true)
    private Long postId;
    @Schema(description = "플랫폼 이름", example = "디즈니플러스", required = true)
    private String platformName;
    @Schema(description = "게시글의 멤버 ID", example = "2", required = true)
    private String memberId; // post.memberId

    @Schema(description = "1인당 가격", example = "3300", required = true)
    private BigDecimal onePersonPrice;
    @Schema(description = "파티 최대 인원 수", example = "5", required = true)
    private int partySize;
    @Schema(description = "현재 참여 인원 수", example = "3", required = true)
    private int CurrentCount;
    @Schema(description = "남은 자리 수", example = "2", required = true)
    private int leftSeat;
    @Schema(description = "참여율", example = "0.6", required = true)
    private double rate; // CurrentCount / partySize

}
