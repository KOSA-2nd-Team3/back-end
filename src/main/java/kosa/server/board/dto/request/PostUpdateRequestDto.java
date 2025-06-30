package kosa.server.board.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Schema(name = "PostUpdateRequest", description = "게시글 수정 요청 DTO")
public class PostUpdateRequestDto {

    @Schema(description = "게시글 ID", example = "11", required = true)
    private Long postId;
    @Schema(description = "구독 기간", example = "6", required = true)
    private int durationMonth;

    @Schema(description = "호스트 이메일)", example = "host@example.com", required = true)
    @JsonProperty("email")
    private String hostId;
    @Schema(description = "호스트 비밀번호", example = "secret123!", required = true)
    @JsonProperty("password")
    private String hostPwd;

    @Builder
    public PostUpdateRequestDto(int durationMonth, String hostId, String hostPwd) {
        this.durationMonth = durationMonth;
        this.hostId = hostId;
        this.hostPwd = hostPwd;
    }
}
