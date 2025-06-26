package kosa.server.chat.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PartyMemberResponseDto {
    private String loginId;
    private String leaderYn;
}
