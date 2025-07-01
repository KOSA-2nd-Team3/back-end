package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kosa.server.member.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(name = "MailSend", description = "메일 발송 요청 DTO")
public class MailSendDto {

    @Schema(description = "메일을 받을 멤버 목록", required = true)
    private List<Member> members;
    @Schema(description = "플랫폼 이름", example = "넷플릭스", required = true)
    private String platFormName;
    @Schema(description = "파티 인원 수", example = "4", required = true)
    private int partySize;
    @Schema(description = "게시글 ID", example = "1001", required = true)
    private Long postId;
}
