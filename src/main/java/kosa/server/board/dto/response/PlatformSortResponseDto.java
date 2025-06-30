package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@Schema(name = "PlatformSortResponse", description = "플랫폼 정렬 기준 응답 DTO")
public class PlatformSortResponseDto {

    @Schema(description = "플랫폼 이름", example = "넷플릭스", required = true)
    private String platformName;
    @Schema(description = "게시글 수", example = "15", required = true)
    private Long postCount;
    @Schema(description = "1인당 가격", example = "4250", required = true)
    private BigDecimal onePersonPrice;
    @Schema(description = "최대 인원 수", example = "4", required = true)
    private int capacity;
}
