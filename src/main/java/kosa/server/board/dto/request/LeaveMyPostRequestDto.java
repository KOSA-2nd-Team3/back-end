package kosa.server.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "LeaveMyPostRequest", description = "게시글에서 나가기 요청 DTO")
public class LeaveMyPostRequestDto {

    @Schema(description = "게시글 ID", example = "1001", required = true)
    private Long postId;
    @Schema(description = "로그인 ID", example = "user123", required = true)
    private String loginId;

}
