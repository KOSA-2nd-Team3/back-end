package kosa.server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Schema(name = "UserInfo", description = "사용자 정보 DTO")
public class UserInfoDto {

    @Schema(description = "로그인 ID", example = "user123", required = true)
    private String loginId;
    @Schema(description = "사용자 역할", example = "ROLE_USER", required = true)
    private String role;

}
