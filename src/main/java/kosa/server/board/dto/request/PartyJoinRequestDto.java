package kosa.server.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(name = "PartyJoinRequest", description = "파티 참여 요청 DTO")
public class PartyJoinRequestDto {

    @Schema(description = "게시글 ID", example = "1001", required = true)
    private Long postId;
    @Schema(description = "사용자 로그인 ID", example = "user123", required = true)
    private String loginId;
    @Schema(description = "작성자 여부", example = "N", required = true)
    private String isOwner;

    @Builder
    public PartyJoinRequestDto(Long postId, String loginId, String isOwner) {
        this.postId = postId;
        this.loginId = loginId;
        this.isOwner = isOwner;
    }
}
