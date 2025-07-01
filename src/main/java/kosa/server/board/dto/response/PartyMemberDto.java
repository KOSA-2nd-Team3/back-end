package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kosa.server.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(name = "PartyMember", description = "파티 멤버 정보 DTO")
public class PartyMemberDto {
    @Schema(description = "파티 멤버 고유 ID", example = "11", required = true)
    private Long memberId;
    @Schema(description = "닉네임", example = "짱구", required = true)
    private String nickName;
    @Schema(description = "작성자 여부", example = "Y", required = true)
    private String isOwner;
    @Schema(description = "가입 일시", example = "2025-06-30T14:00:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;
}
