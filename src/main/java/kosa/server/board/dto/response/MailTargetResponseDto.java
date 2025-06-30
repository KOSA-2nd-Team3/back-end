package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(name = "MailTargetResponse", description = "메일 발송 대상 응답 DTO")
public class MailTargetResponseDto {

        @Schema(description = "수신자 이메일 주소", example = "user@example.com", required = true)
        private final String email;
        @Schema(description = "플랫폼 이름", example = "넷플릭스", required = true)
        private final String platformName;
        @Schema(description = "게시글 URL", required = true)
        private final String postUrl;
        @Schema(description = "파티 인원 수", example = "4", required = true)
        private final int partySize;

}
