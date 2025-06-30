package kosa.server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "LoginRequest", description = "로그인 요청 DTO")
public class LoginRequestDto {

    @Schema(description = "로그인 ID", example = "user123", required = true)
    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @Schema(description = "비밀번호", example = "test1234!", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

}
