package kosa.server.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProfileResponse", description = "프로필 정보 응답 DTO")
public class ProfileResponseDto {
    @Schema(description = "사용자 실명", example = "홍길동", required = true)
    private String name;
    @Schema(description = "닉네임", example = "짱구", required = true)
    private String nickname;
    @Schema(description = "이메일 주소", example = "user@example.com", required = true)
    private String email;
}
