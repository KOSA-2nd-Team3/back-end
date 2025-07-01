package kosa.server.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(name = "PartyMemberResponse", description = "파티 멤버 응답 DTO")
public class PartyMemberResponseDto {

    @Schema(description = "닉네임", example = "user123", required = true)
    private String nickName;
    @Schema(description = "리더 여부", example = "Y", required = true)
    private String leaderYn;
}
