package kosa.server.board.dto.response;

import kosa.server.member.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MailSendDto {
    private List<Member> members;
    private String platFormName;
    private int partySize;
    private Long postId;
}
