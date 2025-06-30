package kosa.server.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/*
게시판 구인글 요청
*
*/
@Data
@Schema(name = "PostCreateRequest", description = "게시글 생성 요청 DTO")
public class PostCreateRequestDto {

    @Schema(description = "플랫폼 ID", example = "1", required = true)
    @NotEmpty(message = "플랫폼 아이디가 없습니다.")
    private Long platformId;

    @Schema(description = "최대 인원 수", example = "10", required = true)
    @NotEmpty(message = "최대 인원수는 1명 이상이어야 합니다.")
    private int capacity;

    @Schema(description = "현재 참여 인원 수", example = "2", required = true)
    @NotEmpty(message = "현재 인원수는 0 이상이어야 합니다.")
    private int currentCount;

    @Schema(description = "로그인 ID", example = "user123", required = true)
    @NotEmpty(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    @Schema(description = "구독 기간", example = "6", required = false)
    private int durationMonth;
    @Schema(description = "호스트 ID", example = "host123", required = false)
    private String hostId;
    @Schema(description = "호스트 비밀번호", example = "secretPwd", required = false)
    private String hostPwd;

}