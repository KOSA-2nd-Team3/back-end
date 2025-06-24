package kosa.server.board.dto.response;

import kosa.server.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyMemberDto {
    private Long memberId;
    private String nickName;
    private String isOwner;
    private LocalDateTime createdAt;
}
