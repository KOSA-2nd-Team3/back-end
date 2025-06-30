package kosa.server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(name = "JoinRequest", description = "회원 가입 요청 DTO")
public class JoinRequestDto {

    @Schema(description = "로그인 ID", example = "user123", required = true)
    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "아이디는 영문과 숫자로 4~20자 이내로 입력해주세요.")
    private String loginId;

    @Schema(description = "비밀번호", example = "test123!", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~16자 이내로 입력해주세요."
    )
    private String password;

    // 비밀번호 확인을 위한 필드
    @Schema(description = "비밀번호 확인", example = "Passw0rd!", required = true)
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordCheck;

    @Schema(description = "닉네임", example = "짱구", required = true)
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Length(min = 2, max = 10, message = "닉네임은 2~10자 이내로 입력해주세요.")
    private String nickname;

    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    @NotBlank(message = "이름을 입력해주세요.")
    private String name; // 실명

    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

}
