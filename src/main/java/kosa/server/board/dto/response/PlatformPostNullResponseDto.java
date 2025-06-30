package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(name = "PlatformPostNullResponse", description = "게시글이 없는 플랫폼 응답 DTO")
public class PlatformPostNullResponseDto {

    @Schema(description = "플랫폼 이름", example = "디즈니플러스", required = true)
    private String platformName;
    @Schema(description = "플랫폼 가격", example = "10900", required = true)
    private BigDecimal platformPrice;
}
