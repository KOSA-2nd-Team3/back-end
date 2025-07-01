package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(name = "PlatformCategory", description = "플랫폼 카테고리 정보 DTO")
public class PlatformCategoryDto {

    @Schema(description = "플랫폼 ID", example = "1", required = true)
    private Long platformId;
    @Schema(description = "플랫폼 이름", example = "넷플릭스", required = true)
    private String platformName;
    @Schema(description = "플랫폼 가격", example = "12900", required = true)
    private BigDecimal price;
    @Schema(description = "카테고리 코드)", example = "1", required = true)
    private int category;
    @Schema(description = "최대 인원 수", example = "4", required = true)
    private int capacity;
    @Schema(description = "플랫폼 이미지 URL", example = "http://localhost:8080/netflix.png", required = true)
    private String imageUrl;
}
