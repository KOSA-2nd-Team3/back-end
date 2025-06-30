package kosa.server.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

@Data
@Schema(name = "StartRequest", description = "시작 요청 DTO")
public class StartRequestDto {

    @Schema(description = "게시글 ID", example = "1001", required = true)
    private Long PostId;
    @Schema(description = "구독 기간", example = "6", required = true)
    private int durationMonth;
}
