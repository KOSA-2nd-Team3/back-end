package kosa.server.board.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Schema(name = "PlatformResponse", description = "플랫폼 정보 응답 DTO")
public class PlatformResponseDto {

    @Schema(description = "플랫폼 ID", example = "1", required = true)
    private Long platformId;
    @Schema(description = "플랫폼 이름", example = "넷플릭스", required = true)
    private String name;
    @Schema(description = "최대 인원 수", example = "4", required = true)
    private int capacity;
    @Schema(description = "플랫폼 가격", example = "12900", required = true)
    private Long price;
    @Schema(description = "카테고리 코드", example = "1", required = true)
    private int category;
    @Schema(description = "구독 단위", example = "1", required = true)
    private int monthUnit;

    @Builder
    public PlatformResponseDto(Long platformId, String name, int capacity, Long price, int category, int monthUnit) {
        this.platformId = platformId;
        this.name =  name;
        this.capacity = capacity;
        this.price = price;
        this.category = category;
        this.monthUnit = monthUnit;
    }
}
