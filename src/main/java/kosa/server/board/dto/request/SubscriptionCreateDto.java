package kosa.server.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "SubscriptionCreate", description = "구독 생성 요청 DTO")
public class SubscriptionCreateDto {

    @Schema(description = "구독할 플랫폼 ID", example = "1", required = true)
    private Long platformId;
    @Schema(description = "로그인 ID", example = "user123", required = true)
    private String loginId;
}
